package bf.amido.sawadogo.boutiquedette.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    
    private static Retrofit retrofit = null;
    
    public static Retrofit getClient() {
        if (retrofit == null) {
            // Créer un interceptor pour les logs
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // Créer le client HTTP
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(chain -> {
                        // Ajouter les headers nécessaires pour Supabase
                        return chain.proceed(chain.request()
                                .newBuilder()
                                .addHeader("apikey", "YOUR_SUPABASE_ANON_KEY")
                                .addHeader("Authorization", "Bearer YOUR_SUPABASE_ANON_KEY")
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Prefer", "return=representation")
                                .build());
                    })
                    .build();
            
            // Créer Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://YOUR_PROJECT_ID.supabase.co/rest/v1/")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    
    // Méthode pour créer le service API
    public static <T> T createService(Class<T> serviceClass) {
        return getClient().create(serviceClass);
    }
}