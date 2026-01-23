package bf.amido.sawadogo.boutiquedette.services;

import android.content.Context;
import android.util.Log;
import bf.amido.sawadogo.boutiquedette.api.ApiClient;
import bf.amido.sawadogo.boutiquedette.api.SupabaseApiService;
import bf.amido.sawadogo.boutiquedette.api.SupabaseConfig;
import bf.amido.sawadogo.boutiquedette.models.Client;
import bf.amido.sawadogo.boutiquedette.models.Dette;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class ApiHelper {
    
    private static final String TAG = "ApiHelper";
    private Context context;
    private SupabaseApiService apiService;
    
    public ApiHelper(Context context) {
        this.context = context;
        this.apiService = ApiClient.getSupabaseService(context);
    }
    
    // ============ CLIENTS ============
    
    public void getAllClients(DataCallback<List<Client>> callback) {
        Call<List<Client>> call = apiService.getAllClients(
            SupabaseConfig.DEFAULT_SELECT,
            SupabaseConfig.DEFAULT_ORDER
        );
        
        executeCall(call, callback);
    }
    
    public void getClientById(String id, DataCallback<Client> callback) {
        Call<List<Client>> call = apiService.getClientById(
            id,
            SupabaseConfig.DEFAULT_SELECT
        );
        
        call.enqueue(new Callback<List<Client>>() {
            @Override
            public void onResponse(Call<List<Client>> call, Response<List<Client>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onSuccess(response.body().get(0));
                } else {
                    callback.onError("Client non trouvé");
                }
            }
            
            @Override
            public void onFailure(Call<List<Client>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    
    public void searchClients(String query, DataCallback<List<Client>> callback) {
        Call<List<Client>> call = apiService.searchClients(
            query + "%",
            SupabaseConfig.DEFAULT_SELECT
        );
        
        executeCall(call, callback);
    }
    
    public void createClient(Client client, DataCallback<Client> callback) {
        Call<List<Client>> call = apiService.createClient(client);
        
        call.enqueue(new Callback<List<Client>>() {
            @Override
            public void onResponse(Call<List<Client>> call, Response<List<Client>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onSuccess(response.body().get(0));
                } else {
                    String error = "Erreur lors de la création";
                    if (response.errorBody() != null) {
                        try {
                            error += ": " + response.errorBody().string();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<List<Client>> call, Throwable t) {
                Log.e(TAG, "Create client error", t);
                callback.onError("Erreur réseau: " + t.getMessage());
            }
        });
    }
    
    public void updateClient(String id, Client client, SimpleCallback callback) {
        Call<Void> call = apiService.updateClient(id, client);
        
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Client mis à jour");
                } else {
                    String error = "Erreur: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            error += ": " + response.errorBody().string();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    
    public void deleteClient(String id, SimpleCallback callback) {
        Call<Void> call = apiService.deleteClient(id);
        
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Client supprimé");
                } else {
                    callback.onError("Erreur: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    
    // ============ DETTES ============
    
    public void getDettesByClient(String clientId, DataCallback<List<Dette>> callback) {
        Call<List<Dette>> call = apiService.getDettesByClient(
            clientId,
            SupabaseConfig.DEFAULT_SELECT,
            SupabaseConfig.DEFAULT_ORDER
        );
        
        executeCall(call, callback);
    }
    
    public void getAllDettes(DataCallback<List<Dette>> callback) {
        Call<List<Dette>> call = apiService.getAllDettes(
            SupabaseConfig.DEFAULT_SELECT,
            SupabaseConfig.DEFAULT_ORDER
        );
        
        executeCall(call, callback);
    }
    
    public void createDette(Dette dette, DataCallback<Dette> callback) {
        Call<List<Dette>> call = apiService.createDette(dette);
        
        call.enqueue(new Callback<List<Dette>>() {
            @Override
            public void onResponse(Call<List<Dette>> call, Response<List<Dette>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onSuccess(response.body().get(0));
                } else {
                    callback.onError("Erreur lors de la création");
                }
            }
            
            @Override
            public void onFailure(Call<List<Dette>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    
    public void updateDette(String id, Dette dette, SimpleCallback callback) {
        Call<Void> call = apiService.updateDette(id, dette);
        
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Dette mise à jour");
                } else {
                    callback.onError("Erreur: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    
    public void deleteDette(String id, SimpleCallback callback) {
        Call<Void> call = apiService.deleteDette(id);
        
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Dette supprimée");
                } else {
                    callback.onError("Erreur: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    
    // ============ MÉTHODES GÉNÉRIQUES ============
    
    private <T> void executeCall(Call<T> call, DataCallback<T> callback) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    T data = response.body();
                    callback.onSuccess(data);
                } else {
                    String error = "Erreur " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            error += ": " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<T> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                callback.onError("Erreur réseau: " + t.getMessage());
            }
        });
    }
    

    // ============ DETTES - Méthodes manquantes ============

public void getDetteById(String detteId, DataCallback<Dette> callback) {
    Call<List<Dette>> call = apiService.getDetteById(
        detteId,
        SupabaseConfig.DEFAULT_SELECT
    );
    
    call.enqueue(new Callback<List<Dette>>() {
        @Override
        public void onResponse(Call<List<Dette>> call, Response<List<Dette>> response) {
            if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                callback.onSuccess(response.body().get(0));
            } else {
                callback.onError("Dette non trouvée");
            }
        }
        
        @Override
        public void onFailure(Call<List<Dette>> call, Throwable t) {
            callback.onError(t.getMessage());
        }
    });
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
}