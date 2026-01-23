package bf.amido.sawadogo.boutiquedette.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
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
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        
        textTotalClients = view.findViewById(R.id.textTotalClients);
        textTotalDettes = view.findViewById(R.id.textTotalDettes);
        textRecentPayments = view.findViewById(R.id.textRecentPayments);
        textTopDebtors = view.findViewById(R.id.textTopDebtors);
        
        loadDashboardData();
        
        return view;
    }
    
    private void loadDashboardData() {
        // Données de test
        textTotalClients.setText("45");
        textTotalDettes.setText("250,000 CFA");
        textRecentPayments.setText("Dernier paiement: Sawadogo Oumar - 25,000 CFA");
        textTopDebtors.setText("1. Marie Konaté: 75,000 CFA\n2. Ousmane Traoré: 50,000 CFA\n3. Fatoumata Ouédraogo: 45,000 CFA");
    }
    
    // Méthode pour rafraîchir les données
    public void refreshData() {
        // Simuler un chargement de nouvelles données
        textTotalClients.setText("48"); // +3 nouveaux clients
        textTotalDettes.setText("245,500 CFA"); // -4,500 CFA
        textRecentPayments.setText("Dernier paiement: Ousmane Traoré - 15,000 CFA (À l'instant)");
        textTopDebtors.setText("1. Marie Konaté: 70,000 CFA\n2. Ousmane Traoré: 35,000 CFA\n3. Fatoumata Ouédraogo: 45,000 CFA");
        
        // Toast dans l'activité parente
        if (getActivity() != null) {
            Toast.makeText(getActivity(), "Données actualisées", Toast.LENGTH_SHORT).show();
        }
    }
}