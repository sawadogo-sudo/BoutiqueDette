package bf.amido.sawadogo.boutiquedette;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import bf.amido.sawadogo.boutiquedette.models.Client;
import bf.amido.sawadogo.boutiquedette.api.ApiHelper;

public class AddEditClientActivity extends AppCompatActivity {

    private EditText editTextNom, editTextPrenom, editTextTelephone,
            editTextEmail, editTextAdresse, editTextVille;
    private Button buttonSaveClient, buttonCancel;
    private ProgressBar progressBar;
    
    private ApiHelper apiHelper;
    private String clientId;
    private boolean isEditMode = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);
        
        apiHelper = new ApiHelper(this);
        
        initViews();
        setupListeners();
        checkMode();
    }
    
    private void initViews() {
        editTextNom = findViewById(R.id.editTextNom);
        editTextPrenom = findViewById(R.id.editTextPrenom);
        editTextTelephone = findViewById(R.id.editTextTelephone);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextAdresse = findViewById(R.id.editTextAdresse);
        editTextVille = findViewById(R.id.editTextVille);
        
        buttonSaveClient = findViewById(R.id.buttonSaveClient);
        buttonCancel = findViewById(R.id.buttonCancel);
        
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }
    
    private void checkMode() {
        String mode = getIntent().getStringExtra("MODE");
        if ("EDIT".equals(mode)) {
            isEditMode = true;
            clientId = getIntent().getStringExtra("CLIENT_ID");
            setTitle("Modifier Client");
            loadClientData();
        } else {
            setTitle("Nouveau Client");
        }
    }
    
    private void loadClientData() {
        if (getIntent().hasExtra("CLIENT_NOM")) {
            editTextNom.setText(getIntent().getStringExtra("CLIENT_NOM"));
            editTextPrenom.setText(getIntent().getStringExtra("CLIENT_PRENOM"));
            editTextTelephone.setText(getIntent().getStringExtra("CLIENT_TELEPHONE"));
            editTextEmail.setText(getIntent().getStringExtra("CLIENT_EMAIL"));
            editTextVille.setText(getIntent().getStringExtra("CLIENT_VILLE"));
            editTextAdresse.setText(getIntent().getStringExtra("CLIENT_ADRESSE"));
        } else if (clientId != null) {
            loadClientFromApi();
        }
    }
    
    private void loadClientFromApi() {
        showProgress(true);
        
        // IMPORTANT: Le Client modèle doit avoir getId() qui retourne String
        // ou nous devons adapter l'API
        apiHelper.getClientById(clientId, new ApiHelper.DataCallback<Client>() {
            @Override
            public void onSuccess(Client client) {
                runOnUiThread(() -> {
                    showProgress(false);
                    if (client != null) {
                        editTextNom.setText(client.getNom());
                        editTextPrenom.setText(client.getPrenom() != null ? client.getPrenom() : "");
                        editTextTelephone.setText(client.getTelephone());
                        editTextEmail.setText(client.getEmail() != null ? client.getEmail() : "");
                        editTextVille.setText(client.getVille() != null ? client.getVille() : "");
                        editTextAdresse.setText(client.getAdresse() != null ? client.getAdresse() : "");
                    } else {
                        Toast.makeText(AddEditClientActivity.this, 
                            "Client non trouvé", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(AddEditClientActivity.this, 
                        "Erreur de chargement: " + error, Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }
    
    private void setupListeners() {
        buttonSaveClient.setOnClickListener(v -> {
            if (validateForm()) {
                saveClient();
            }
        });

        buttonCancel.setOnClickListener(v -> {
            finish();
        });
    }
    
    private boolean validateForm() {
        boolean isValid = true;
        
        editTextNom.setError(null);
        editTextTelephone.setError(null);
        editTextEmail.setError(null);
        
        String nom = editTextNom.getText().toString().trim();
        if (nom.isEmpty()) {
            editTextNom.setError("Le nom est requis");
            editTextNom.requestFocus();
            isValid = false;
        }

        String telephone = editTextTelephone.getText().toString().trim();
        if (telephone.isEmpty()) {
            editTextTelephone.setError("Le téléphone est requis");
            editTextTelephone.requestFocus();
            isValid = false;
        } else if (!isValidPhone(telephone)) {
            editTextTelephone.setError("Format invalide (ex: 70123456)");
            editTextTelephone.requestFocus();
            isValid = false;
        }
        
        String email = editTextEmail.getText().toString().trim();
        if (!email.isEmpty() && !isValidEmail(email)) {
            editTextEmail.setError("Format d'email invalide");
            editTextEmail.requestFocus();
            isValid = false;
        }
        
        return isValid;
    }
    
    private boolean isValidPhone(String phone) {
        // Accepte: 70123456, 76123456
        String phoneRegex = "^[67]\\d{7}$";
        return phone.replaceAll("\\s+", "").matches(phoneRegex);
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
    
    private void saveClient() {
        showProgress(true);
        buttonSaveClient.setEnabled(false);
        
        Client client = new Client();
        client.setNom(editTextNom.getText().toString().trim());
        client.setPrenom(editTextPrenom.getText().toString().trim());
        client.setTelephone(editTextTelephone.getText().toString().trim());
        
        String email = editTextEmail.getText().toString().trim();
        client.setEmail(email.isEmpty() ? null : email);
        
        String ville = editTextVille.getText().toString().trim();
        client.setVille(ville.isEmpty() ? null : ville);
        
        String adresse = editTextAdresse.getText().toString().trim();
        client.setAdresse(adresse.isEmpty() ? null : adresse);
        
        if (isEditMode && clientId != null) {
            // CORRECTION : Le modèle Client doit avoir setId(String) ou on doit convertir
            try {
                // Si Client a setId(int)
                client.setId(Integer.parseInt(clientId));
                updateClientInApi(client);
            } catch (NumberFormatException e) {
                // Si Client a setId(String)
                // client.setId(clientId);
                updateClientInApi(client);
            }
        } else {
            createClientInApi(client);
        }
    }
    
    private void createClientInApi(Client client) {
        apiHelper.createClient(client, new ApiHelper.DataCallback<Client>() {
            @Override
            public void onSuccess(Client createdClient) {
                runOnUiThread(() -> {
                    showProgress(false);
                    buttonSaveClient.setEnabled(true);
                    
                    Toast.makeText(AddEditClientActivity.this, 
                        "Client créé avec succès", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showProgress(false);
                    buttonSaveClient.setEnabled(true);
                    
                    Toast.makeText(AddEditClientActivity.this, 
                        "Erreur: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void updateClientInApi(Client client) {
        // CORRECTION : L'API attend un String pour l'ID
        String idToUpdate = clientId;
        if (client.getId() != 0) {
            idToUpdate = String.valueOf(client.getId());
        }
        
        apiHelper.updateClient(idToUpdate, client, new ApiHelper.SimpleCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    showProgress(false);
                    buttonSaveClient.setEnabled(true);
                    
                    Toast.makeText(AddEditClientActivity.this, 
                        "Client mis à jour", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showProgress(false);
                    buttonSaveClient.setEnabled(true);
                    
                    Toast.makeText(AddEditClientActivity.this, 
                        "Erreur: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            buttonSaveClient.setText(isEditMode ? "Mise à jour..." : "Enregistrement...");
        } else {
            buttonSaveClient.setText(isEditMode ? "Modifier" : "Enregistrer");
        }
    }
}