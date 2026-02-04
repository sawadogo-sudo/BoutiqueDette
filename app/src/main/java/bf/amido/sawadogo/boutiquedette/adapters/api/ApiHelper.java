package bf.amido.sawadogo.boutiquedette.adapters.api;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bf.amido.sawadogo.boutiquedette.models.Client;
import bf.amido.sawadogo.boutiquedette.models.Dette;
import bf.amido.sawadogo.boutiquedette.models.Paiement;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiHelper {
    
    private static final String TAG = "ApiHelper";
    private Context context;
    private OkHttpClient okHttpClient;
    private Gson gson;
    private String baseUrl;
    private String supabaseKey;
    
    public ApiHelper(Context context) {
        this.context = context;
        this.okHttpClient = new OkHttpClient();
        
        this.gson = new GsonBuilder()
            .setLenient()
            .create();
        
        this.baseUrl = "https://rcsqmtihjrdpaxwzejle.supabase.co/rest/v1/";
        this.supabaseKey = "sb_publishable_Ljtlgo608Ij4NKJaGPpJwg_WaKPrKdJ";
        
        Log.d(TAG, "ApiHelper initialisé");
    }
    
    // ============ MÉTHODES CLIENTS ============
    
    public void getAllClients(DataCallback<List<Client>> callback) {
        Log.d(TAG, "getAllClients appelé");
        
        Request request = new Request.Builder()
            .url(baseUrl + "clients?order=created_at.desc")
            .get()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .addHeader("Accept", "application/json")
            .build();
        
        executeRequest(request, new TypeToken<List<Client>>(){}.getType(), callback);
    }
    
    public void getClientById(String id, DataCallback<Client> callback) {
        Log.d(TAG, "getClientById appelé avec ID: " + id);
        
        if (id == null || id.isEmpty()) {
            callback.onError("ID client invalide");
            return;
        }
        
        Request request = new Request.Builder()
            .url(baseUrl + "clients?id=eq." + id + "&limit=1")
            .get()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .addHeader("Accept", "application/json")
            .build();
        
        executeSingleItemRequest(request, new TypeToken<List<Client>>(){}.getType(), callback);
    }
    
    public void searchClients(String query, DataCallback<List<Client>> callback) {
        Log.d(TAG, "searchClients appelé avec query: " + query);
        
        if (query == null || query.trim().isEmpty()) {
            getAllClients(callback);
            return;
        }
        
        String encodedQuery = query.trim().replace(" ", "%20");
        String url = baseUrl + "clients?or=(nom.ilike.*" + encodedQuery + 
                    "*,prenom.ilike.*" + encodedQuery + 
                    "*,telephone.ilike.*" + encodedQuery + "*)";
        
        Request request = new Request.Builder()
            .url(url)
            .get()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .addHeader("Accept", "application/json")
            .build();
        
        executeRequest(request, new TypeToken<List<Client>>(){}.getType(), callback);
    }
    
    public void createClient(Client client, DataCallback<Client> callback) {
        Log.d(TAG, "createClient appelé");
        
        try {
            if (client.getNom() == null || client.getNom().trim().isEmpty()) {
                callback.onError("Le nom est obligatoire");
                return;
            }
            
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("nom", client.getNom().trim());
            
            if (client.getTelephone() != null && !client.getTelephone().trim().isEmpty()) {
                jsonObject.addProperty("telephone", client.getTelephone().trim());
            }
            if (client.getPrenom() != null && !client.getPrenom().trim().isEmpty()) {
                jsonObject.addProperty("prenom", client.getPrenom().trim());
            }
            if (client.getEmail() != null && !client.getEmail().trim().isEmpty()) {
                jsonObject.addProperty("email", client.getEmail().trim());
            }
            if (client.getVille() != null && !client.getVille().trim().isEmpty()) {
                jsonObject.addProperty("ville", client.getVille().trim());
            }
            if (client.getAdresse() != null && !client.getAdresse().trim().isEmpty()) {
                jsonObject.addProperty("adresse", client.getAdresse().trim());
            }
            
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), 
                jsonObject.toString()
            );
            
            Request request = new Request.Builder()
                .url(baseUrl + "clients")
                .post(body)
                .addHeader("apikey", supabaseKey)
                .addHeader("Authorization", "Bearer " + supabaseKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .addHeader("Accept", "application/json")
                .build();
            
            executeSingleItemRequest(request, new TypeToken<List<Client>>(){}.getType(), callback);
            
        } catch (Exception e) {
            Log.e(TAG, "Exception createClient");
            callback.onError("Erreur création " );
        }
    }
    
    public void updateClient(String id, Client client, SimpleCallback callback) {
        Log.d(TAG, "updateClient appelé pour ID: " + id);
        
        if (id == null || id.isEmpty()) {
            callback.onError("ID client invalide");
            return;
        }
        
        try {
            JsonObject jsonObject = new JsonObject();
            
            if (client.getNom() != null && !client.getNom().trim().isEmpty()) {
                jsonObject.addProperty("nom", client.getNom().trim());
            }
            if (client.getPrenom() != null) {
                jsonObject.addProperty("prenom", client.getPrenom().trim());
            }
            if (client.getTelephone() != null && !client.getTelephone().trim().isEmpty()) {
                jsonObject.addProperty("telephone", client.getTelephone().trim());
            }
            if (client.getEmail() != null) {
                jsonObject.addProperty("email", client.getEmail().trim());
            }
            if (client.getVille() != null) {
                jsonObject.addProperty("ville", client.getVille().trim());
            }
            if (client.getAdresse() != null) {
                jsonObject.addProperty("adresse", client.getAdresse().trim());
            }
            
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), 
                jsonObject.toString()
            );
            
            Request request = new Request.Builder()
                .url(baseUrl + "clients?id=eq." + id)
                .patch(body)
                .addHeader("apikey", supabaseKey)
                .addHeader("Authorization", "Bearer " + supabaseKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .addHeader("Accept", "application/json")
                .build();
            
            executeSimpleRequest(request, callback);
            
        } catch (Exception e) {
            Log.e(TAG, "Exception updateClient: ");
            callback.onError("Erreur mise à jour");
        }
    }
    
    public void deleteClient(String id, SimpleCallback callback) {
        Log.d(TAG, "deleteClient appelé pour ID: " + id);
        
        if (id == null || id.isEmpty()) {
            callback.onError("ID client invalide");
            return;
        }
        
        try {
            Request request = new Request.Builder()
                .url(baseUrl + "clients?id=eq." + id)
                .delete()
                .addHeader("apikey", supabaseKey)
                .addHeader("Authorization", "Bearer " + supabaseKey)
                .addHeader("Prefer", "return=minimal")
                .addHeader("Accept", "application/json")
                .build();
            
            executeSimpleRequest(request, callback);
            
        } catch (Exception e) {
            Log.e(TAG, "Exception deleteClient " );
            callback.onError("Erreur suppression " );
        }
    }
    
    // ============ MÉTHODES DETTES ============
    
    public void getAllDettesWithClientInfo(DataCallback<List<Dette>> callback) {
        Log.d(TAG, "getAllDettesWithClientInfo appelé");
        
        String url = baseUrl + "dettes?" +
                     "select=*,clients:client_id(id,nom,prenom,telephone,email,ville,adresse)" +
                     "&order=created_at.desc";
        
        Request request = new Request.Builder()
            .url(url)
            .get()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .addHeader("Accept", "application/json")
            .build();
        
        executeRequest(request, new TypeToken<List<Dette>>(){}.getType(), callback);
    }
    
    public void getAllDettes(DataCallback<List<Dette>> callback) {
        Log.d(TAG, "getAllDettes appelé");
        
        Request request = new Request.Builder()
            .url(baseUrl + "dettes?order=created_at.desc")
            .get()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .addHeader("Accept", "application/json")
            .build();
        
        executeRequest(request, new TypeToken<List<Dette>>(){}.getType(), callback);
    }
    
    public void getDettesByClientWithInfo(String clientId, DataCallback<List<Dette>> callback) {
        Log.d(TAG, "getDettesByClientWithInfo appelé pour clientId: " + clientId);
        
        if (clientId == null || clientId.isEmpty()) {
            callback.onError("ID client invalide");
            return;
        }
        
        String url = baseUrl + "dettes?" +
                     "select=*,clients:client_id(id,nom,prenom,telephone)" +
                     "&client_id=eq." + clientId + 
                     "&order=date_dette.desc";
        
        Request request = new Request.Builder()
            .url(url)
            .get()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .addHeader("Accept", "application/json")
            .build();
        
        executeRequest(request, new TypeToken<List<Dette>>(){}.getType(), callback);
    }
    
    public void getDettesByClient(String clientId, DataCallback<List<Dette>> callback) {
        Log.d(TAG, "getDettesByClient appelé pour clientId: " + clientId);
        
        if (clientId == null || clientId.isEmpty()) {
            callback.onError("ID client invalide");
            return;
        }
        
        Request request = new Request.Builder()
            .url(baseUrl + "dettes?client_id=eq." + clientId + "&order=date_dette.desc")
            .get()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .addHeader("Accept", "application/json")
            .build();
        
        executeRequest(request, new TypeToken<List<Dette>>(){}.getType(), callback);
    }
    
    public void createDette(Dette dette, DataCallback<Dette> callback) {
        Log.d(TAG, "createDette appelé");
        
        try {
            if (dette.getClientId() == null || dette.getClientId().isEmpty()) {
                callback.onError("ID client requis");
                return;
            }
            
            if (dette.getMontant() <= 0) {
                callback.onError("Le montant doit être supérieur à 0");
                return;
            }
            
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("client_id", dette.getClientId());
            jsonObject.addProperty("montant", dette.getMontant());
            jsonObject.addProperty("user_id", getCurrentUserId());
            
            // Initialiser les champs de paiement
            jsonObject.addProperty("montant_restant", dette.getMontant());
            jsonObject.addProperty("montant_paye", 0.0);
            
            if (dette.getDescription() != null && !dette.getDescription().trim().isEmpty()) {
                jsonObject.addProperty("description", dette.getDescription().trim());
            }
            
            if (dette.getDateDette() != null && !dette.getDateDette().trim().isEmpty()) {
                jsonObject.addProperty("date_dette", dette.getDateDette().trim());
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String today = sdf.format(new Date());
                jsonObject.addProperty("date_dette", today);
            }
            
            if (dette.getDateEcheance() != null && !dette.getDateEcheance().trim().isEmpty()) {
                jsonObject.addProperty("date_echeance", dette.getDateEcheance().trim());
            }
            
            if (dette.getStatut() != null && !dette.getStatut().trim().isEmpty()) {
                jsonObject.addProperty("statut", dette.getStatut().trim());
            } else {
                jsonObject.addProperty("statut", "en_cours");
            }
            
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), 
                jsonObject.toString()
            );
            
            Request request = new Request.Builder()
                .url(baseUrl + "dettes")
                .post(body)
                .addHeader("apikey", supabaseKey)
                .addHeader("Authorization", "Bearer " + supabaseKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .addHeader("Accept", "application/json")
                .build();
            
            executeSingleItemRequest(request, new TypeToken<List<Dette>>(){}.getType(), callback);
            
        } catch (Exception e) {
            Log.e(TAG, "Exception createDette");
            callback.onError("Erreur création dette");
        }
    }
    
    public void updateDette(String id, Dette dette, SimpleCallback callback) {
        Log.d(TAG, "updateDette appelé pour ID: " + id);
        
        if (id == null || id.isEmpty()) {
            callback.onError("ID dette invalide");
            return;
        }
        
        try {
            JsonObject jsonObject = new JsonObject();
            
            if (dette.getClientId() != null && !dette.getClientId().isEmpty()) {
                jsonObject.addProperty("client_id", dette.getClientId());
            }
            
            if (dette.getMontant() > 0) {
                jsonObject.addProperty("montant", dette.getMontant());
            }
            
            if (dette.getDescription() != null) {
                jsonObject.addProperty("description", dette.getDescription().trim());
            }
            if (dette.getDateDette() != null && !dette.getDateDette().isEmpty()) {
                jsonObject.addProperty("date_dette", dette.getDateDette().trim());
            }
            if (dette.getDateEcheance() != null && !dette.getDateEcheance().isEmpty()) {
                jsonObject.addProperty("date_echeance", dette.getDateEcheance().trim());
            }
            if (dette.getStatut() != null && !dette.getStatut().isEmpty()) {
                jsonObject.addProperty("statut", dette.getStatut().trim());
            }
            
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), 
                jsonObject.toString()
            );
            
            Request request = new Request.Builder()
                .url(baseUrl + "dettes?id=eq." + id)
                .patch(body)
                .addHeader("apikey", supabaseKey)
                .addHeader("Authorization", "Bearer " + supabaseKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .addHeader("Accept", "application/json")
                .build();
            
            executeSimpleRequest(request, callback);
            
        } catch (Exception e) {
            Log.e(TAG, "Exception updateDette: ");
            callback.onError("Erreur mise à jour dette: " );
        }
    }
    
    public void deleteDette(String id, SimpleCallback callback) {
        Log.d(TAG, "deleteDette appelé pour ID: " + id);
        
        if (id == null || id.isEmpty()) {
            callback.onError("ID dette invalide");
            return;
        }
        
        try {
            Request request = new Request.Builder()
                .url(baseUrl + "dettes?id=eq." + id)
                .delete()
                .addHeader("apikey", supabaseKey)
                .addHeader("Authorization", "Bearer " + supabaseKey)
                .addHeader("Prefer", "return=minimal")
                .addHeader("Accept", "application/json")
                .build();
            
            executeSimpleRequest(request, callback);
            
        } catch (Exception e) {
            Log.e(TAG, "Exception deleteDette: ");
            callback.onError("Erreur suppression dette: " );
        }
    }
    
    public void getDetteById(String detteId, DataCallback<Dette> callback) {
        Log.d(TAG, "getDetteById appelé pour detteId: " + detteId);
        
        if (detteId == null || detteId.isEmpty()) {
            callback.onError("ID dette invalide");
            return;
        }
        
        Request request = new Request.Builder()
            .url(baseUrl + "dettes?id=eq." + detteId + "&limit=1")
            .get()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .addHeader("Accept", "application/json")
            .build();
        
        executeSingleItemRequest(request, new TypeToken<List<Dette>>(){}.getType(), callback);
    }
    
    public void getDetteWithClientInfo(String detteId, DataCallback<Dette> callback) {
        Log.d(TAG, "getDetteWithClientInfo appelé pour detteId: " + detteId);
        
        if (detteId == null || detteId.isEmpty()) {
            callback.onError("ID dette invalide");
            return;
        }
        
        String url = baseUrl + "dettes?" +
                     "select=*,clients:client_id(id,nom,prenom,telephone)" +
                     "&id=eq." + detteId + 
                     "&limit=1";
        
        Request request = new Request.Builder()
            .url(url)
            .get()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .addHeader("Accept", "application/json")
            .build();
        
        executeSingleItemRequest(request, new TypeToken<List<Dette>>(){}.getType(), callback);
    }
    
    // MÉTHODE AJOUTÉE POUR CORRIGER L'ERREUR
    public void updateDetteStatut(String detteId, String nouveauStatut, SimpleCallback callback) {
        Log.d(TAG, "updateDetteStatut appelé pour detteId: " + detteId + ", statut: " + nouveauStatut);
        
        if (detteId == null || detteId.isEmpty()) {
            callback.onError("ID dette invalide");
            return;
        }
        
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("statut", nouveauStatut);
            
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), 
                jsonObject.toString()
            );
            
            Request request = new Request.Builder()
                .url(baseUrl + "dettes?id=eq." + detteId)
                .patch(body)
                .addHeader("apikey", supabaseKey)
                .addHeader("Authorization", "Bearer " + supabaseKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .addHeader("Accept", "application/json")
                .build();
            
            executeSimpleRequest(request, callback);
            
        } catch (Exception e) {
            Log.e(TAG, "Exception updateDetteStatut: " );
            callback.onError("Erreur mise à jour statut: " );
        }
    }
    
    // ============ MÉTHODES PAIEMENTS ============
    
    public void getAllPaiements(DataCallback<List<Paiement>> callback) {
        Log.d(TAG, "getAllPaiements appelé");
        
        Request request = new Request.Builder()
            .url(baseUrl + "paiements?order=date_paiement.desc")
            .get()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .addHeader("Accept", "application/json")
            .build();
        
        executeRequest(request, new TypeToken<List<Paiement>>(){}.getType(), callback);
    }
    
    public void createPaiement(Paiement paiement, DataCallback<Paiement> callback) {
        Log.d(TAG, "createPaiement appelé");
        
        try {
            if (paiement == null) {
                callback.onError("Paiement est null");
                return;
            }
            
            if (paiement.getDetteId() == null || paiement.getDetteId().isEmpty()) {
                callback.onError("ID dette est requis");
                return;
            }
            
            if (paiement.getClientId() == null || paiement.getClientId().isEmpty()) {
                callback.onError("Erreur: ID client manquant dans le paiement.");
                return;
            }
            
            if (paiement.getMontant() <= 0) {
                callback.onError("Le montant doit être supérieur à 0");
                return;
            }
            
            JsonObject jsonObject = new JsonObject();
            
            jsonObject.addProperty("dette_id", paiement.getDetteId());
            jsonObject.addProperty("client_id", paiement.getClientId());
            jsonObject.addProperty("montant", paiement.getMontant());
            
            String userId = getCurrentUserId();
            if (userId != null && !userId.isEmpty() && !userId.equals("default_user")) {
                jsonObject.addProperty("user_id", userId);
            }
            
            if (paiement.getDatePaiement() != null && !paiement.getDatePaiement().trim().isEmpty()) {
                jsonObject.addProperty("date_paiement", paiement.getDatePaiement().trim());
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String today = sdf.format(new Date());
                jsonObject.addProperty("date_paiement", today);
            }
            
            if (paiement.getModePaiement() != null && !paiement.getModePaiement().trim().isEmpty()) {
                jsonObject.addProperty("mode_paiement", paiement.getModePaiement().trim());
            } else {
                jsonObject.addProperty("mode_paiement", "Espèces");
            }
            
            if (paiement.getReference() != null && !paiement.getReference().trim().isEmpty()) {
                jsonObject.addProperty("reference", paiement.getReference().trim());
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                String reference = "PAY-" + sdf.format(new Date()) + "-" + (int)(Math.random() * 1000);
                jsonObject.addProperty("reference", reference);
            }
            
            if (paiement.getDescription() != null && !paiement.getDescription().trim().isEmpty()) {
                jsonObject.addProperty("description", paiement.getDescription().trim());
            } else {
                jsonObject.addProperty("description", "Paiement de dette #" + paiement.getDetteId());
            }
            
            String jsonString = jsonObject.toString();
            Log.d(TAG, "Envoi paiement JSON: " + jsonString);
            
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), 
                jsonString
            );
            
            Request request = new Request.Builder()
                .url(baseUrl + "paiements")
                .post(body)
                .addHeader("apikey", supabaseKey)
                .addHeader("Authorization", "Bearer " + supabaseKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .addHeader("Accept", "application/json")
                .build();
            
            executeSingleItemRequest(request, new TypeToken<List<Paiement>>(){}.getType(), callback);
            
        } catch (Exception e) {
            Log.e(TAG, "Exception createPaiement: " );
            callback.onError("Erreur création paiement: " );
        }
    }
    
    // Méthode simplifiée pour créer un paiement
    public void effectuerPaiementComplet(Paiement paiement, SimpleCallback callback) {
        Log.d(TAG, "effectuerPaiementComplet appelé");
        
        if (paiement == null || !paiement.isValid()) {
            callback.onError("Paiement invalide");
            return;
        }
        
        // Créer le paiement
        createPaiement(paiement, new DataCallback<Paiement>() {
            @Override
            public void onSuccess(Paiement paiementCree) {
                Log.d(TAG, "Paiement créé avec succès, ID: " + paiementCree.getId());
                
                // Mettre à jour le statut de la dette
                updateDetteStatut(paiement.getDetteId(), "payé", new SimpleCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Log.d(TAG, "Dette mise à jour avec succès: " + message);
                        callback.onSuccess("Paiement effectué avec succès");
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Erreur mise à jour dette: " + error);
                        callback.onSuccess("Paiement enregistré mais erreur mise à jour dette: " + error);
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Erreur création paiement: " + error);
                callback.onError("Erreur lors du paiement: " + error);
            }
        });
    }
    
    public void getPaiementsByDetteId(String detteId, DataCallback<List<Paiement>> callback) {
        Log.d(TAG, "getPaiementsByDetteId appelé pour detteId: " + detteId);
        
        if (detteId == null || detteId.isEmpty()) {
            callback.onError("ID dette invalide");
            return;
        }
        
        Request request = new Request.Builder()
            .url(baseUrl + "paiements?dette_id=eq." + detteId + "&order=date_paiement.desc")
            .get()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .addHeader("Accept", "application/json")
            .build();
        
        executeRequest(request, new TypeToken<List<Paiement>>(){}.getType(), callback);
    }
    
    public void getPaiementsByClientId(String clientId, DataCallback<List<Paiement>> callback) {
        Log.d(TAG, "getPaiementsByClientId appelé pour clientId: " + clientId);
        
        if (clientId == null || clientId.isEmpty()) {
            callback.onError("ID client invalide");
            return;
        }
        
        Request request = new Request.Builder()
            .url(baseUrl + "paiements?client_id=eq." + clientId + "&order=date_paiement.desc")
            .get()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .addHeader("Accept", "application/json")
            .build();
        
        executeRequest(request, new TypeToken<List<Paiement>>(){}.getType(), callback);
    }
    
    // ============ MÉTHODES GÉNÉRIQUES ============
    
    private <T> void executeRequest(Request request, Type type, DataCallback<T> callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = null;
                try {
                    responseBody = response.body() != null ? 
                        response.body().string() : "null";
                    
                    Log.d(TAG, "Réponse ");
                    
                    if (response.isSuccessful()) {
                        try {
                            T data = gson.fromJson(responseBody, type);
                            callback.onSuccess(data);
                        } catch (JsonSyntaxException e) {
                            Log.e(TAG, "Erreur parsing JSON");
                            callback.onError("Erreur format données JSON");
                        }
                    } else {
                        String errorMsg = parseError(responseBody, response.code());
                        Log.e(TAG, "Erreur ");
                        callback.onError("Erreur " );
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Erreur traitement réponse: " );
                    callback.onError("Erreur traitement: ");
                } finally {
                    if (response != null && response.body() != null) {
                        response.body().close();
                    }
                }
            }
            
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "probleme de connectionn ");
                callback.onError("probleme de connection: ");
            }
        });
    }
    
    private <T> void executeSingleItemRequest(Request request, Type listType, DataCallback<T> callback) {
        executeRequest(request, listType, new DataCallback<List<T>>() {
            @Override
            public void onSuccess(List<T> items) {
                if (items != null && !items.isEmpty()) {
                    callback.onSuccess(items.get(0));
                } else {
                    callback.onError("Aucun élément trouvé");
                }
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    private void executeSimpleRequest(Request request, SimpleCallback callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = null;
                try {
                    responseBody = response.body() != null ? 
                        response.body().string() : "null";
                    
                    Log.d(TAG, "Réponse simple " + request.url() + ": " + response.code());
                    
                    if (response.isSuccessful()) {
                        callback.onSuccess("Opération réussie");
                    } else {
                        String errorMsg = parseError(responseBody, response.code());
                        callback.onError("Erreur " );
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Erreur traitement réponse");
                    callback.onError("Erreur ");
                } finally {
                    if (response.body() != null) {
                        response.body().close();
                    }
                }
            }
            
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Erreur réseau " );
                callback.onError("Erreur réseau: " );
            }
        });
    }
    
    // ============ UTILITAIRES ============
    
    private String parseError(String responseBody, int statusCode) {
        try {
            if (responseBody == null || responseBody.isEmpty() || responseBody.equals("null")) {
                return "Réponse vide (" + statusCode + ")";
            }
            
            JsonObject json = gson.fromJson(responseBody, JsonObject.class);
            
            if (json.has("message")) {
                return json.get("message").getAsString();
            }
            if (json.has("error")) {
                return json.get("error").getAsString();
            }
            if (json.has("details")) {
                return json.get("details").getAsString();
            }
            
            return "Erreur " + statusCode + ": " + 
                   (responseBody.length() > 100 ? responseBody.substring(0, 100) + "..." : responseBody);
            
        } catch (Exception e) {
            return "Erreur " + statusCode + ": " + 
                   (responseBody.length() > 100 ? responseBody.substring(0, 100) + "..." : responseBody);
        }
    }
    
    public String getCurrentUserId() {
        String userId = "default_user";
        try {
            if (context != null) {
                userId = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                    .getString("user_id", "default_user");
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur récupération user ID: " + e.getMessage());
        }
        return userId;
    }
    
    public static double safeParseDouble(String montantStr) {
        return safeParseDouble(montantStr, 0.0);
    }
    
    public static double safeParseDouble(String montantStr, double defaultValue) {
        if (montantStr == null || montantStr.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            String cleaned = montantStr.replace(',', '.').trim();
            cleaned = cleaned.replaceAll("[^\\d.-]", "");
            
            if (cleaned.isEmpty()) {
                return defaultValue;
            }
            
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Erreur conversion montant: '" + montantStr + "'");
            return defaultValue;
        }
    }
    
    // ============ INTERFACES ============
    
    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }
    
    public interface SimpleCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}