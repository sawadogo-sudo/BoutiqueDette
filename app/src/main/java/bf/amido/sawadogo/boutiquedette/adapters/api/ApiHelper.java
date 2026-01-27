package bf.amido.sawadogo.boutiquedette.api;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
        this.gson = new Gson();
        
        // Configuration Supabase - IMPORTANT: Vérifiez ces valeurs
        this.baseUrl = "https://rcsqmtihjrdpaxwzejle.supabase.co/rest/v1/";
        this.supabaseKey = "sb_publishable_Ljtlgo608Ij4NKJaGPpJwg_WaKPrKdJ";
        
        Log.d(TAG, "=============================================");
        Log.d(TAG, "ApiHelper initialisé");
        Log.d(TAG, "URL: " + baseUrl);
        Log.d(TAG, "Clé API présente: " + (supabaseKey != null && !supabaseKey.isEmpty()));
        Log.d(TAG, "=============================================");
        
        // Tester la connexion automatiquement
        testConnectionSilently();
    }
    
    // ============ MÉTHODES DE TEST ============
    
    public void testConnection(SimpleCallback callback) {
        Log.d(TAG, "Test connexion Supabase...");
        
        Request request = new Request.Builder()
            .url(baseUrl + "clients?limit=1")
            .get()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .build();
        
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body() != null ? response.body().string() : "null";
                Log.d(TAG, "Test connexion: Code " + response.code() + ", Body: " + 
                      (body.length() > 200 ? body.substring(0, 200) + "..." : body));
                
                if (response.isSuccessful()) {
                    callback.onSuccess("Connexion OK - Code: " + response.code());
                } else {
                    String errorMsg = parseSupabaseError(body, response.code());
                    callback.onError("Échec " + response.code() + ": " + errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Test connexion échec: " + e.getMessage());
                callback.onError("Erreur réseau: " + e.getMessage());
            }
        });
    }
    
    private void testConnectionSilently() {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                    .url(baseUrl)
                    .head()
                    .addHeader("apikey", supabaseKey)
                    .addHeader("Authorization", "Bearer " + supabaseKey)
                    .build();
                
                Response response = okHttpClient.newCall(request).execute();
                Log.d(TAG, "Test auto connexion: " + response.code());
            } catch (Exception e) {
                Log.e(TAG, "Test auto connexion échoué: " + e.getMessage());
            }
        }).start();
    }
    
    // ============ CLIENTS ============
    
    public void getAllClients(DataCallback<List<Client>> callback) {
        Log.d(TAG, "getAllClients appelé");
        
        Request request = new Request.Builder()
            .url(baseUrl + "clients?order=created_at.desc")
            .get()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .build();
        
        executeOkHttpRequest(request, new TypeToken<List<Client>>(){}.getType(), callback);
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
            .build();
        
        executeOkHttpRequest(request, new TypeToken<List<Client>>(){}.getType(), callback);
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
                .build();
            
            executeSingleItemRequest(request, new TypeToken<List<Client>>(){}.getType(), callback);
            
        } catch (Exception e) {
            Log.e(TAG, "Exception createClient: " + e.getMessage());
            callback.onError("Erreur création: " + e.getMessage());
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
                .build();
            
            executeSimpleRequest(request, callback);
            
        } catch (Exception e) {
            Log.e(TAG, "Exception updateClient: " + e.getMessage());
            callback.onError("Erreur mise à jour: " + e.getMessage());
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
                .build();
            
            executeSimpleRequest(request, callback);
            
        } catch (Exception e) {
            Log.e(TAG, "Exception deleteClient: " + e.getMessage());
            callback.onError("Erreur suppression: " + e.getMessage());
        }
    }
    
    // ============ DETTES ============
    
    public void getAllDettes(DataCallback<List<Dette>> callback) {
        Log.d(TAG, "getAllDettes appelé");
        
        Request request = new Request.Builder()
            .url(baseUrl + "dettes?order=created_at.desc")
            .get()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .build();
        
        executeOkHttpRequest(request, new TypeToken<List<Dette>>(){}.getType(), callback);
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
            .build();
        
        executeOkHttpRequest(request, new TypeToken<List<Dette>>(){}.getType(), callback);
    }
    
    public void createDette(Dette dette, DataCallback<Dette> callback) {
        Log.d(TAG, "createDette appelé");
        
        try {
            if (dette.getClientId() == null || dette.getClientId().isEmpty()) {
                callback.onError("ID client requis");
                return;
            }
            
            // CORRECTION: Validation du montant
            if (dette.getMontant() <= 0) {
                callback.onError("Le montant doit être supérieur à 0");
                return;
            }
            
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("client_id", dette.getClientId());
            
            // CORRECTION: S'assurer que le montant est bien un nombre
            jsonObject.addProperty("montant", dette.getMontant());
            
            jsonObject.addProperty("user_id", getCurrentUserId());
            
            if (dette.getDescription() != null && !dette.getDescription().trim().isEmpty()) {
                jsonObject.addProperty("description", dette.getDescription().trim());
            }
            
            // CORRECTION: Gestion des dates
            if (dette.getDateDette() != null && !dette.getDateDette().trim().isEmpty()) {
                jsonObject.addProperty("date_dette", dette.getDateDette().trim());
            } else {
                // Date par défaut: aujourd'hui au format ISO
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                String today = sdf.format(new java.util.Date());
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
            
            Log.d(TAG, "JSON dette à envoyer: " + jsonObject.toString());
            
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
                .build();
            
            executeSingleItemRequest(request, new TypeToken<List<Dette>>(){}.getType(), callback);
            
        } catch (Exception e) {
            Log.e(TAG, "Exception createDette: " + e.getMessage(), e);
            callback.onError("Erreur création dette: " + e.getMessage());
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
            
            // CORRECTION: Validation du montant
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
                .build();
            
            executeSimpleRequest(request, callback);
            
        } catch (Exception e) {
            Log.e(TAG, "Exception updateDette: " + e.getMessage());
            callback.onError("Erreur mise à jour dette: " + e.getMessage());
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
                .build();
            
            executeSimpleRequest(request, callback);
            
        } catch (Exception e) {
            Log.e(TAG, "Exception deleteDette: " + e.getMessage());
            callback.onError("Erreur suppression dette: " + e.getMessage());
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
            .build();
        
        executeSingleItemRequest(request, new TypeToken<List<Dette>>(){}.getType(), callback);
    }
    
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
                .build();
            
            executeSimpleRequest(request, callback);
            
        } catch (Exception e) {
            Log.e(TAG, "Exception updateDetteStatut: " + e.getMessage());
            callback.onError("Erreur mise à jour statut: " + e.getMessage());
        }
    }
    
    // ============ PAIEMENTS ============
    
    public void getAllPaiements(DataCallback<List<Paiement>> callback) {
        Log.d(TAG, "getAllPaiements appelé");
        
        Request request = new Request.Builder()
            .url(baseUrl + "paiements?order=date_paiement.desc")
            .get()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer " + supabaseKey)
            .build();
        
        executeOkHttpRequest(request, new TypeToken<List<Paiement>>(){}.getType(), callback);
    }
    
    public void createPaiement(Paiement paiement, DataCallback<Paiement> callback) {
        Log.d(TAG, "createPaiement appelé");
        
        try {
            if (paiement.getDetteId() == null || paiement.getDetteId().isEmpty()) {
                callback.onError("ID dette requis");
                return;
            }
            
            // CORRECTION: Validation du montant
            if (paiement.getMontant() <= 0) {
                callback.onError("Le montant doit être supérieur à 0");
                return;
            }
            
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("dette_id", paiement.getDetteId());
            jsonObject.addProperty("client_id", paiement.getClientId());
            jsonObject.addProperty("user_id", getCurrentUserId());
            
            // CORRECTION: S'assurer que le montant est bien un nombre
            jsonObject.addProperty("montant", paiement.getMontant());
            
            // CORRECTION: Gestion des dates
            if (paiement.getDatePaiement() != null && !paiement.getDatePaiement().trim().isEmpty()) {
                jsonObject.addProperty("date_paiement", paiement.getDatePaiement().trim());
            } else {
                // Date par défaut: aujourd'hui au format ISO
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                String today = sdf.format(new java.util.Date());
                jsonObject.addProperty("date_paiement", today);
            }
            
            if (paiement.getModePaiement() != null && !paiement.getModePaiement().trim().isEmpty()) {
                jsonObject.addProperty("mode_paiement", paiement.getModePaiement().trim());
            }
            if (paiement.getReference() != null && !paiement.getReference().trim().isEmpty()) {
                jsonObject.addProperty("reference", paiement.getReference().trim());
            }
            if (paiement.getDescription() != null && !paiement.getDescription().trim().isEmpty()) {
                jsonObject.addProperty("description", paiement.getDescription().trim());
            }
            
            Log.d(TAG, "JSON paiement à envoyer: " + jsonObject.toString());
            
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), 
                jsonObject.toString()
            );
            
            Request request = new Request.Builder()
                .url(baseUrl + "paiements")
                .post(body)
                .addHeader("apikey", supabaseKey)
                .addHeader("Authorization", "Bearer " + supabaseKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .build();
            
            executeSingleItemRequest(request, new TypeToken<List<Paiement>>(){}.getType(), callback);
            
        } catch (Exception e) {
            Log.e(TAG, "Exception createPaiement: " + e.getMessage(), e);
            callback.onError("Erreur création paiement: " + e.getMessage());
        }
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
            .build();
        
        executeOkHttpRequest(request, new TypeToken<List<Paiement>>(){}.getType(), callback);
    }
    
    // ============ MÉTHODES GÉNÉRIQUES ============
    
    private <T> void executeOkHttpRequest(Request request, Type type, DataCallback<T> callback) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = null;
                try {
                    responseBody = response.body() != null ? 
                        response.body().string() : "null";
                    
                    Log.d(TAG, "Réponse API " + request.url() + ": " + response.code());
                    
                    // CORRECTION: Log détaillé pour débogage
                    String logBody = responseBody.length() > 500 ? 
                        responseBody.substring(0, 500) + "..." : responseBody;
                    Log.d(TAG, "Corps de la réponse: " + logBody);
                    
                    if (response.isSuccessful()) {
                        try {
                            // CORRECTION: Gestion améliorée du parsing JSON
                            if (responseBody == null || responseBody.equals("null") || responseBody.trim().isEmpty()) {
                                callback.onError("Réponse vide du serveur");
                                return;
                            }
                            
                            T data = gson.fromJson(responseBody, type);
                            callback.onSuccess(data);
                        } catch (JsonSyntaxException e) {
                            Log.e(TAG, "Erreur parsing JSON: " + e.getMessage());
                            Log.e(TAG, "JSON problématique: " + responseBody);
                            callback.onError("Erreur format données: " + e.getMessage());
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Erreur format nombre: " + e.getMessage());
                            Log.e(TAG, "Données problématiques: " + responseBody);
                            callback.onError("Erreur format numérique dans la réponse");
                        }
                    } else {
                        String errorMsg = parseSupabaseError(responseBody, response.code());
                        callback.onError("Erreur " + response.code() + ": " + errorMsg);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Erreur traitement réponse: " + e.getMessage(), e);
                    callback.onError("Erreur traitement: " + e.getMessage());
                } finally {
                    if (response.body() != null) {
                        response.body().close();
                    }
                }
            }
            
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Erreur réseau " + request.url() + ": " + e.getMessage());
                callback.onError("Erreur réseau: " + e.getMessage());
            }
        });
    }
    
    private <T> void executeSingleItemRequest(Request request, Type listType, DataCallback<T> callback) {
        executeOkHttpRequest(request, listType, new DataCallback<List<T>>() {
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
                    
                    Log.d(TAG, "Réponse simple " + request.url() + ": " + response.code() + 
                          " - " + (responseBody.length() > 200 ? responseBody.substring(0, 200) + "..." : responseBody));
                    
                    if (response.isSuccessful()) {
                        callback.onSuccess("Opération réussie");
                    } else {
                        String errorMsg = parseSupabaseError(responseBody, response.code());
                        callback.onError("Erreur " + response.code() + ": " + errorMsg);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Erreur traitement réponse: " + e.getMessage());
                    callback.onError("Erreur: " + e.getMessage());
                } finally {
                    if (response.body() != null) {
                        response.body().close();
                    }
                }
            }
            
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Erreur réseau " + request.url() + ": " + e.getMessage());
                callback.onError("Erreur réseau: " + e.getMessage());
            }
        });
    }
    
    // ============ UTILITAIRES ============
    
    private String parseSupabaseError(String responseBody, int statusCode) {
        try {
            if (responseBody == null || responseBody.isEmpty() || responseBody.equals("null")) {
                return "Réponse vide (" + statusCode + ")";
            }
            
            // Essayer de parser comme JSON
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
            
        } catch (JsonSyntaxException e) {
            // Si pas du JSON, retourner le texte brut
            return "Erreur " + statusCode + ": " + 
                   (responseBody.length() > 100 ? responseBody.substring(0, 100) + "..." : responseBody);
        } catch (NumberFormatException e) {
            return "Erreur format dans la réponse: " + e.getMessage();
        }
    }
    
    // ============ INTERFACES DE CALLBACK ============
    
    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }
    
    public interface SimpleCallback {
        void onSuccess(String message);
        void onError(String error);
    }
    
    // Méthode utilitaire pour récupérer l'ID utilisateur
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
    
    // CORRECTION: Méthode utilitaire pour tester les conversions de montant
    public static double parseMontant(String montantStr) {
        try {
            // Remplacer les virgules par des points pour les nombres français
            String cleaned = montantStr.replace(',', '.').trim();
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Erreur conversion montant: " + montantStr);
            return 0.0;
        }
    }
}