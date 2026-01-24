package bf.amido.sawadogo.boutiquedette;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import bf.amido.sawadogo.boutiquedette.models.Client;
import bf.amido.sawadogo.boutiquedette.services.ApiHelper;

public class AddEditClientActivity extends AppCompatActivity {
    
    private TextView tvTitle;
    private EditText etNom, etPrenom, etTelephone, etEmail, etVille, etAdresse;
    private Button btnSave, btnCancel;
    
    private ApiHelper apiHelper;
    private String clientId;
    private boolean isEditMode = false; 
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_client);
        
        apiHelper = new ApiHelper(this);
        
        initViews();
        setupButtons();
        
        // Vérifier le mode (ajout ou édition)
        String mode = getIntent().getStringExtra("MODE");
        if ("EDIT".equals(mode)) {
            isEditMode = true;
            clientId = getIntent().getStringExtra("CLIENT_ID");
            tvTitle.setText("Modifier Client");
            loadClientData();
        } else {
            tvTitle.setText("Nouveau Client");
        }
    }
    
    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        etNom = findViewById(R.id.etNom);
        etPrenom = findViewById(R.id.etPrenom);
        etTelephone = findViewById(R.id.etTelephone);
        etEmail = findViewById(R.id.etEmail);
        etVille = findViewById(R.id.etVille);
        etAdresse = findViewById(R.id.etAdresse);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }
    
    private void setupButtons() {
        btnSave.setOnClickListener(v -> saveClient());
        btnCancel.setOnClickListener(v -> finish());
    }
    
    private void loadClientData() {
        // Si nous avons les données dans l'intent, les utiliser
        if (getIntent().hasExtra("CLIENT_NOM")) {
            etNom.setText(getIntent().getStringExtra("CLIENT_NOM"));
            etPrenom.setText(getIntent().getStringExtra("CLIENT_PRENOM"));
            etTelephone.setText(getIntent().getStringExtra("CLIENT_TELEPHONE"));
            etEmail.setText(getIntent().getStringExtra("CLIENT_EMAIL"));
            etVille.setText(getIntent().getStringExtra("CLIENT_VILLE"));
            etAdresse.setText(getIntent().getStringExtra("CLIENT_ADRESSE"));
        } else if (clientId != null) {
            // Sinon, charger depuis l'API
            loadClientFromSupabase();
        }
    }
    
    private void loadClientFromSupabase() {
        apiHelper.getClientById(clientId, new ApiHelper.DataCallback<Client>() {
            @Override
            public void onSuccess(Client client) {
                runOnUiThread(() -> {
                    if (client != null) {
                        etNom.setText(client.getNom());
                        etPrenom.setText(client.getPrenom());
                        etTelephone.setText(client.getTelephone());
                        etEmail.setText(client.getEmail() != null ? client.getEmail() : "");
                        etVille.setText(client.getVille() != null ? client.getVille() : "");
                        etAdresse.setText(client.getAdresse() != null ? client.getAdresse() : "");
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(AddEditClientActivity.this, 
                        "Erreur de chargement: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void saveClient() {
        if (!validateForm()) {
            return;
        }
        
        Client client = new Client();
        client.setNom(etNom.getText().toString().trim());
        client.setPrenom(etPrenom.getText().toString().trim());
        client.setTelephone(etTelephone.getText().toString().trim());
        client.setEmail(etEmail.getText().toString().trim());
        client.setVille(etVille.getText().toString().trim());
        client.setAdresse(etAdresse.getText().toString().trim());
        
        if (isEditMode) {
            client.setId(clientId);
            updateClientInSupabase(client);
        } else {
            createClientInSupabase(client);
        }
    }
    
    private boolean validateForm() {
        boolean isValid = true;
        
        if (etNom.getText().toString().trim().isEmpty()) {
            etNom.setError("Le nom est obligatoire");
            etNom.requestFocus();
            isValid = false;
        }
        
        if (etPrenom.getText().toString().trim().isEmpty()) {
            etPrenom.setError("Le prénom est obligatoire");
            etPrenom.requestFocus();
            isValid = false;
        }
        
        if (etTelephone.getText().toString().trim().isEmpty()) {
            etTelephone.setError("Le téléphone est obligatoire");
            etTelephone.requestFocus();
            isValid = false;
        }
        
        return isValid;
    }
    
    private void createClientInSupabase(Client client) {
        btnSave.setEnabled(false);
        btnSave.setText("Enregistrement...");
        
        apiHelper.createClient(client, new ApiHelper.DataCallback<Client>() {
            @Override
            public void onSuccess(Client createdClient) {
                runOnUiThread(() -> {
                    btnSave.setEnabled(true);
                    btnSave.setText("Enregistrer");
                    
                    Toast.makeText(AddEditClientActivity.this, 
                        "Client créé avec succès", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    btnSave.setEnabled(true);
                    btnSave.setText("Enregistrer");
                    
                    Toast.makeText(AddEditClientActivity.this, 
                        "Erreur: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void updateClientInSupabase(Client client) {
        btnSave.setEnabled(false);
        btnSave.setText("Mise à jour...");
        
        apiHelper.updateClient(client.getId(), client, new ApiHelper.SimpleCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    btnSave.setEnabled(true);
                    btnSave.setText("Enregistrer");
                    
                    Toast.makeText(AddEditClientActivity.this, 
                        "Client mis à jour avec succès", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    btnSave.setEnabled(true);
                    btnSave.setText("Enregistrer");
                    
                    Toast.makeText(AddEditClientActivity.this, 
                        "Erreur: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}