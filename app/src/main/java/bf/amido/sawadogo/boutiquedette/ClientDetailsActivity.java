package bf.amido.sawadogo.boutiquedette;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import bf.amido.sawadogo.boutiquedette.adapters.api.ApiClient;
import bf.amido.sawadogo.boutiquedette.adapters.api.SupabaseApiService;
import bf.amido.sawadogo.boutiquedette.models.Client;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class ClientDetailsActivity extends AppCompatActivity {
    
    private SupabaseApiService apiService;
    private TextView tvNom, tvPrenom, tvTelephone, tvEmail, tvAdresse, tvVille, tvCreatedAt;
    private Button btnEdit, btnDelete;
    private String clientId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_details);
        
        // Récupérer l'ID du client depuis l'intent
        clientId = getIntent().getStringExtra("CLIENT_ID");
        if (clientId == null || clientId.isEmpty()) {
            Toast.makeText(this, "ID client manquant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Initialiser l'API service
        apiService = ApiClient.getSupabaseService(this);
        
        // Initialiser les vues
        initViews();
        
        // Configurer les boutons
        setupButtons();
        
        // Charger les détails du client
        loadClientDetails();
    }
    
    private void initViews() {
        tvNom = findViewById(R.id.tvNom);
        tvPrenom = findViewById(R.id.tvPrenom);
        tvTelephone = findViewById(R.id.tvTelephone);
        tvEmail = findViewById(R.id.tvEmail);
        tvAdresse = findViewById(R.id.tvAdresse);
        tvVille = findViewById(R.id.tvVille); // Assurez-vous que ce TextView existe dans le layout
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
    }
    
    private void setupButtons() {
        btnEdit.setOnClickListener(v -> {
            Toast.makeText(this, "Édition à implémenter", Toast.LENGTH_SHORT).show();
        });
        
        btnDelete.setOnClickListener(v -> {
            Toast.makeText(this, "Suppression à implémenter", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void loadClientDetails() {
        String select = "nom,prenom,telephone,email,adresse,ville,created_at";
        
        apiService.getClientById(clientId, select)
            .enqueue(new Callback<List<Client>>() {
                @Override
                public void onResponse(Call<List<Client>> call, Response<List<Client>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        Client client = response.body().get(0);
                        displayClientDetails(client);
                    } else {
                        Toast.makeText(ClientDetailsActivity.this, 
                            "Client non trouvé", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                
                @Override
                public void onFailure(Call<List<Client>> call, Throwable t) {
                    Toast.makeText(ClientDetailsActivity.this, 
                        "Erreur: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }
            });
    }
    
    private void displayClientDetails(Client client) {
        tvNom.setText(client.getNom());
        tvPrenom.setText(client.getPrenom());
        tvTelephone.setText(client.getTelephone());
        
        // Gérer les valeurs nulles
        String email = client.getEmail();
        tvEmail.setText(email != null && !email.isEmpty() ? email : "Non spécifié");
        
        String adresse = client.getAdresse();
        tvAdresse.setText(adresse != null && !adresse.isEmpty() ? adresse : "Non spécifié");
        
        String ville = client.getVille();
        tvVille.setText(ville != null && !ville.isEmpty() ? ville : "Non spécifié");
        
        String createdAt = client.getCreatedAt();
        if (createdAt != null && !createdAt.isEmpty()) {
            tvCreatedAt.setText(createdAt);
        } else {
            tvCreatedAt.setVisibility(android.view.View.GONE);
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}