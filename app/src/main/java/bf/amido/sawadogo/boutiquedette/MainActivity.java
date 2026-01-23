package bf.amido.sawadogo.boutiquedette;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import bf.amido.sawadogo.boutiquedette.api.ApiClient;
import bf.amido.sawadogo.boutiquedette.api.ApiService;
import bf.amido.sawadogo.boutiquedette.models.Client;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    private CardView cardClients, cardDettes, cardPaiements, cardStats;
    private TextView textTotalClients, textTotalDettes, textTopDebtors;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupApi();
        setupListeners();
        loadDashboardData();
    }
    
    private void initViews() {
        cardClients = findViewById(R.id.cardClients);
        cardDettes = findViewById(R.id.cardDettes);
        cardPaiements = findViewById(R.id.cardPaiements);
        cardStats = findViewById(R.id.cardStats);
        textTotalClients = findViewById(R.id.textTotalClients);
        textTotalDettes = findViewById(R.id.textTotalDettes);
        textTopDebtors = findViewById(R.id.textTopDebtors);
    }
    
    private void setupApi() {
        apiService = ApiClient.getClient().create(ApiService.class);
        sharedPreferences = getSharedPreferences("boutique_prefs", MODE_PRIVATE);
    }
    
    private void setupListeners() {
        cardClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ClientsActivity.class));
            }
        });
        
        findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }
    
    private void loadDashboardData() {
        // Récupérer le total des clients
        Call<List<Client>> call = apiService.getAllClients();
        call.enqueue(new Callback<List<Client>>() {
            @Override
            public void onResponse(Call<List<Client>> call, Response<List<Client>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int totalClients = response.body().size();
                    textTotalClients.setText(String.valueOf(totalClients));
                    
                    // Calculer le total des dettes
                    double totalDettes = 0;
                    for (Client client : response.body()) {
                        totalDettes += client.getSolde();
                    }
                    textTotalDettes.setText(String.format("%.2f CFA", totalDettes));
                    
                    // Trouver les 3 clients les plus endettés
                    response.body().sort((c1, c2) -> Double.compare(c2.getSolde(), c1.getSolde()));
                    StringBuilder topDebtors = new StringBuilder();
                    int limit = Math.min(3, response.body().size());
                    for (int i = 0; i < limit; i++) {
                        Client client = response.body().get(i);
                        topDebtors.append(client.getNom())
                                 .append(": ")
                                 .append(String.format("%.2f CFA", client.getSolde()))
                                 .append("\n");
                    }
                    textTopDebtors.setText(topDebtors.toString());
                }
            }
            
            @Override
            public void onFailure(Call<List<Client>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur de chargement", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        
        startActivity(new Intent(MainActivity.this, AuthActivity.class));
        finish();
    }
}