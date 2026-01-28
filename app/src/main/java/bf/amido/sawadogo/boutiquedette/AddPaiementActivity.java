package bf.amido.sawadogo.boutiquedette;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import bf.amido.sawadogo.boutiquedette.models.Dette;
import bf.amido.sawadogo.boutiquedette.models.Client;
import bf.amido.sawadogo.boutiquedette.models.Paiement;
import bf.amido.sawadogo.boutiquedette.adapters.api.ApiHelper;

public class AddPaiementActivity extends AppCompatActivity {
    
    private TextView tvClientInfo, tvDetteInfo;
    private EditText etMontant, etReference, etDescription;
    private Spinner spinnerModePaiement;
    private DatePicker datePickerPaiement;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    
    private ApiHelper apiHelper;
    private String clientId;
    private String detteId;
    private double detteMontant;
    private double detteRestante;
    private Client client;
    private Dette dette;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_paiement);
        
        apiHelper = new ApiHelper(this);
        
        // Récupérer les données de l'intent
        clientId = getIntent().getStringExtra("CLIENT_ID");
        detteId = getIntent().getStringExtra("DETTE_ID");
        
        if (clientId == null || detteId == null) {
            Toast.makeText(this, "Données invalides", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        setupListeners();
        loadClientAndDette();
        
        // Configurer la date d'aujourd'hui par défaut
        Calendar calendar = Calendar.getInstance();
        datePickerPaiement.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            null
        );
    }
    
    private void initViews() {
        tvClientInfo = findViewById(R.id.tvClientInfo);
        tvDetteInfo = findViewById(R.id.tvDetteInfo);
        
        etMontant = findViewById(R.id.etMontant);
        etReference = findViewById(R.id.etReference);
        etDescription = findViewById(R.id.etDescription);
        
        spinnerModePaiement = findViewById(R.id.spinnerModePaiement);
        
        datePickerPaiement = findViewById(R.id.datePickerPaiement);
        
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        
        // Configurer le spinner des modes de paiement
        String[] modesPaiement = {"Espèces", "Mobile Money", "Carte Bancaire", "Virement"};
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            modesPaiement
        );
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModePaiement.setAdapter(modeAdapter);
    }
    
    private void setupListeners() {
        btnSave.setOnClickListener(v -> {
            if (validateForm()) {
                savePaiement();
            }
        });
        
        btnCancel.setOnClickListener(v -> {
            finish();
        });
        
        // Limiter le montant au maximum possible
        etMontant.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String montantStr = etMontant.getText().toString().trim();
                if (!montantStr.isEmpty()) {
                    try {
                        double montant = Double.parseDouble(montantStr.replace(",", "."));
                        if (montant > detteRestante) {
                            etMontant.setText(String.format(Locale.FRANCE, "%.0f", detteRestante));
                            Toast.makeText(this, 
                                "Montant ajusté au maximum possible", 
                                Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        // Ignorer
                    }
                }
            }
        });
    }
    
    private void loadClientAndDette() {
        showProgress(true);
        
        // Charger le client
        apiHelper.getClientById(clientId, new ApiHelper.DataCallback<Client>() {
            @Override
            public void onSuccess(Client loadedClient) {
                client = loadedClient;
                updateClientInfo();
                
                // Charger la dette
                loadDetteDetails();
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(AddPaiementActivity.this, 
                        "Erreur chargement client: " + error, 
                        Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }
    
    private void loadDetteDetails() {
        apiHelper.getDetteById(detteId, new ApiHelper.DataCallback<Dette>() {
            @Override
            public void onSuccess(Dette loadedDette) {
                dette = loadedDette;
                if (dette != null) {
                    detteMontant = dette.getMontant();
                    
                    // Charger les paiements existants pour calculer le reste
                    loadPaiementsExistants();
                } else {
                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(AddPaiementActivity.this, 
                            "Dette non trouvée", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(AddPaiementActivity.this, 
                        "Erreur chargement dette: " + error, 
                        Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }
    
    private void loadPaiementsExistants() {
        apiHelper.getPaiementsByDetteId(detteId, new ApiHelper.DataCallback<List<Paiement>>() {
            @Override
            public void onSuccess(List<Paiement> paiements) {
                runOnUiThread(() -> {
                    showProgress(false);
                    
                    // Calculer le total des paiements
                    double totalPaiements = 0;
                    if (paiements != null) {
                        for (Paiement p : paiements) {
                            totalPaiements += p.getMontant();
                        }
                    }
                    
                    detteRestante = detteMontant - totalPaiements;
                    updateDetteInfo();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showProgress(false);
                    // En cas d'erreur, considérer qu'il n'y a pas de paiements
                    detteRestante = detteMontant;
                    updateDetteInfo();
                });
            }
        });
    }
    
    private void updateClientInfo() {
        if (client != null) {
            String clientInfo = client.getNom();
            if (client.getPrenom() != null && !client.getPrenom().isEmpty()) {
                clientInfo += " " + client.getPrenom();
            }
            if (client.getTelephone() != null && !client.getTelephone().isEmpty()) {
                clientInfo += " - " + client.getTelephone();
            }
            tvClientInfo.setText(clientInfo);
        }
    }
    
    private void updateDetteInfo() {
        if (dette != null) {
            String detteInfo = String.format(Locale.FRANCE,
                "Montant total: %,.0f FCFA\nReste à payer: %,.0f FCFA",
                detteMontant, detteRestante);
            tvDetteInfo.setText(detteInfo);
            
            // Définir le montant maximum possible comme placeholder
            etMontant.setHint(String.format(Locale.FRANCE, "Max: %,.0f FCFA", detteRestante));
            
            // Si la dette est déjà payée, désactiver le formulaire
            if (detteRestante <= 0) {
                etMontant.setEnabled(false);
                btnSave.setEnabled(false);
                Toast.makeText(this, "Cette dette est déjà entièrement payée", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private boolean validateForm() {
        boolean isValid = true;
        
        // Vérifier le montant
        String montantStr = etMontant.getText().toString().trim();
        if (montantStr.isEmpty()) {
            etMontant.setError("Le montant est requis");
            etMontant.requestFocus();
            isValid = false;
        } else {
            try {
                // Gérer les virgules pour les nombres français
                montantStr = montantStr.replace(",", ".");
                double montant = Double.parseDouble(montantStr);
                
                if (montant <= 0) {
                    etMontant.setError("Le montant doit être supérieur à 0");
                    etMontant.requestFocus();
                    isValid = false;
                } else if (montant > detteRestante) {
                    etMontant.setError(String.format(Locale.FRANCE,
                        "Le montant ne peut pas dépasser %,.0f FCFA", detteRestante));
                    etMontant.requestFocus();
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                etMontant.setError("Montant invalide (ex: 10000 ou 10000,50)");
                etMontant.requestFocus();
                isValid = false;
            }
        }
        
        return isValid;
    }
    
    private void savePaiement() {
        showProgress(true);
        btnSave.setEnabled(false);
        
        // Vérifier d'abord si le montant est valide
        String montantStr = etMontant.getText().toString().trim();
        double montant;
        try {
            montantStr = montantStr.replace(",", ".");
            montant = Double.parseDouble(montantStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Montant invalide", Toast.LENGTH_SHORT).show();
            showProgress(false);
            btnSave.setEnabled(true);
            return;
        }
        
        // Vérifier que le montant n'est pas trop élevé
        if (montant > detteRestante) {
            Toast.makeText(this, 
                String.format(Locale.FRANCE, "Le montant ne peut pas dépasser %,.0f FCFA", detteRestante), 
                Toast.LENGTH_LONG).show();
            showProgress(false);
            btnSave.setEnabled(true);
            return;
        }
        
        // Créer l'objet Paiement
        Paiement paiement = new Paiement();
        paiement.setDetteId(detteId);
        paiement.setClientId(clientId);
        paiement.setMontant(montant);
        
        // Récupérer l'ID utilisateur
        String userId = apiHelper.getCurrentUserId();
        paiement.setUserId(userId);
        
        // Date de paiement
        int day = datePickerPaiement.getDayOfMonth();
        int month = datePickerPaiement.getMonth();
        int year = datePickerPaiement.getYear();
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        paiement.setDatePaiement(sdf.format(cal.getTime()));
        
        // Mode de paiement
        if (spinnerModePaiement.getSelectedItem() != null) {
            String modePaiement = spinnerModePaiement.getSelectedItem().toString();
            paiement.setModePaiement(modePaiement);
        } else {
            paiement.setModePaiement("Espèces");
        }
        
        // Référence (optionnel)
        String reference = etReference.getText().toString().trim();
        if (!reference.isEmpty()) {
            paiement.setReference(reference);
        }
        
        // Description (optionnel)
        String description = etDescription.getText().toString().trim();
        if (!description.isEmpty()) {
            paiement.setDescription(description);
        }
        
        // Enregistrer le paiement
        apiHelper.createPaiement(paiement, new ApiHelper.DataCallback<Paiement>() {
            @Override
            public void onSuccess(Paiement createdPaiement) {
                runOnUiThread(() -> {
                    // Mettre à jour le statut de la dette si nécessaire
                    updateDetteStatus(montant);
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showProgress(false);
                    btnSave.setEnabled(true);
                    
                    Toast.makeText(AddPaiementActivity.this, 
                        "Erreur lors de l'enregistrement: " + error, 
                        Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void updateDetteStatus(double montantPaiement) {
        double nouveauTotalPaiements = (detteMontant - detteRestante) + montantPaiement;
        
        String nouveauStatut = "en_cours"; // Par défaut
        
        if (nouveauTotalPaiements >= detteMontant) {
            nouveauStatut = "payé";
        } else if (nouveauTotalPaiements > 0) {
            nouveauStatut = "partiel";
        }
        
        // Mettre à jour le statut de la dette
        apiHelper.updateDetteStatut(detteId, nouveauStatut, new ApiHelper.SimpleCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    showProgress(false);
                    btnSave.setEnabled(true);
                    
                    Toast.makeText(AddPaiementActivity.this, 
                        "Paiement enregistré avec succès", 
                        Toast.LENGTH_SHORT).show();
                    
                    setResult(RESULT_OK);
                    finish();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showProgress(false);
                    btnSave.setEnabled(true);
                    
                    // Le paiement a été enregistré, mais la mise à jour du statut a échoué
                    Toast.makeText(AddPaiementActivity.this, 
                        "Paiement enregistré, mais erreur mise à jour statut: " + error, 
                        Toast.LENGTH_LONG).show();
                    
                    setResult(RESULT_OK);
                    finish();
                });
            }
        });
    }
    
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            btnSave.setText("Enregistrement...");
            btnSave.setEnabled(false);
        } else {
            btnSave.setText("Enregistrer");
            btnSave.setEnabled(true);
        }
    }
}