package bf.amido.sawadogo.boutiquedette.api;

import bf.amido.sawadogo.boutiquedette.models.Client;
import bf.amido.sawadogo.boutiquedette.models.Dette;
import bf.amido.sawadogo.boutiquedette.models.Paiement;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import java.util.List;

public interface ApiService {
    
    // Clients
    @GET("clients")
    Call<List<Client>> getAllClients();
    
    @GET("clients")
    Call<List<Client>> searchClients(@Query("nom") String nom);
    
    @POST("clients")
    Call<Client> createClient(@Body Client client);
    
    @PUT("clients/{id}")
    Call<Client> updateClient(@Path("id") String id, @Body Client client);
    
    @DELETE("clients/{id}")
    Call<Void> deleteClient(@Path("id") String id);
    
    // Dettes
    @GET("dettes")
    Call<List<Dette>> getDettesByClient(@Query("client_id") String clientId);
    
    @POST("dettes")
    Call<Dette> createDette(@Body Dette dette);
    
    // Paiements
    @GET("paiements")
    Call<List<Paiement>> getPaiementsByClient(@Query("client_id") String clientId);
    
    @POST("paiements")
    Call<Paiement> createPaiement(@Body Paiement paiement);
}