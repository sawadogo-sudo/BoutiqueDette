package bf.amido.sawadogo.boutiquedette.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bf.amido.sawadogo.boutiquedette.R;
import bf.amido.sawadogo.boutiquedette.AddEditDetteActivity;
import bf.amido.sawadogo.boutiquedette.models.Client;
import bf.amido.sawadogo.boutiquedette.models.Dette;
import bf.amido.sawadogo.boutiquedette.adapters.api.ApiHelper;
import bf.amido.sawadogo.boutiquedette.adapters.DetteAdapter;

public class DettesFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private DetteAdapter detteAdapter;
    private List<Dette> detteList;
    private ApiHelper apiHelper;
    private TextView tvEmpty;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvTotalDettes, tvNombreDettes;
    private FloatingActionButton fabAddDette;
    
    public DettesFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dettes, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        try {
            // Initialiser avec un contexte valide
            if (getContext() != null) {
                apiHelper = new ApiHelper(getContext());
            }
            
            detteList = new ArrayList<>();
            
            initViews(view);
            setupRecyclerView();
            setupSwipeRefresh();
            
            // Charger les dettes immédiatement
            loadDettesWithClients();
            
        } catch (Exception e) {
            showToast("Erreur initialisation: " + e.getMessage());
            Log.e("DettesFragment", "Erreur initialisation", e);
        }
    }
    
    private void initViews(View view) {
        try {
            recyclerView = view.findViewById(R.id.recyclerViewDettes);
            tvEmpty = view.findViewById(R.id.tvEmpty);
            
            // Nouveaux éléments du layout amélioré
            swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
            tvTotalDettes = view.findViewById(R.id.tvTotalDettes);
            tvNombreDettes = view.findViewById(R.id.tvNombreDettes);
            fabAddDette = view.findViewById(R.id.fabAddDette);
            
            if (fabAddDette != null) {
                fabAddDette.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(getActivity(), AddEditDetteActivity.class);
                        intent.putExtra("MODE", "ADD");
                        startActivity(intent);
                    } catch (Exception e) {
                        showToast("Erreur ouverture: " + e.getMessage());
                        Log.e("DettesFragment", "Erreur ouverture AddEditDetteActivity", e);
                    }
                });
            }
            
        } catch (Exception e) {
            showToast("Erreur initialisation des vues: " + e.getMessage());
            Log.e("DettesFragment", "Erreur initViews", e);
        }
    }
    
    private void setupRecyclerView() {
        try {
            if (getContext() != null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                detteAdapter = new DetteAdapter(getContext(), detteList);
                recyclerView.setAdapter(detteAdapter);
            }
        } catch (Exception e) {
            showToast("Erreur RecyclerView: " + e.getMessage());
            Log.e("DettesFragment", "Erreur setupRecyclerView", e);
        }
    }
    
    private void setupSwipeRefresh() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                loadDettesWithClients();
            });
            
            // Couleurs du swipe refresh
            swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
            );
        }
    }
    
    private void loadDettesWithClients() {
        try {
            if (apiHelper == null && getContext() != null) {
                apiHelper = new ApiHelper(getContext());
            }
            
            if (apiHelper == null) {
                showEmptyState("ApiHelper non initialisé");
                return;
            }
            
            // Afficher un indicateur de chargement
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(true);
            }
            
            // Étape 1: Charger tous les clients
            apiHelper.getAllClients(new ApiHelper.DataCallback<List<Client>>() {
                @Override
                public void onSuccess(List<Client> clients) {
                    Log.d("DettesFragment", clients.size() + " clients chargés");
                    
                    // Étape 2: Charger toutes les dettes
                    apiHelper.getAllDettes(new ApiHelper.DataCallback<List<Dette>>() {
                        @Override
                        public void onSuccess(List<Dette> dettes) {
                            Log.d("DettesFragment", dettes.size() + " dettes chargées");
                            
                            // Étape 3: Combiner les données
                            combineDettesWithClients(dettes, clients);
                            
                            // Arrêter le swipe refresh
                            if (swipeRefreshLayout != null) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.e("DettesFragment", "Erreur chargement dettes: " + error);
                            
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    // Arrêter le swipe refresh
                                    if (swipeRefreshLayout != null) {
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                    
                                    showEmptyState("Erreur chargement dettes");
                                    showToast("Erreur: " + error);
                                });
                            }
                        }
                    });
                }
                
                @Override
                public void onError(String error) {
                    Log.e("DettesFragment", "Erreur chargement clients: " + error);
                    
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            // Arrêter le swipe refresh
                            if (swipeRefreshLayout != null) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            
                            // Essayer de charger seulement les dettes
                            loadDettesOnly();
                        });
                    }
                }
            });
            
        } catch (Exception e) {
            Log.e("DettesFragment", "Exception loadDettesWithClients", e);
            
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // Arrêter le swipe refresh
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    
                    showToast("Erreur: " + e.getMessage());
                    loadDettesOnly();
                });
            }
        }
    }
    
    private void combineDettesWithClients(List<Dette> dettes, List<Client> clients) {
        if (getActivity() == null) return;
        
        getActivity().runOnUiThread(() -> {
            try {
                // Créer une Map pour un accès rapide aux clients par ID
                Map<String, Client> clientMap = new HashMap<>();
                for (Client client : clients) {
                    if (client.getId() != null) {
                        clientMap.put(client.getId(), client);
                    }
                }
                
                // Associer les clients aux dettes
                List<Dette> dettesWithClientInfo = new ArrayList<>();
                for (Dette dette : dettes) {
                    Dette detteCopy = new Dette();
                    // Copier toutes les propriétés
                    detteCopy.setId(dette.getId());
                    detteCopy.setClientId(dette.getClientId());
                    detteCopy.setMontant(dette.getMontant());
                    detteCopy.setDescription(dette.getDescription());
                    detteCopy.setDateDette(dette.getDateDette());
                    detteCopy.setDateEcheance(dette.getDateEcheance());
                    detteCopy.setStatut(dette.getStatut());
                    detteCopy.setUserId(dette.getUserId());
                    detteCopy.setCreatedAt(dette.getCreatedAt());
                    
                    // Trouver le client correspondant
                    if (dette.getClientId() != null) {
                        Client client = clientMap.get(dette.getClientId());
                        if (client != null) {
                            // Définir les infos du client
                            detteCopy.setClientNom(client.getNom());
                            detteCopy.setClientPrenom(client.getPrenom());
                            detteCopy.setClientTelephone(client.getTelephone());
                        } else {
                            // Client non trouvé
                            detteCopy.setClientNom("Client #" + dette.getClientId());
                        }
                    }
                    
                    dettesWithClientInfo.add(detteCopy);
                    
                    // Log pour débogage
                    Log.d("DettesFragment", "Dette ID: " + detteCopy.getId() + 
                          ", Client ID: " + detteCopy.getClientId() + 
                          ", Nom complet: " + detteCopy.getNomComplet());
                }
                
                // Mettre à jour l'UI
                updateUI(dettesWithClientInfo);
                
            } catch (Exception e) {
                Log.e("DettesFragment", "Erreur combineDettesWithClients", e);
                showToast("Erreur combinaison données: " + e.getMessage());
                updateUI(dettes); // Utiliser les dettes sans info client
            }
        });
    }
    
    private void loadDettesOnly() {
        try {
            apiHelper.getAllDettes(new ApiHelper.DataCallback<List<Dette>>() {
                @Override
                public void onSuccess(List<Dette> dettes) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                // Arrêter le swipe refresh
                                if (swipeRefreshLayout != null) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                                
                                updateUI(dettes);
                                
                            } catch (Exception e) {
                                Log.e("DettesFragment", "Erreur updateUI", e);
                                showToast("Erreur affichage: " + e.getMessage());
                            }
                        });
                    }
                }
                
                @Override
                public void onError(String error) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            // Arrêter le swipe refresh
                            if (swipeRefreshLayout != null) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            
                            showEmptyState("Erreur chargement: " + 
                                (error != null ? error.substring(0, Math.min(error.length(), 50)) : "Erreur inconnue"));
                            
                            showToast("Erreur: " + error);
                        });
                    }
                }
            });
            
        } catch (Exception e) {
            Log.e("DettesFragment", "Exception loadDettesOnly", e);
            
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // Arrêter le swipe refresh
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    
                    showToast("Erreur: " + e.getMessage());
                    showEmptyState("Erreur: " + e.getMessage());
                });
            }
        }
    }
    
    private void updateUI(List<Dette> dettes) {
        try {
            // Mettre à jour la liste
            detteList.clear();
            if (dettes != null && !dettes.isEmpty()) {
                detteList.addAll(dettes);
                
                // Mettre à jour les statistiques
                updateStatistics(dettes);
                
                // Cacher le message vide
                if (tvEmpty != null) {
                    tvEmpty.setVisibility(View.GONE);
                }
                
                // Afficher la liste
                if (recyclerView != null) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
                
                // Notifier l'adapter
                if (detteAdapter != null) {
                    detteAdapter.updateData(dettes);
                }
                
                // Log final
                Log.d("DettesFragment", "UI mise à jour avec " + dettes.size() + " dettes");
                
            } else {
                showEmptyState("Aucune dette enregistrée");
            }
            
        } catch (Exception e) {
            Log.e("DettesFragment", "Erreur updateUI", e);
            showToast("Erreur mise à jour UI: " + e.getMessage());
            showEmptyState("Erreur affichage données");
        }
    }
    
    private void updateStatistics(List<Dette> dettes) {
        try {
            if (dettes == null || dettes.isEmpty()) {
                resetStatistics();
                return;
            }
            
            double total = 0;
            int enCoursCount = 0;
            int payeCount = 0;
            
            for (Dette dette : dettes) {
                total += dette.getMontant();
                
                if (dette.isPaye()) {
                    payeCount++;
                } else {
                    enCoursCount++;
                }
            }
            
            // Mettre à jour les TextView des statistiques
            if (tvTotalDettes != null) {
                tvTotalDettes.setText(String.format("%,.0f FCFA", total));
            }
            
            if (tvNombreDettes != null) {
                tvNombreDettes.setText(String.valueOf(enCoursCount));
            }
            
            Log.d("DettesFragment", "Statistiques: Total=" + total + 
                  ", En cours=" + enCoursCount + ", Payées=" + payeCount);
            
        } catch (Exception e) {
            Log.e("DettesFragment", "Erreur statistiques", e);
        }
    }
    
    private void resetStatistics() {
        if (tvTotalDettes != null) {
            tvTotalDettes.setText("0 FCFA");
        }
        
        if (tvNombreDettes != null) {
            tvNombreDettes.setText("0");
        }
    }
    
    private void showEmptyState(String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                try {
                    if (tvEmpty != null) {
                        tvEmpty.setText(message);
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                    
                    if (recyclerView != null) {
                        recyclerView.setVisibility(View.GONE);
                    }
                    
                    // Réinitialiser les statistiques
                    resetStatistics();
                    
                } catch (Exception e) {
                    Log.e("DettesFragment", "Erreur showEmptyState", e);
                }
            });
        }
    }
    
    private void showToast(String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                try {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("DettesFragment", "Erreur showToast", e);
                }
            });
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Recharger les données quand le fragment revient au premier plan
        loadDettesWithClients();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nettoyer les références pour éviter les fuites mémoire
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(null);
        }
        
        recyclerView = null;
        detteAdapter = null;
        apiHelper = null;
        tvEmpty = null;
        swipeRefreshLayout = null;
        tvTotalDettes = null;
        tvNombreDettes = null;
        fabAddDette = null;
    }
}