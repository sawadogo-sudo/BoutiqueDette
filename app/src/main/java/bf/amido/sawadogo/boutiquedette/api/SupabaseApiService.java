package bf.amido.sawadogo.boutiquedette.api;

import java.util.List;

import bf.amido.sawadogo.boutiquedette.models.Client;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SupabaseApiService {
    
    // Opérations pour les clients
    @GET("clients")
    Call<List<Client>> getClients();
    
    @GET("clients?select=*")
    Call<List<Client>> getClientsWithAllFields();
    
    @GET("clients?order=created_at.desc")
    Call<List<Client>> getClientsOrderedByDate();
    
    @GET("clients")
    Call<List<Client>> searchClientsByName(@Query("nom") String query);
    
    @POST("clients")
    Call<Client> createClient(@Body Client client);
    
    @PUT("clients?id=eq.{id}")
    Call<Client> updateClient(@Path("id") String id, @Body Client client);
    
    @DELETE("clients?id=eq.{id}")
    Call<Void> deleteClient(@Path("id") String id);
}