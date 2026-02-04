package bf.amido.sawadogo.boutiquedette.adapters.api;

import android.content.Context;
import android.content.SharedPreferences;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    
    private static Retrofit retrofit = null;
    private static Retrofit authRetrofit = null;
    
    private static final String PREF_NAME = "supabase_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    
    // Méthode principale pour obtenir le service Supabase
    public static SupabaseApiService getSupabaseService(Context context) {
        return getClient(context).create(SupabaseApiService.class);
    }
    
    // Méthode alternative pour obtenir le service Api (si vous l'utilisez)
    public static ApiService getApiService(Context context) {
        return getClient(context).create(ApiService.class);
    }
    
    // Client pour l'API REST (données)
    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            // Récupérer le token d'authentification
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String accessToken = prefs.getString(KEY_ACCESS_TOKEN, "");
            
            // Créer un interceptor pour les logs (désactivez en production)
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // Créer le client HTTP
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(chain -> {
                        // Utiliser le token d'authentification si disponible, sinon la clé API
                        String authToken = !accessToken.isEmpty() ? 
                                "Bearer " + accessToken : 
                                "Bearer " + SupabaseConfig.API_KEY;
                        
                        return chain.proceed(chain.request()
                                .newBuilder()
                                .addHeader(SupabaseConfig.HEADER_API_KEY, SupabaseConfig.API_KEY)
                                .addHeader(SupabaseConfig.HEADER_AUTHORIZATION, authToken)
                                .addHeader(SupabaseConfig.HEADER_CONTENT_TYPE, "application/json")
                                .addHeader(SupabaseConfig.HEADER_PREFER, "return=representation")
                                .build());
                    })
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            
            // Créer Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(SupabaseConfig.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    
    // Client pour l'authentification
    public static Retrofit getAuthClient() {
        if (authRetrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        return chain.proceed(chain.request()
                                .newBuilder()
                                .addHeader(SupabaseConfig.HEADER_API_KEY, SupabaseConfig.API_KEY)
                                .addHeader(SupabaseConfig.HEADER_CONTENT_TYPE, "application/json")
                                .build());
                    })
                    .build();
            
            authRetrofit = new Retrofit.Builder()
                    .baseUrl(SupabaseConfig.AUTH_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return authRetrofit;
    }
    
    // Méthode générique pour créer un service
    public static <T> T createService(Class<T> serviceClass, Context context) {
        return getClient(context).create(serviceClass);
    }
    
    // ============ GESTION DE L'AUTHENTIFICATION ============
    
    public static void saveAuthData(Context context, String accessToken, String refreshToken, String userId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }
    
    public static String getAccessToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_ACCESS_TOKEN, "");
    }
    
    public static String getRefreshToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_REFRESH_TOKEN, "");
    }
    
    public static String getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_ID, "");
    }
    
    public static boolean isLoggedIn(Context context) {
        return !getAccessToken(context).isEmpty();
    }
    
    public static void clearAuthData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_REFRESH_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.apply();
    }
    
    // ============ MÉTHODES UTILITAIRES ============
    
    public static String getFullUrl(String endpoint) {
        return SupabaseConfig.BASE_URL + endpoint;
    }
    
    public static String getAuthUrl(String endpoint) {
        return SupabaseConfig.AUTH_URL + endpoint;
    }
}