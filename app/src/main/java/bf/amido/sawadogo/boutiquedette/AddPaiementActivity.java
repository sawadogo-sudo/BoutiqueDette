package bf.amido.sawadogo.boutiquedette;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import bf.amido.sawadogo.boutiquedette.models.Client;
import bf.amido.sawadogo.boutiquedette.models.Dette;
import bf.amido.sawadogo.boutiquedette.models.Paiement;
import bf.amido.sawadogo.boutiquedette.adapters.api.ApiHelper;

public class AddPaiementActivity extends AppCompatActivity {
    
    private TextView tvClientInfo, tvDetteInfo, tvSelectClient;
    private EditText etMontant, etReference, etDescription;
    private Spinner spinnerModePaiement, spinnerClient, spinnerDette;
    private DatePicker datePickerPaiement;
    private Button btnSave, btnCancel, btnSelectClient;
    private ProgressBar progressBar;
    private LinearLayout layoutClientSelection, layoutPaiementForm;
    
    private ApiHelper apiHelper;
    private String clientId;
    private String detteId;
    private double detteMontant;
    private double detteRestante;
    private Client selectedClient;
    private Dette selectedDette;
    private List<Client> clientsList;
    private List<Dette> dettesList = new ArrayList<>();
    private List<Dette> allDettesList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_paiement);
        
        apiHelper = new ApiHelper(this);
        
        // Récupérer les données de l'intent (si venant d'ailleurs)
        clientId = getIntent().getStringExtra("CLIENT_ID");
        detteId = getIntent().getStringExtra("DETTE_ID");
        
        Log.d("AddPaiement", "Intent reçu - Client ID: " + clientId + ", Dette ID: " + detteId);
        
        initViews();
        setupListeners();
        
        // Configurer la date d'aujourd'hui par défaut
        Calendar calendar = Calendar.getInstance();
        datePickerPaiement.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            null
        );
        
        // Si on a déjà un client et une dette, charger directement
        if (clientId != null && detteId != null && 
            !clientId.isEmpty() && !detteId.isEmpty()) {
            layoutClientSelection.setVisibility(View.GONE);
            layoutPaiementForm.setVisibility(View.VISIBLE);
            loadClientAndDette();
        } else {
            // Sinon, montrer la sélection
            layoutClientSelection.setVisibility(View.VISIBLE);
            layoutPaiementForm.setVisibility(View.GONE);
            loadAllClientsAndDettes();
        }
    }
    
    private void initViews() {
        tvClientInfo = findViewById(R.id.tvClientInfo);
        tvDetteInfo = findViewById(R.id.tvDetteInfo);
        tvSelectClient = findViewById(R.id.tvSelectClient);
        
        etMontant = findViewById(R.id.etMontant);
        etReference = findViewById(R.id.etReference);
        etDescription = findViewById(R.id.etDescription);
        
        spinnerModePaiement = findViewById(R.id.spinnerModePaiement);
        spinnerClient = findViewById(R.id.spinnerClient);
        spinnerDette = findViewById(R.id.spinnerDette);
        
        datePickerPaiement = findViewById(R.id.datePickerPaiement);
        
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnSelectClient = findViewById(R.id.btnSelectClient);
        
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        
        layoutClientSelection = findViewById(R.id.layoutClientSelection);
        layoutPaiementForm = findViewById(R.id.layoutPaiementForm);
        
        // Configurer le spinner des modes de paiement
        String[] modesPaiement = {"Espèces", "Mobile Money", "Carte Bancaire"};
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
        
        btnSelectClient.setOnClickListener(v -> {
            if (selectedClient != null && selectedDette != null) {
                // Passer au formulaire de paiement
                layoutClientSelection.setVisibility(View.GONE);
                layoutPaiementForm.setVisibility(View.VISIBLE);
                loadPaiementsForSelectedDette();
            } else {
                Toast.makeText(this, "Veuillez sélectionner un client et une dette", 
                    Toast.LENGTH_SHORT).show();
            }
        });
        
        // Quand on sélectionne un client, charger ses dettes
        spinnerClient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && clientsList != null && position - 1 < clientsList.size()) {
                    selectedClient = clientsList.get(position - 1);
                    Log.d("AddPaiement", "Client sélectionné: " + selectedClient.getId() + " - " + selectedClient.getNom());
                    loadDettesForClient(selectedClient.getId());
                } else {
                    selectedClient = null;
                    dettesList.clear();
                    updateDetteSpinner();
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedClient = null;
            }
        });
        
        // Quand on sélectionne une dette
        spinnerDette.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && dettesList != null && position - 1 < dettesList.size()) {
                    selectedDette = dettesList.get(position - 1);
                    Log.d("AddPaiement", "Dette sélectionnée: " + selectedDette.getId() + " - " + selectedDette.getMontant() + " FCFA");
                    updateSelectedDetteInfo();
                } else {
                    selectedDette = null;
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDette = null;
            }
        });
        
        // Limiter le montant au maximum possible
        etMontant.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String montantStr = etMontant.getText().toString().trim();
                if (!montantStr.isEmpty()) {
                    try {
                        double montant = Double.parseDouble(montantStr.replace(",", "."));
                        if (selectedDette != null && montant > detteRestante) {
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
    
    private void loadAllClientsAndDettes() {
        showProgress(true, "Chargement des données...");
        
        // Charger tous les clients d'abord
        apiHelper.getAllClients(new ApiHelper.DataCallback<List<Client>>() {
            @Override
            public void onSuccess(List<Client> clients) {
                clientsList = clients;
                Log.d("AddPaiement", clients.size() + " clients chargés");
                
                // Charger toutes les dettes
                apiHelper.getAllDettes(new ApiHelper.DataCallback<List<Dette>>() {
                    @Override
                    public void onSuccess(List<Dette> allDettes) {
                        runOnUiThread(() -> {
                            showProgress(false, "");
                            
                            allDettesList = allDettes;
                            Log.d("AddPaiement", allDettes.size() + " dettes chargées");
                            
                            // Filtrer les clients qui ont des dettes NON PAYÉES
                            List<Client> clientsWithDettes = new ArrayList<>();
                            for (Client client : clients) {
                                boolean hasUnpaidDette = false;
                                for (Dette dette : allDettes) {
                                    if (dette.getClientId().equals(client.getId()) && 
                                        !isDettePayee(dette)) {
                                        hasUnpaidDette = true;
                                        break;
                                    }
                                }
                                if (hasUnpaidDette) {
                                    clientsWithDettes.add(client);
                                }
                            }
                            
                            if (clientsWithDettes.isEmpty()) {
                                tvSelectClient.setText("Aucun client avec dette non payée trouvé");
                                spinnerClient.setEnabled(false);
                                btnSelectClient.setEnabled(false);
                                
                                // Afficher un message avec option pour continuer quand même
                                Toast.makeText(AddPaiementActivity.this, 
                                    "Aucune dette non payée. Vous pouvez quand même enregistrer un paiement.", 
                                    Toast.LENGTH_LONG).show();
                                
                                // Permettre de sélectionner tous les clients
                                updateClientSpinner(clients);
                            } else {
                                updateClientSpinner(clientsWithDettes);
                            }
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            showProgress(false, "");
                            Toast.makeText(AddPaiementActivity.this, 
                                "Erreur chargement dettes: " + error, 
                                Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showProgress(false, "");
                    Toast.makeText(AddPaiementActivity.this, 
                        "Erreur chargement clients: " + error, 
                        Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private boolean isDettePayee(Dette dette) {
        String statut = dette.getStatut();
        return statut != null && 
               (statut.equalsIgnoreCase("payé") || 
                statut.equalsIgnoreCase("payée") || 
                statut.equalsIgnoreCase("paye"));
    }
    
    private void updateClientSpinner(List<Client> clients) {
        clientsList = clients;
        
        List<String> clientNames = new ArrayList<>();
        clientNames.add("Sélectionner un client");
        
        for (Client client : clients) {
            String displayName = client.getNom();
            if (client.getPrenom() != null && !client.getPrenom().isEmpty()) {
                displayName += " " + client.getPrenom();
            }
            if (client.getTelephone() != null && !client.getTelephone().isEmpty()) {
                displayName += " - " + client.getTelephone();
            }
            clientNames.add(displayName);
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            clientNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClient.setAdapter(adapter);
        
        if (clients.isEmpty()) {
            spinnerClient.setEnabled(false);
        } else {
            spinnerClient.setEnabled(true);
        }
    }
    
    private void loadDettesForClient(String clientId) {
        showProgress(true, "Chargement des dettes...");
        
        dettesList.clear();
        
        // Filtrer les dettes de ce client depuis la liste déjà chargée
        for (Dette dette : allDettesList) {
            if (dette.getClientId().equals(clientId) && !isDettePayee(dette)) {
                dettesList.add(dette);
            }
        }
        
        runOnUiThread(() -> {
            showProgress(false, "");
            updateDetteSpinner();
            
            Log.d("AddPaiement", dettesList.size() + " dettes trouvées pour client " + clientId);
            
            if (dettesList.isEmpty()) {
                Toast.makeText(AddPaiementActivity.this, 
                    "Ce client n'a pas de dette non payée", 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateDetteSpinner() {
        if (dettesList.isEmpty()) {
            List<String> emptyList = new ArrayList<>();
            emptyList.add("Aucune dette non payée");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                emptyList
            );
            spinnerDette.setAdapter(adapter);
            spinnerDette.setEnabled(false);
            selectedDette = null;
        } else {
            List<String> detteDescriptions = new ArrayList<>();
            detteDescriptions.add("Sélectionner une dette");
            
            for (Dette dette : dettesList) {
                String description = String.format(Locale.FRANCE,
                    "Dette: %,.0f FCFA - %s",
                    dette.getMontant(),
                    dette.getStatut() != null ? dette.getStatut() : "En cours");
                
                if (dette.getDescription() != null && !dette.getDescription().isEmpty()) {
                    description += " - " + dette.getDescription();
                }
                
                detteDescriptions.add(description);
            }
            
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                detteDescriptions
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDette.setAdapter(adapter);
            spinnerDette.setEnabled(true);
            
            // Sélectionner automatiquement la première dette
            if (!dettesList.isEmpty()) {
                selectedDette = dettesList.get(0);
                updateSelectedDetteInfo();
            }
        }
    }
    
    private void updateSelectedDetteInfo() {
        if (selectedDette != null && selectedClient != null) {
            String info = String.format(Locale.FRANCE,
                "Client: %s %s\nMontant dette: %,.0f FCFA\nStatut: %s",
                selectedClient.getNom(),
                selectedClient.getPrenom() != null ? selectedClient.getPrenom() : "",
                selectedDette.getMontant(),
                selectedDette.getStatut() != null ? selectedDette.getStatut() : "En cours");
            
            tvSelectClient.setText(info);
            btnSelectClient.setEnabled(true);
        } else {
            btnSelectClient.setEnabled(false);
        }
    }
    
    private void loadClientAndDette() {
        showProgress(true, "Chargement des informations...");
        
        // Charger le client
        apiHelper.getClientById(clientId, new ApiHelper.DataCallback<Client>() {
            @Override
            public void onSuccess(Client loadedClient) {
                selectedClient = loadedClient;
                if (selectedClient != null) {
                    Log.d("AddPaiement", "Client chargé: " + selectedClient.getId() + " - " + selectedClient.getNom());
                }
                updateClientInfo();
                
                // Charger la dette
                loadDetteDetails();
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showProgress(false, "");
                    Toast.makeText(AddPaiementActivity.this, 
                        "Erreur chargement client: " + error, 
                        Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }
    
    private void loadPaiementsForSelectedDette() {
        if (selectedDette == null) {
            Toast.makeText(this, "Aucune dette sélectionnée", Toast.LENGTH_SHORT).show();
            return;
        }
        
        detteId = selectedDette.getId();
        detteMontant = selectedDette.getMontant();
        
        // VÉRIFICATION CRITIQUE : S'assurer que les IDs sont valides
        if (detteId == null || detteId.isEmpty() || selectedClient == null || selectedClient.getId() == null) {
            Toast.makeText(this, "Erreur: ID invalide", Toast.LENGTH_LONG).show();
            Log.e("AddPaiement", "ID invalide - detteId: " + detteId + ", clientId: " + 
                  (selectedClient != null ? selectedClient.getId() : "null"));
            return;
        }
        
        Log.d("AddPaiement", "Dette sélectionnée - ID: " + detteId + ", Montant: " + detteMontant);
        Log.d("AddPaiement", "Client associé - ID: " + selectedClient.getId() + ", Nom: " + selectedClient.getNom());
        
        updateClientInfo();
        updateDetteInfo();
        
        // Charger les paiements existants
        loadPaiementsExistants();
    }
    
    private void loadDetteDetails() {
        apiHelper.getDetteById(detteId, new ApiHelper.DataCallback<Dette>() {
            @Override
            public void onSuccess(Dette loadedDette) {
                selectedDette = loadedDette;
                if (selectedDette != null) {
                    detteMontant = selectedDette.getMontant();
                    Log.d("AddPaiement", "Dette chargée - ID: " + selectedDette.getId() + ", Montant: " + detteMontant);
                    
                    // Charger les paiements existants pour calculer le reste
                    loadPaiementsExistants();
                } else {
                    runOnUiThread(() -> {
                        showProgress(false, "");
                        Toast.makeText(AddPaiementActivity.this, 
                            "Dette non trouvée", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showProgress(false, "");
                    Toast.makeText(AddPaiementActivity.this, 
                        "Erreur chargement dette: " + error, 
                        Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }
    
    private void loadPaiementsExistants() {
        showProgress(true, "Calcul du reste...");
        
        apiHelper.getPaiementsByDetteId(detteId, new ApiHelper.DataCallback<List<Paiement>>() {
            @Override
            public void onSuccess(List<Paiement> paiements) {
                runOnUiThread(() -> {
                    showProgress(false, "");
                    
                    // Calculer le total des paiements
                    double totalPaiements = 0;
                    if (paiements != null) {
                        for (Paiement p : paiements) {
                            totalPaiements += p.getMontant();
                        }
                    }
                    
                    detteRestante = detteMontant - totalPaiements;
                    updateDetteInfo();
                    
                    Log.d("AddPaiement", "Reste à payer: " + detteRestante + " FCFA");
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showProgress(false, "");
                    // En cas d'erreur, considérer qu'il n'y a pas de paiements
                    detteRestante = detteMontant;
                    updateDetteInfo();
                    Log.d("AddPaiement", "Aucun paiement existant, reste = montant total: " + detteRestante);
                });
            }
        });
    }
    
    private void updateClientInfo() {
        if (selectedClient != null) {
            String clientInfo = selectedClient.getNom();
            if (selectedClient.getPrenom() != null && !selectedClient.getPrenom().isEmpty()) {
                clientInfo += " " + selectedClient.getPrenom();
            }
            if (selectedClient.getTelephone() != null && !selectedClient.getTelephone().isEmpty()) {
                clientInfo += " - " + selectedClient.getTelephone();
            }
            tvClientInfo.setText(clientInfo);
        }
    }
    
    private void updateDetteInfo() {
        if (selectedDette != null) {
            String detteInfo = String.format(Locale.FRANCE,
                "Montant total: %,.0f FCFA\nReste à payer: %,.0f FCFA",
                detteMontant, detteRestante);
            
            // Ajouter la description si elle existe
            if (selectedDette.getDescription() != null && !selectedDette.getDescription().isEmpty()) {
                detteInfo += "\nDescription: " + selectedDette.getDescription();
            }
            
            tvDetteInfo.setText(detteInfo);
            
            // Définir le montant maximum possible comme placeholder
            etMontant.setHint(String.format(Locale.FRANCE, "Max: %,.0f FCFA", detteRestante));
            
            // Si la dette est déjà payée, désactiver le formulaire
            if (detteRestante <= 0) {
                etMontant.setEnabled(false);
                btnSave.setEnabled(false);
                Toast.makeText(this, "Cette dette est déjà entièrement payée", Toast.LENGTH_LONG).show();
            } else {
                etMontant.setEnabled(true);
                btnSave.setEnabled(true);
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
        showProgress(true, "Enregistrement en cours...");
        btnSave.setEnabled(false);
        
        // VÉRIFICATION CRITIQUE : S'assurer que les IDs sont valides
        if (selectedClient == null || selectedClient.getId() == null || selectedClient.getId().isEmpty()) {
            Toast.makeText(this, "Erreur: Client invalide", Toast.LENGTH_LONG).show();
            showProgress(false, "");
            btnSave.setEnabled(true);
            return;
        }
        
        if (detteId == null || detteId.isEmpty()) {
            Toast.makeText(this, "Erreur: Dette invalide", Toast.LENGTH_LONG).show();
            showProgress(false, "");
            btnSave.setEnabled(true);
            return;
        }
        
        // Vérifier d'abord si le montant est valide
        String montantStr = etMontant.getText().toString().trim();
        double montant;
        try {
            montantStr = montantStr.replace(",", ".");
            montant = Double.parseDouble(montantStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Montant invalide", Toast.LENGTH_SHORT).show();
            showProgress(false, "");
            btnSave.setEnabled(true);
            return;
        }
        
        // Vérifier que le montant n'est pas trop élevé
        if (montant > detteRestante) {
            Toast.makeText(this, 
                String.format(Locale.FRANCE, "Le montant ne peut pas dépasser %,.0f FCFA", detteRestante), 
                Toast.LENGTH_LONG).show();
            showProgress(false, "");
            btnSave.setEnabled(true);
            return;
        }
        
        // Créer l'objet Paiement
        Paiement paiement = new Paiement();
        paiement.setDetteId(detteId);
        paiement.setClientId(selectedClient.getId());
        paiement.setMontant(montant);
        
        // Récupérer l'ID utilisateur
        String userId = apiHelper.getCurrentUserId();
        if (userId == null || userId.isEmpty()) {
            userId = "default_user"; // Valeur par défaut
        }
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
        } else {
            // Générer une référence automatique
            SimpleDateFormat sdfRef = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            paiement.setReference("PAY-" + sdfRef.format(new Date()));
        }
        
        // Description (optionnel)
        String description = etDescription.getText().toString().trim();
        if (!description.isEmpty()) {
            paiement.setDescription(description);
        } else {
            paiement.setDescription("Paiement de dette");
        }
        
        // LOG pour débogage
        Log.d("AddPaiement", "=== TENTATIVE D'ENREGISTREMENT ===");
        Log.d("AddPaiement", "Dette ID: " + paiement.getDetteId());
        Log.d("AddPaiement", "Client ID: " + paiement.getClientId());
        Log.d("AddPaiement", "User ID: " + paiement.getUserId());
        Log.d("AddPaiement", "Montant: " + paiement.getMontant());
        Log.d("AddPaiement", "Date: " + paiement.getDatePaiement());
        Log.d("AddPaiement", "Mode: " + paiement.getModePaiement());
        Log.d("AddPaiement", "Référence: " + paiement.getReference());
        
        // Enregistrer le paiement avec la méthode complète
        apiHelper.effectuerPaiementComplet(paiement, new ApiHelper.SimpleCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    showProgress(false, "");
                    Toast.makeText(AddPaiementActivity.this, message, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showProgress(false, "");
                    btnSave.setEnabled(true);
                    
                    Log.e("AddPaiement", "Erreur création paiement: " + error);
                    
                    if (error != null && error.contains("uuid")) {
                        Toast.makeText(AddPaiementActivity.this, 
                            "Erreur: ID client ou dette invalide. Veuillez réessayer.", 
                            Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AddPaiementActivity.this, 
                            "Erreur lors de l'enregistrement: " + error, 
                            Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    
    // Méthode pour vérifier si une chaîne est un UUID valide
    private boolean isValidUUID(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            return false;
        }
        try {
            // Pattern UUID v4: xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx
            // où x est hexadécimal et y est 8, 9, A, ou B
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    private void showProgress(boolean show, String message) {
        runOnUiThread(() -> {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            if (show) {
                if (btnSave != null) {
                    btnSave.setText(message);
                    btnSave.setEnabled(false);
                }
                if (btnSelectClient != null) {
                    btnSelectClient.setEnabled(false);
                }
                if (btnCancel != null) {
                    btnCancel.setEnabled(false);
                }
                if (spinnerClient != null) {
                    spinnerClient.setEnabled(false);
                }
                if (spinnerDette != null) {
                    spinnerDette.setEnabled(false);
                }
            } else {
                if (btnSave != null) {
                    btnSave.setText("Enregistrer");
                    btnSave.setEnabled(true);
                }
                if (btnSelectClient != null) {
                    btnSelectClient.setEnabled(true);
                }
                if (btnCancel != null) {
                    btnCancel.setEnabled(true);
                }
                if (spinnerClient != null) {
                    spinnerClient.setEnabled(true);
                }
                if (spinnerDette != null) {
                    spinnerDette.setEnabled(true);
                }
            }
        });
    }
}