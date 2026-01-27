package bf.amido.sawadogo.boutiquedette.fragments;

import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import bf.amido.sawadogo.boutiquedette.R;
import bf.amido.sawadogo.boutiquedette.adapters.PaiementAdapter;
import bf.amido.sawadogo.boutiquedette.models.Paiement;
import bf.amido.sawadogo.boutiquedette.api.ApiHelper;

public class PaiementFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private PaiementAdapter paiementAdapter;
    private List<Paiement> paiementList;
    private ApiHelper apiHelper;
    private TextView tvEmpty, tvTotalAujourdhui;
    
    public PaiementFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_paiement, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        try {
            // Initialiser avec un contexte valide
            if (getContext() != null) {
                apiHelper = new ApiHelper(getContext());
            } else {
                Toast.makeText(getActivity(), "Contexte non disponible", Toast.LENGTH_SHORT).show();
                return;
            }
            
            paiementList = new ArrayList<>();
            
            initViews(view);
            setupRecyclerView();
            
            // Charger les paiements après un petit délai
            if (getActivity() != null) {
                getActivity().runOnUiThread(this::loadPaiementsAujourdhui);
            }
            
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Erreur initialisation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    
    private void initViews(View view) {
        try {
            recyclerView = view.findViewById(R.id.recyclerViewPaiements);
            tvEmpty = view.findViewById(R.id.tvEmpty);
            tvTotalAujourdhui = view.findViewById(R.id.tvTotalAujourdhui);
            
            // Vérifier que les vues existent
            if (tvEmpty == null) {
                Toast.makeText(getActivity(), "tvEmpty non trouvé", Toast.LENGTH_SHORT).show();
            }
            if (tvTotalAujourdhui == null) {
                Toast.makeText(getActivity(), "tvTotalAujourdhui non trouvé", Toast.LENGTH_SHORT).show();
            }
            
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Erreur vues: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupRecyclerView() {
        try {
            if (getContext() != null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                paiementAdapter = new PaiementAdapter(getContext(), paiementList);
                recyclerView.setAdapter(paiementAdapter);
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Erreur RecyclerView: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loadPaiementsAujourdhui() {
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
                if (tvTotalAujourdhui != null) {
                    tvTotalAujourdhui.setText("Erreur d'initialisation");
                }
                return;
            }
            
            // Récupérer la date d'aujourd'hui au format YYYY-MM-DD
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String aujourdhui = sdf.format(Calendar.getInstance().getTime());
            
            // Charger tous les paiements
            apiHelper.getAllPaiements(new ApiHelper.DataCallback<List<Paiement>>() {
                @Override
                public void onSuccess(List<Paiement> paiements) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                paiementList.clear();
                                double totalAujourdhui = 0;
                                
                                if (paiements != null && !paiements.isEmpty()) {
                                    // Filtrer pour aujourd'hui et calculer le total
                                    for (Paiement p : paiements) {
                                        if (p != null && p.getDatePaiement() != null && 
                                            p.getDatePaiement().contains(aujourdhui)) {
                                            paiementList.add(p);
                                            totalAujourdhui += p.getMontant();
                                        }
                                    }
                                    
                                    // Mettre à jour l'interface
                                    if (!paiementList.isEmpty()) {
                                        if (tvEmpty != null) {
                                            tvEmpty.setVisibility(View.GONE);
                                        }
                                        if (recyclerView != null) {
                                            recyclerView.setVisibility(View.VISIBLE);
                                        }
                                        if (tvTotalAujourdhui != null) {
                                            tvTotalAujourdhui.setText(String.format(Locale.FRANCE, 
                                                "Total aujourd'hui: %,.0f FCFA", totalAujourdhui));
                                        }
                                    } else {
                                        if (tvEmpty != null) {
                                            tvEmpty.setText("Aucun paiement aujourd'hui");
                                            tvEmpty.setVisibility(View.VISIBLE);
                                        }
                                        if (recyclerView != null) {
                                            recyclerView.setVisibility(View.GONE);
                                        }
                                        if (tvTotalAujourdhui != null) {
                                            tvTotalAujourdhui.setText("Total aujourd'hui: 0 FCFA");
                                        }
                                    }
                                } else {
                                    // Aucun paiement dans la base
                                    if (tvEmpty != null) {
                                        tvEmpty.setText("Aucun paiement enregistré");
                                        tvEmpty.setVisibility(View.VISIBLE);
                                    }
                                    if (recyclerView != null) {
                                        recyclerView.setVisibility(View.GONE);
                                    }
                                    if (tvTotalAujourdhui != null) {
                                        tvTotalAujourdhui.setText("Total aujourd'hui: 0 FCFA");
                                    }
                                }
                                
                                if (paiementAdapter != null) {
                                    paiementAdapter.notifyDataSetChanged();
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
                                    tvEmpty.setText("Erreur: " + (error != null ? 
                                        error.substring(0, Math.min(error.length(), 50)) : "Erreur inconnue"));
                                    tvEmpty.setVisibility(View.VISIBLE);
                                }
                                if (recyclerView != null) {
                                    recyclerView.setVisibility(View.GONE);
                                }
                                if (tvTotalAujourdhui != null) {
                                    tvTotalAujourdhui.setText("Erreur de chargement");
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
            if (tvTotalAujourdhui != null) {
                tvTotalAujourdhui.setText("Erreur");
            }
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Recharger les données quand le fragment revient au premier plan
        if (getActivity() != null) {
            getActivity().runOnUiThread(this::loadPaiementsAujourdhui);
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nettoyer les références
        recyclerView = null;
        paiementAdapter = null;
        apiHelper = null;
    }
}