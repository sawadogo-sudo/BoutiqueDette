package bf.amido.sawadogo.boutiquedette.adapters.api;

import java.util.List;

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

/**
 * Interface API simplifiée
 * Utilisez SupabaseApiService pour plus de fonctionnalités
 */
public interface ApiService {
    
    // ============ CLIENTS ============
    
    @GET("clients")
    Call<List<Client>> getClients();
    
    @GET("clients")
    Call<List<Client>> getClientsOrdered(@Query("order") String order);
    
    @GET("clients/{id}")
    Call<Client> getClientById(@Path("id") String id);
    
    @POST("clients")
    Call<Client> createClient(@Body Client client);
    
    @PUT("clients/{id}")
    Call<Client> updateClient(@Path("id") String id, @Body Client client);
    
    @DELETE("clients/{id}")
    Call<Void> deleteClient(@Path("id") String id);
    
    // ============ DETTES ============
    
    @GET("dettes")
    Call<List<Dette>> getDettes();
    
    @GET("dettes/client/{clientId}")
    Call<List<Dette>> getDettesByClient(@Path("clientId") String clientId);
    
    @GET("dettes/status/{status}")
    Call<List<Dette>> getDettesByStatus(@Path("status") String status);
    
    @POST("dettes")
    Call<Dette> createDette(@Body Dette dette);
    
    @PUT("dettes/{id}")
    Call<Dette> updateDette(@Path("id") String id, @Body Dette dette);
    
    @DELETE("dettes/{id}")
    Call<Void> deleteDette(@Path("id") String id);
    
    // ============ PAIEMENTS ============
    
    @GET("paiements")
    Call<List<Paiement>> getPaiements();
    
    @GET("paiements/dette/{detteId}")
    Call<List<Paiement>> getPaiementsByDette(@Path("detteId") String detteId);
    
    @POST("paiements")
    Call<Paiement> createPaiement(@Body Paiement paiement);
    
    @PUT("paiements/{id}")
    Call<Paiement> updatePaiement(@Path("id") String id, @Body Paiement paiement);
    
    // ============ STATISTIQUES ============
    
    @GET("stats/clients/count")
    Call<Integer> getClientsCount();
    
    @GET("stats/dettes/total")
    Call<Double> getTotalDettes();
    
    @GET("stats/dettes/status")
    Call<Object> getDettesByStatusCount();
    
    // ============ RECHERCHE ============
    
    @GET("clients/search")
    Call<List<Client>> searchClients(@Query("q") String query);
    
    @GET("dettes/search")
    Call<List<Dette>> searchDettes(@Query("q") String query);
}