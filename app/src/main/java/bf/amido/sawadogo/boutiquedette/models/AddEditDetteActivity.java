package bf.amido.sawadogo.boutiquedette; 

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import bf.amido.sawadogo.boutiquedette.models.Dette;
import bf.amido.sawadogo.boutiquedette.models.Client;
import bf.amido.sawadogo.boutiquedette.adapters.api.ApiHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddEditDetteActivity extends AppCompatActivity {
    
    private TextView tvTitle;
    private Spinner spinnerClient;
    private EditText etMontant, etDescription, etDateDette;
    private Button btnSave, btnCancel, btnSelectDate;
    
    private ApiHelper apiHelper;
    private String detteId;
    private boolean isEditMode = false;
    private List<Client> clientsList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_dette);
        
        apiHelper = new ApiHelper(this);
        
        initViews();
        setupButtons();
        loadClients();
        
        // Vérifier le mode (ajout ou édition)
        String mode = getIntent().getStringExtra("MODE");
        if ("EDIT".equals(mode)) {
            isEditMode = true;
            detteId = getIntent().getStringExtra("DETTE_ID");
            tvTitle.setText("Modifier Dette");
            
            // Charger les données après le chargement des clients
            new android.os.Handler().postDelayed(() -> {
                loadDetteData();
            }, 500);
        } else {
            tvTitle.setText("Nouvelle Dette");
            // Date par défaut = aujourd'hui
            etDateDette.setText(getCurrentDate());
            
            // Si on a un client pré-sélectionné
            if (getIntent().hasExtra("CLIENT_ID")) {
                String clientId = getIntent().getStringExtra("CLIENT_ID");
                // La sélection se fera dans loadClients()
            }
        }
    }
    
    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        spinnerClient = findViewById(R.id.spinnerClient);
        etMontant = findViewById(R.id.etMontant);
        etDescription = findViewById(R.id.etDescription);
        etDateDette = findViewById(R.id.etDateDette);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnSelectDate = findViewById(R.id.btnSelectDate);
    }
    
    private void setupButtons() {
        btnSave.setOnClickListener(v -> saveDette());
        btnCancel.setOnClickListener(v -> finish());
        
        btnSelectDate.setOnClickListener(v -> {
            // TODO: Implémenter un DatePickerDialog
            Toast.makeText(this, "Sélectionner une date", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void loadClients() {
        apiHelper.getAllClients(new ApiHelper.DataCallback<List<Client>>() {
            @Override
            public void onSuccess(List<Client> clients) {
                runOnUiThread(() -> {
                    clientsList = clients;
                    
                    // Créer une liste pour l'affichage (Nom + Prénom)
                    List<String> clientNames = new ArrayList<>();
                    clientNames.add("Sélectionner un client");
                    
                    for (Client client : clients) {
                        clientNames.add(client.getNom() + " " + client.getPrenom() + " - " + client.getTelephone());
                    }
                    
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        AddEditDetteActivity.this,
                        android.R.layout.simple_spinner_item,
                        clientNames
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerClient.setAdapter(adapter);
                    
                    // Si mode édition et qu'on a un client ID, on attendra loadDetteData()
                    // Si mode ajout avec client pré-sélectionné
                    if (!isEditMode && getIntent().hasExtra("CLIENT_ID")) {
                        String clientId = getIntent().getStringExtra("CLIENT_ID");
                        selectClientInSpinner(clientId);
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(AddEditDetteActivity.this, 
                        "Erreur de chargement des clients: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void selectClientInSpinner(String clientId) {
        if (clientId == null || clientId.isEmpty()) return;
        
        // Le clientId est déjà un String, pas besoin de conversion
        for (int i = 0; i < clientsList.size(); i++) {
            // CORRECTION : Utiliser equals() pour comparer des Strings
            if (clientsList.get(i).getId().equals(clientId)) {
                spinnerClient.setSelection(i + 1); // +1 pour l'élément "Sélectionner un client"
                break;
            }
        }
    }
    
    private void loadDetteData() {
        if (detteId != null && !detteId.isEmpty()) {
            apiHelper.getDetteById(detteId, new ApiHelper.DataCallback<Dette>() {
                @Override
                public void onSuccess(Dette dette) {
                    runOnUiThread(() -> {
                        if (dette != null) {
                            etMontant.setText(String.valueOf(dette.getMontant()));
                            etDescription.setText(dette.getDescription() != null ? dette.getDescription() : "");
                            etDateDette.setText(dette.getDateDette());
                            
                            // Sélectionner le client dans le spinner
                            if (dette.getClientId() != null && !dette.getClientId().isEmpty()) {
                                selectClientInSpinner(dette.getClientId());
                            }
                        } else {
                            Toast.makeText(AddEditDetteActivity.this, 
                                "Dette non trouvée", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddEditDetteActivity.this, 
                            "Erreur de chargement: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else {
            // Si on a les données dans l'intent (pour un chargement rapide)
            if (getIntent().hasExtra("DETTE_MONTANT")) {
                etMontant.setText(getIntent().getStringExtra("DETTE_MONTANT"));
                etDescription.setText(getIntent().getStringExtra("DETTE_DESCRIPTION"));
                etDateDette.setText(getIntent().getStringExtra("DETTE_DATE"));
                
                if (getIntent().hasExtra("CLIENT_ID")) {
                    String clientId = getIntent().getStringExtra("CLIENT_ID");
                    selectClientInSpinner(clientId);
                }
            }
        }
    }
    
    private void saveDette() {
        if (!validateForm()) {
            return;
        }
        
        // Vérifier qu'un client est sélectionné
        int selectedPosition = spinnerClient.getSelectedItemPosition();
        if (selectedPosition == 0) { // Premier item = "Sélectionner un client"
            Toast.makeText(this, "Veuillez sélectionner un client", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Dette dette = new Dette();
        try {
            dette.setMontant(Double.parseDouble(etMontant.getText().toString().trim()));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Montant invalide", Toast.LENGTH_SHORT).show();
            return;
        }
        dette.setDescription(etDescription.getText().toString().trim());
        dette.setDateDette(etDateDette.getText().toString().trim());
        
        // Récupérer l'ID du client sélectionné
        Client selectedClient = clientsList.get(selectedPosition - 1);
        // CORRECTION : Pas besoin de conversion, getId() retourne déjà un String
        dette.setClientId(selectedClient.getId());
        
        if (isEditMode && detteId != null && !detteId.isEmpty()) {
            // CORRECTION : setId() attend un String maintenant
            dette.setId(detteId);
            updateDetteInSupabase(dette);
        } else {
            createDetteInSupabase(dette);
        }
    }
    
    private boolean validateForm() {
        boolean isValid = true;
        
        if (etMontant.getText().toString().trim().isEmpty()) {
            etMontant.setError("Le montant est obligatoire");
            etMontant.requestFocus();
            isValid = false;
        } else {
            try {
                double montant = Double.parseDouble(etMontant.getText().toString().trim());
                if (montant <= 0) {
                    etMontant.setError("Le montant doit être supérieur à 0");
                    etMontant.requestFocus();
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                etMontant.setError("Montant invalide");
                etMontant.requestFocus();
                isValid = false;
            }
        }
        
        if (etDateDette.getText().toString().trim().isEmpty()) {
            etDateDette.setError("La date est obligatoire");
            etDateDette.requestFocus();
            isValid = false;
        }
        
        return isValid;
    }
    
    private void createDetteInSupabase(Dette dette) {
        btnSave.setEnabled(false);
        btnSave.setText("Enregistrement...");
        
        apiHelper.createDette(dette, new ApiHelper.DataCallback<Dette>() {
            @Override
            public void onSuccess(Dette createdDette) {
                runOnUiThread(() -> {
                    btnSave.setEnabled(true);
                    btnSave.setText("Enregistrer");
                    
                    Toast.makeText(AddEditDetteActivity.this, 
                        "Dette créée avec succès", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    btnSave.setEnabled(true);
                    btnSave.setText("Enregistrer");
                    
                    Toast.makeText(AddEditDetteActivity.this, 
                        "Erreur: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void updateDetteInSupabase(Dette dette) {
        btnSave.setEnabled(false);
        btnSave.setText("Mise à jour...");
        
        // CORRECTION : Pas besoin de conversion, detteId est déjà un String
        apiHelper.updateDette(detteId, dette, new ApiHelper.SimpleCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    btnSave.setEnabled(true);
                    btnSave.setText("Enregistrer");
                    
                    Toast.makeText(AddEditDetteActivity.this, 
                        "Dette mise à jour avec succès", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    btnSave.setEnabled(true);
                    btnSave.setText("Enregistrer");
                    
                    Toast.makeText(AddEditDetteActivity.this, 
                        "Erreur: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}