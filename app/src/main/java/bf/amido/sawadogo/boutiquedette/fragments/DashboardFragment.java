package bf.amido.sawadogo.boutiquedette.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import bf.amido.sawadogo.boutiquedette.R;

public class DashboardFragment extends Fragment {

    private TextView textTotalClients, textTotalDettes, textRecentPayments, textTopDebtors;
    
    public DashboardFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflater le layout du fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        
        // Initialiser les vues
        textTotalClients = view.findViewById(R.id.textTotalClients);
        textTotalDettes = view.findViewById(R.id.textTotalDettes);
        textRecentPayments = view.findViewById(R.id.textRecentPayments);
        textTopDebtors = view.findViewById(R.id.textTopDebtors);
        
        // Charger les données
        loadDashboardData();
        
        return view;
    }
    
    private void loadDashboardData() {
        // Données de test - à remplacer par des données réelles plus tard
        textTotalClients.setText("45");
        textTotalDettes.setText("250,000 CFA");
        textRecentPayments.setText("Dernier paiement: Sawadogo Oumar - 25,000 CFA");
        textTopDebtors.setText("1. Marie Konaté: 75,000 CFA\n2. Ousmane Traoré: 50,000 CFA\n3. Fatoumata Ouédraogo: 45,000 CFA");
    }
}