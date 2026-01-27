package bf.amido.sawadogo.boutiquedette.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bf.amido.sawadogo.boutiquedette.R;
import bf.amido.sawadogo.boutiquedette.AddEditClientActivity;
import bf.amido.sawadogo.boutiquedette.ClientsActivity;
import bf.amido.sawadogo.boutiquedette.adapters.ClientAdapter;
import bf.amido.sawadogo.boutiquedette.models.Client;
import bf.amido.sawadogo.boutiquedette.api.ApiHelper;

public class ClientsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ClientAdapter clientAdapter;
    private List<Client> clientList;
    private ApiHelper apiHelper;
    private TextView tvEmpty;
    private Button btnAddClient;
    
    public ClientsFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clients, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        try {
            // Initialiser l'ApiHelper avec un contexte valide
            if (getContext() != null) {
                apiHelper = new ApiHelper(getContext());
            } else {
                Toast.makeText(getActivity(), "Contexte non disponible", Toast.LENGTH_SHORT).show();
                return;
            }
            
            clientList = new ArrayList<>();
            
            initViews(view);
            setupRecyclerView();
            
            // Charger les clients après un petit délai
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    loadClients();
                });
            }
            
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Erreur initialisation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    
    private void initViews(View view) {
        try {
            recyclerView = view.findViewById(R.id.recyclerViewClients);
            tvEmpty = view.findViewById(R.id.tvEmpty);
            btnAddClient = view.findViewById(R.id.btnAddClient);
            
            if (btnAddClient != null) {
                btnAddClient.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(getActivity(), AddEditClientActivity.class);
                        intent.putExtra("MODE", "ADD");
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            // NOTE: Le bouton btnGestionClients a été retiré car il n'existe pas dans le layout
            // Si vous voulez ajouter un bouton pour ouvrir l'activité complète ClientsActivity,
            // ajoutez-le d'abord dans votre layout fragment_clients.xml
            
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Erreur vues: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupRecyclerView() {
        try {
            if (getContext() != null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                clientAdapter = new ClientAdapter(getContext(), clientList, client -> {
                    // Ouvrir les détails du client
                    try {
                        Intent intent = new Intent(getActivity(), AddEditClientActivity.class);
                        intent.putExtra("MODE", "EDIT");
                        intent.putExtra("CLIENT_ID", String.valueOf(client.getId()));
                        intent.putExtra("CLIENT_NOM", client.getNom());
                        intent.putExtra("CLIENT_PRENOM", client.getPrenom());
                        intent.putExtra("CLIENT_TELEPHONE", client.getTelephone());
                        intent.putExtra("CLIENT_EMAIL", client.getEmail());
                        intent.putExtra("CLIENT_VILLE", client.getVille());
                        intent.putExtra("CLIENT_ADRESSE", client.getAdresse());
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Erreur ouverture client: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                recyclerView.setAdapter(clientAdapter);
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Erreur RecyclerView: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loadClients() {
        try {
            if (apiHelper == null && getContext() != null) {
                apiHelper = new ApiHelper(getContext());
            }
            
            if (apiHelper == null) {
                if (tvEmpty != null) {
                    tvEmpty.setText("ApiHelper non initialisé");
                    tvEmpty.setVisibility(View.VISIBLE);
                }
                if (recyclerView != null) {
                    recyclerView.setVisibility(View.GONE);
                }
                return;
            }
            
            apiHelper.getAllClients(new ApiHelper.DataCallback<List<Client>>() {
                @Override
                public void onSuccess(List<Client> clients) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                clientList.clear();
                                if (clients != null && !clients.isEmpty()) {
                                    clientList.addAll(clients);
                                    if (tvEmpty != null) {
                                        tvEmpty.setVisibility(View.GONE);
                                    }
                                    if (recyclerView != null) {
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    if (tvEmpty != null) {
                                        tvEmpty.setText("Aucun client trouvé");
                                        tvEmpty.setVisibility(View.VISIBLE);
                                    }
                                    if (recyclerView != null) {
                                        recyclerView.setVisibility(View.GONE);
                                    }
                                }
                                if (clientAdapter != null) {
                                    clientAdapter.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "Erreur traitement: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                
                @Override
                public void onError(String error) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                if (tvEmpty != null) {
                                    tvEmpty.setText("Erreur: " + (error != null ? error.substring(0, Math.min(error.length(), 50)) : "Erreur inconnue"));
                                    tvEmpty.setVisibility(View.VISIBLE);
                                }
                                if (recyclerView != null) {
                                    recyclerView.setVisibility(View.GONE);
                                }
                                Toast.makeText(getActivity(), "Erreur API: " + error, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "Erreur affichage: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
            
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Erreur chargement: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            if (tvEmpty != null) {
                tvEmpty.setText("Exception: " + e.getMessage());
                tvEmpty.setVisibility(View.VISIBLE);
            }
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Recharger les données quand le fragment revient au premier plan
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                loadClients();
            });
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nettoyer les références
        recyclerView = null;
        clientAdapter = null;
        apiHelper = null;
    }
}