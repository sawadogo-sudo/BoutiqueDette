package bf.amido.sawadogo.boutiquedette.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import bf.amido.sawadogo.boutiquedette.R;
import bf.amido.sawadogo.boutiquedette.adapters.api.ApiHelper;
import bf.amido.sawadogo.boutiquedette.models.Client;
import bf.amido.sawadogo.boutiquedette.models.Dette;
import java.util.List;

public class DashboardFragment extends Fragment {
    
    private TextView tvTotalClients, tvTotalDettes, tvDettesEnCours, tvDettesPayees;
    private ApiHelper apiHelper;
    
    public DashboardFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        apiHelper = new ApiHelper(getContext());
        
        initViews(view);
        loadData();
    }
    
    private void initViews(View view) {
        tvTotalClients = view.findViewById(R.id.tvTotalClients);
        tvTotalDettes = view.findViewById(R.id.tvTotalDettes);
        tvDettesEnCours = view.findViewById(R.id.tvDettesEnCours);
        tvDettesPayees = view.findViewById(R.id.tvDettesPayees);
    }
    
    public void refreshData() {
        // Cette méthode est appelée depuis MainActivity
        if (isAdded() && getActivity() != null) {
            loadData();
        }
    }
    
    private void loadData() {
        loadClients();
        loadDettes();
    }
    
    private void loadClients() {
        if (apiHelper == null) {
            apiHelper = new ApiHelper(getContext());
        }
        
        apiHelper.getAllClients(new ApiHelper.DataCallback<List<Client>>() {
            @Override
            public void onSuccess(List<Client> clients) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        tvTotalClients.setText(String.valueOf(clients.size()));
                    });
                }
            }
            
            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        tvTotalClients.setText("0");
                    });
                }
            }
        });
    }
    
    private void loadDettes() {
        if (apiHelper == null) {
            apiHelper = new ApiHelper(getContext());
        }
        
        apiHelper.getAllDettes(new ApiHelper.DataCallback<List<Dette>>() {
            @Override
            public void onSuccess(List<Dette> dettes) {
                if (getActivity() != null) {
                    final double totalDettes = calculateTotalDettes(dettes);
                    final long enCours = countDettesEnCours(dettes);
                    final long payees = countDettesPayees(dettes);
                    
                    getActivity().runOnUiThread(() -> {
                        tvTotalDettes.setText(String.format("%,.0f CFA", totalDettes));
                        tvDettesEnCours.setText(String.valueOf(enCours));
                        tvDettesPayees.setText(String.valueOf(payees));
                    });
                }
            }
            
            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        tvTotalDettes.setText("0 CFA");
                        tvDettesEnCours.setText("0");
                        tvDettesPayees.setText("0");
                    });
                }
            }
        });
    }
    
    private double calculateTotalDettes(List<Dette> dettes) {
        double total = 0;
        if (dettes != null) {
            for (Dette dette : dettes) {
                if (!dette.isPaye()) {
                    total += dette.getMontant();
                }
            }
        }
        return total;
    }
    
    private long countDettesEnCours(List<Dette> dettes) {
        if (dettes == null) return 0;
        
        // Solution compatible avec Android (pas de stream() en API basse)
        long count = 0;
        for (Dette dette : dettes) {
            if (!dette.isPaye()) {
                count++;
            }
        }
        return count;
        
        // Si vous utilisez API 24+ vous pouvez utiliser :
        // return dettes.stream().filter(d -> !d.isPaye()).count();
    }
    
    private long countDettesPayees(List<Dette> dettes) {
        if (dettes == null) return 0;
        
        // Solution compatible avec Android (pas de stream() en API basse)
        long count = 0;
        for (Dette dette : dettes) {
            if (dette.isPaye()) {
                count++;
            }
        }
        return count;
        
        // Si vous utilisez API 24+ vous pouvez utiliser :
        // return dettes.stream().filter(Dette::isPaye).count();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Recharger les données quand le fragment revient au premier plan
        if (isAdded() && getActivity() != null) {
            loadData();
        }
    }
}