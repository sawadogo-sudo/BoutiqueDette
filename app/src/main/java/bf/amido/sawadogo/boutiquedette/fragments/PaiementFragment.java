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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import bf.amido.sawadogo.boutiquedette.R;
import bf.amido.sawadogo.boutiquedette.adapters.PaiementAdapter;
import bf.amido.sawadogo.boutiquedette.models.Paiement;
import bf.amido.sawadogo.boutiquedette.adapters.api.ApiHelper;
import bf.amido.sawadogo.boutiquedette.AddPaiementActivity;

public class PaiementFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private PaiementAdapter paiementAdapter;
    private List<Paiement> paiementList;
    private ApiHelper apiHelper;
    private TextView tvEmpty, tvTotalAujourdhui;
    private Button btnNouveauPaiement;
    
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
        
        // Initialiser ApiHelper
        if (getContext() != null) {
            apiHelper = new ApiHelper(getContext());
        }
        
        paiementList = new ArrayList<>();
        
        initViews(view);
        setupRecyclerView();
        
        // Charger les paiements
        loadPaiementsAujourdhui();
    }
    
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewPaiements);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        tvTotalAujourdhui = view.findViewById(R.id.tvTotalAujourdhui);
        btnNouveauPaiement = view.findViewById(R.id.btnNouveauPaiement);
        
        // Configurer le bouton pour ajouter un paiement
        if (btnNouveauPaiement != null) {
            btnNouveauPaiement.setOnClickListener(v -> {
                openAddPaiementActivity();
            });
        }
    }
    
    private void setupRecyclerView() {
        if (getContext() != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            paiementAdapter = new PaiementAdapter(getContext(), paiementList);
            recyclerView.setAdapter(paiementAdapter);
        }
    }
    
    private void openAddPaiementActivity() {
        try {
            // Ouvrir directement AddPaiementActivity sans vérifier les dettes
            // L'activité gérera elle-même la sélection des clients avec dettes
            Intent intent = new Intent(getActivity(), AddPaiementActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loadPaiementsAujourdhui() {
        if (apiHelper == null && getContext() != null) {
            apiHelper = new ApiHelper(getContext());
        }
        
        if (apiHelper == null) {
            showErrorMessage("ApiHelper non initialisé", "Erreur d'initialisation");
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
                                    updateUI(true, totalAujourdhui);
                                } else {
                                    updateUI(false, 0);
                                }
                            } else {
                                // Aucun paiement dans la base
                                updateUI(false, 0);
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
                        showErrorMessage("Erreur: " + (error != null ? 
                            error.substring(0, Math.min(error.length(), 50)) : "Erreur inconnue"), 
                            "Erreur de chargement");
                    });
                }
            }
        });
    }
    
    private void updateUI(boolean hasData, double totalAujourdhui) {
        if (hasData) {
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
    }
    
    private void showErrorMessage(String emptyMessage, String totalMessage) {
        try {
            if (tvEmpty != null) {
                tvEmpty.setText(emptyMessage);
                tvEmpty.setVisibility(View.VISIBLE);
            }
            if (recyclerView != null) {
                recyclerView.setVisibility(View.GONE);
            }
            if (tvTotalAujourdhui != null) {
                tvTotalAujourdhui.setText(totalMessage);
            }
        } catch (Exception e) {
            // Ignorer les erreurs d'UI
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Recharger les données quand le fragment revient au premier plan
        loadPaiementsAujourdhui();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nettoyer les références
        recyclerView = null;
        paiementAdapter = null;
        apiHelper = null;
        tvEmpty = null;
        tvTotalAujourdhui = null;
        btnNouveauPaiement = null;
    }
}