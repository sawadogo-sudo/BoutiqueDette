package bf.amido.sawadogo.boutiquedette;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import bf.amido.sawadogo.boutiquedette.models.Client;
import bf.amido.sawadogo.boutiquedette.api.ApiClient;
import bf.amido.sawadogo.boutiquedette.api.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientDetailsActivity extends AppCompatActivity {
    
    private TextView textNom, textTelephone, textAdresse, textSolde;
    private Button buttonAddDette, buttonAddPaiement, buttonHistorique;
    private ApiService apiService;
    private String clientId;
    private Client client;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_details);
        
        // Récupérer l'ID du client depuis l'intent
        Intent intent = getIntent();
        clientId = intent.getStringExtra("client_id");
        String clientNom = intent.getStringExtra("client_nom");
        
        // Initialiser les vues
        initViews();
        
        // Configurer l'API
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // Charger les données du client
        if (clientId != null) {
            loadClientDetails();
        }
        
        if (clientNom != null) {
            textNom.setText(clientNom);
        }
        
        setupListeners();
    }
    
    private void initViews() {
        textNom = findViewById(R.id.textNom);
        textTelephone = findViewById(R.id.textTelephone);
        textAdresse = findViewById(R.id.textAdresse);
        textSolde = findViewById(R.id.textSolde);
        buttonAddDette = findViewById(R.id.buttonAddDette);
        buttonAddPaiement = findViewById(R.id.buttonAddPaiement);
        buttonHistorique = findViewById(R.id.buttonHistorique);
    }
    
    private void loadClientDetails() {
        // TODO: Implémenter la récupération des détails du client depuis l'API
        // Pour l'instant, simulation
        textNom.setText("Client " + clientId);
        textTelephone.setText("+226 XX XX XX XX");
        textAdresse.setText("Adresse du client");
        textSolde.setText("0.00 CFA");
    }
    
    private void setupListeners() {
        buttonAddDette.setOnClickListener(v -> {
            Intent intent = new Intent(ClientDetailsActivity.this, AddDetteActivity.class);
            intent.putExtra("client_id", clientId);
            intent.putExtra("client_nom", textNom.getText().toString());
            startActivity(intent);
        });
        
        buttonAddPaiement.setOnClickListener(v -> {
            Intent intent = new Intent(ClientDetailsActivity.this, AddPaiementActivity.class);
            intent.putExtra("client_id", clientId);
            intent.putExtra("client_nom", textNom.getText().toString());
            startActivity(intent);
        });
        
        buttonHistorique.setOnClickListener(v -> {
            Toast.makeText(this, "Historique à implémenter", Toast.LENGTH_SHORT).show();
        });
    }
}