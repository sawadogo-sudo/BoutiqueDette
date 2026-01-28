package bf.amido.sawadogo.boutiquedette.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import bf.amido.sawadogo.boutiquedette.R;
import bf.amido.sawadogo.boutiquedette.adapters.HistoriqueAdapter;
import bf.amido.sawadogo.boutiquedette.models.Client;
import bf.amido.sawadogo.boutiquedette.models.Dette;
import bf.amido.sawadogo.boutiquedette.models.HistoriqueItem;
import bf.amido.sawadogo.boutiquedette.models.Paiement;
import bf.amido.sawadogo.boutiquedette.adapters.api.ApiHelper;

public class HistoriqueFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private HistoriqueAdapter historiqueAdapter;
    private List<HistoriqueItem> historiqueList;
    private ApiHelper apiHelper;
    private TextView tvEmpty, tvStatistiques;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isLoading = false;
    
    public HistoriqueFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historique, container, false);
        
        apiHelper = new ApiHelper(requireContext());
        historiqueList = new ArrayList<>();
        
        initViews(view);
        setupRecyclerView();
        
        return view;
    }
    
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewHistorique);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        tvStatistiques = view.findViewById(R.id.tvStatistiques);
        progressBar = view.findViewById(R.id.progressBar);
        
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!isLoading) {
                loadHistorique();
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historiqueAdapter = new HistoriqueAdapter(requireContext(), historiqueList);
        recyclerView.setAdapter(historiqueAdapter);
    }
    
    private void loadHistorique() {
        // Éviter les chargements multiples
        if (isLoading) {
            return;
        }
        
        isLoading = true;
        showProgress(true);
        
        // Nettoyer la liste avant de recharger
        historiqueList.clear();
        if (historiqueAdapter != null) {
            historiqueAdapter.notifyDataSetChanged();
        }
        
        // Utiliser un compteur pour suivre le nombre d'appels API terminés
        AtomicInteger completedCalls = new AtomicInteger(0);
        final int totalCalls = 3; // clients + dettes + paiements
        
        // 1. Charger les clients
        apiHelper.getAllClients(new ApiHelper.DataCallback<List<Client>>() {
            @Override
            public void onSuccess(List<Client> clients) {
                if (clients != null && !clients.isEmpty()) {
                    // Ajouter les créations de clients à l'historique
                    // Limiter aux 5 derniers clients pour éviter trop d'éléments
                    int count = Math.min(clients.size(), 5);
                    for (int i = 0; i < count; i++) {
                        Client client = clients.get(i);
                        if (client.getCreatedAt() != null) {
                            // Créer un HistoriqueItem simple
                            historiqueList.add(new HistoriqueItem(
                                "Nouveau client",
                                client.getNom() + (client.getPrenom() != null && !client.getPrenom().isEmpty() ? " " + client.getPrenom() : ""),
                                formatTime(client.getCreatedAt()),
                                formatDate(client.getCreatedAt())
                            ));
                        }
                    }
                }
                
                // Marquer cet appel comme terminé
                checkIfAllCallsCompleted(completedCalls.incrementAndGet(), totalCalls);
            }
            
            @Override
            public void onError(String error) {
                Log.e("HistoriqueFragment", "Erreur chargement clients: " + error);
                checkIfAllCallsCompleted(completedCalls.incrementAndGet(), totalCalls);
            }
        });
        
        // 2. Charger les dettes
        apiHelper.getAllDettes(new ApiHelper.DataCallback<List<Dette>>() {
            @Override
            public void onSuccess(List<Dette> dettes) {
                if (dettes != null && !dettes.isEmpty()) {
                    // Ajouter les créations de dettes à l'historique
                    // Limiter aux 5 dernières dettes
                    int count = Math.min(dettes.size(), 5);
                    for (int i = 0; i < count; i++) {
                        Dette dette = dettes.get(i);
                        if (dette.getCreatedAt() != null) {
                            historiqueList.add(new HistoriqueItem(
                                "Dette créée",
                                String.format(Locale.FRANCE, "Dette de %,.0f FCFA", dette.getMontant()),
                                formatTime(dette.getCreatedAt()),
                                formatDate(dette.getCreatedAt())
                            ));
                        }
                    }
                }
                
                // Marquer cet appel comme terminé
                checkIfAllCallsCompleted(completedCalls.incrementAndGet(), totalCalls);
            }
            
            @Override
            public void onError(String error) {
                Log.e("HistoriqueFragment", "Erreur chargement dettes: " + error);
                checkIfAllCallsCompleted(completedCalls.incrementAndGet(), totalCalls);
            }
        });
        
        // 3. Charger les paiements
        apiHelper.getAllPaiements(new ApiHelper.DataCallback<List<Paiement>>() {
            @Override
            public void onSuccess(List<Paiement> paiements) {
                if (paiements != null && !paiements.isEmpty()) {
                    // Ajouter les paiements à l'historique
                    // Limiter aux 5 derniers paiements
                    int count = Math.min(paiements.size(), 5);
                    for (int i = 0; i < count; i++) {
                        Paiement paiement = paiements.get(i);
                        if (paiement.getDatePaiement() != null) {
                            historiqueList.add(new HistoriqueItem(
                                "Paiement reçu",
                                String.format(Locale.FRANCE, "Paiement de %,.0f FCFA", paiement.getMontant()),
                                formatTime(paiement.getDatePaiement()),
                                formatDate(paiement.getDatePaiement())
                            ));
                        }
                    }
                }
                
                // Marquer cet appel comme terminé
                checkIfAllCallsCompleted(completedCalls.incrementAndGet(), totalCalls);
            }
            
            @Override
            public void onError(String error) {
                Log.e("HistoriqueFragment", "Erreur chargement paiements: " + error);
                checkIfAllCallsCompleted(completedCalls.incrementAndGet(), totalCalls);
            }
        });
    }
    
    private void checkIfAllCallsCompleted(int completed, int total) {
        if (completed >= total) {
            // Tous les appels sont terminés, mettre à jour l'UI
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // Inverser la liste pour avoir les plus récents en premier
                    Collections.reverse(historiqueList);
                    
                    // Limiter aux 15 dernières activités au total
                    if (historiqueList.size() > 15) {
                        historiqueList = new ArrayList<>(historiqueList.subList(0, 15));
                    }
                    
                    // Mettre à jour l'interface
                    updateUI();
                    
                    isLoading = false;
                    showProgress(false);
                    swipeRefreshLayout.setRefreshing(false);
                });
            }
        }
    }
    
    private void updateUI() {
        if (!historiqueList.isEmpty()) {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            tvStatistiques.setText(String.format("%d activités récentes", historiqueList.size()));
            
            // Mettre à jour l'adapter
            if (historiqueAdapter == null) {
                historiqueAdapter = new HistoriqueAdapter(requireContext(), historiqueList);
                recyclerView.setAdapter(historiqueAdapter);
            } else {
                // Si votre adapter n'a pas updateList, recréez-le
                historiqueAdapter = new HistoriqueAdapter(requireContext(), historiqueList);
                recyclerView.setAdapter(historiqueAdapter);
            }
        } else {
            tvEmpty.setText("Aucune activité enregistrée");
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvStatistiques.setText("Aucune activité");
        }
    }
    
    private String formatTime(String dateTime) {
        try {
            if (dateTime == null || dateTime.isEmpty()) {
                return "--:--";
            }
            
            // Si la date contient un 'T' (format ISO)
            if (dateTime.contains("T")) {
                String[] parts = dateTime.split("T");
                if (parts.length > 1) {
                    String timePart = parts[1];
                    if (timePart.length() >= 5) {
                        return timePart.substring(0, 5); // HH:MM
                    }
                }
            }
            
            // Si c'est déjà une heure simple
            if (dateTime.contains(":")) {
                String[] parts = dateTime.split(" ");
                for (String part : parts) {
                    if (part.contains(":")) {
                        return part.substring(0, 5);
                    }
                }
            }
            
            return dateTime;
        } catch (Exception e) {
            return "--:--";
        }
    }
    
    private String formatDate(String dateTime) {
        try {
            if (dateTime == null || dateTime.isEmpty()) {
                return "--/--/----";
            }
            
            Date date = null;
            SimpleDateFormat inputFormat;
            
            // Essayer différents formats de date
            if (dateTime.contains("T")) {
                inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            } else if (dateTime.contains("-") && dateTime.length() >= 10) {
                inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            } else {
                return dateTime;
            }
            
            try {
                date = inputFormat.parse(dateTime);
            } catch (Exception e) {
                // Essayer un autre format
                try {
                    inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    date = inputFormat.parse(dateTime);
                } catch (Exception e2) {
                    return dateTime.substring(0, Math.min(10, dateTime.length()));
                }
            }
            
            if (date != null) {
                // Vérifier si c'est aujourd'hui
                SimpleDateFormat todayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String today = todayFormat.format(new Date());
                String itemDate = todayFormat.format(date);
                
                if (itemDate.equals(today)) {
                    return "Aujourd'hui";
                } else {
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    return outputFormat.format(date);
                }
            }
            
            return dateTime.length() >= 10 ? dateTime.substring(0, 10) : dateTime;
        } catch (Exception e) {
            return "--/--/----";
        }
    }
    
    private void showProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        
        if (show) {
            tvEmpty.setText("Chargement de l'historique...");
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (!isLoading) {
            loadHistorique();
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nettoyer pour éviter les fuites mémoire
        isLoading = false;
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}