package bf.amido.sawadogo.boutiquedette.adapters.api;

import java.util.List;
import java.util.Map;

import bf.amido.sawadogo.boutiquedette.models.Client;
import bf.amido.sawadogo.boutiquedette.models.Dette;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface SupabaseApiService {
    
    // ============ AUTHENTIFICATION ============
    
    @Headers({
        "Content-Type: " + SupabaseConfig.CONTENT_TYPE_JSON,
        "Prefer: " + SupabaseConfig.PREFER_REPRESENTATION
    })
    @POST("auth/v1/token?grant_type=password")
    Call<Map<String, Object>> signIn(@Body Map<String, String> credentials);
    
    @Headers({
        "Content-Type: " + SupabaseConfig.CONTENT_TYPE_JSON,
        "Prefer: " + SupabaseConfig.PREFER_REPRESENTATION
    })
    @POST("auth/v1/signup")
    Call<Map<String, Object>> signUp(@Body Map<String, String> userData);
    
    // ============ CLIENTS ============
    
    @GET(SupabaseConfig.TABLE_CLIENTS)
    Call<List<Client>> getAllClients(
        @Query(SupabaseConfig.QUERY_SELECT) String select,
        @Query(SupabaseConfig.QUERY_ORDER) String order
    );
    
    @GET(SupabaseConfig.TABLE_CLIENTS)
    Call<List<Client>> getClientById(
        @Query("id") String eq,
        @Query(SupabaseConfig.QUERY_SELECT) String select
    );
    
    @GET(SupabaseConfig.TABLE_CLIENTS)
    Call<List<Client>> searchClients(
        @Query("nom") String ilike,
        @Query(SupabaseConfig.QUERY_SELECT) String select
    );
    
    @Headers({
        "Content-Type: " + SupabaseConfig.CONTENT_TYPE_JSON,
        "Prefer: " + SupabaseConfig.PREFER_REPRESENTATION
    })
    @POST(SupabaseConfig.TABLE_CLIENTS)
    Call<List<Client>> createClient(@Body Client client);
    
    @Headers({
        "Content-Type: " + SupabaseConfig.CONTENT_TYPE_JSON,
        "Prefer: " + SupabaseConfig.PREFER_MINIMAL
    })
    @PUT(SupabaseConfig.TABLE_CLIENTS)
    Call<Void> updateClient(
        @Query("id") String eq,
        @Body Client client
    );
    
    @Headers({
        "Prefer: " + SupabaseConfig.PREFER_MINIMAL
    })
    @DELETE(SupabaseConfig.TABLE_CLIENTS)
    Call<Void> deleteClient(@Query("id") String eq);
    
    // ============ DETTES ============
    
    @GET(SupabaseConfig.TABLE_DETTES)
    Call<List<Dette>> getAllDettes(
        @Query(SupabaseConfig.QUERY_SELECT) String select,
        @Query(SupabaseConfig.QUERY_ORDER) String order
    );
    
    @GET(SupabaseConfig.TABLE_DETTES)
    Call<List<Dette>> getDettesByClient(
        @Query("client_id") String eq,
        @Query(SupabaseConfig.QUERY_SELECT) String select,
        @Query(SupabaseConfig.QUERY_ORDER) String order
    );
    
    @Headers({
        "Content-Type: " + SupabaseConfig.CONTENT_TYPE_JSON,
        "Prefer: " + SupabaseConfig.PREFER_REPRESENTATION
    })
    @POST(SupabaseConfig.TABLE_DETTES)
    Call<List<Dette>> createDette(@Body Dette dette);
    
    @Headers({
        "Content-Type: " + SupabaseConfig.CONTENT_TYPE_JSON,
        "Prefer: " + SupabaseConfig.PREFER_MINIMAL
    })
    @PUT(SupabaseConfig.TABLE_DETTES)
    Call<Void> updateDette(
        @Query("id") String eq,
        @Body Dette dette
    );
    
    @Headers({
        "Prefer: " + SupabaseConfig.PREFER_MINIMAL
    })
    @DELETE(SupabaseConfig.TABLE_DETTES)
    Call<Void> deleteDette(@Query("id") String eq);
    
    @GET("dettes")
Call<List<Dette>> getDetteById(
    @Query("id") String id,
    @Query("select") String select
);
}