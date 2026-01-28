package bf.amido.sawadogo.boutiquedette;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import bf.amido.sawadogo.boutiquedette.models.Dette;
import bf.amido.sawadogo.boutiquedette.models.Client;
import bf.amido.sawadogo.boutiquedette.adapters.api.ApiHelper;

public class AddDetteActivity extends AppCompatActivity {
    
    private EditText editTextDescription, editTextMontant;
    private Spinner spinnerClient, spinnerStatut;
    private DatePicker datePickerDette, datePickerEcheance;
    private Button buttonSave, buttonCancel;
    private ProgressBar progressBar;
    
    private ApiHelper apiHelper;
    private List<Client> clientsList;
    private String selectedClientId = null; // Changer de int à String
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dette);
        
        apiHelper = new ApiHelper(this);
        
        initViews();
        setupListeners();
        loadClients();
        
        // Vérifier si un client est passé en paramètre
        String clientIdStr = getIntent().getStringExtra("CLIENT_ID");
        if (clientIdStr != null && !clientIdStr.isEmpty()) {
            selectedClientId = clientIdStr; // Garder comme String
        }
    }
    
    private void initViews() {
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextMontant = findViewById(R.id.editTextMontant);
        
        spinnerClient = findViewById(R.id.spinnerClient);
        spinnerStatut = findViewById(R.id.spinnerStatut);
        
        datePickerDette = findViewById(R.id.datePickerDette);
        datePickerEcheance = findViewById(R.id.datePickerEcheance);
        
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);
        
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        
        // Configurer le spinner des statuts
        ArrayAdapter<CharSequence> statutAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.statut_dette_array,
            android.R.layout.simple_spinner_item
        );
        statutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatut.setAdapter(statutAdapter);
        
        // Définir la date d'aujourd'hui par défaut
        Calendar calendar = Calendar.getInstance();
        datePickerDette.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            null
        );
        
        // Date d'échéance par défaut (1 mois plus tard)
        calendar.add(Calendar.MONTH, 1);
        datePickerEcheance.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            null
        );
    }
    
    private void loadClients() {
        showProgress(true);
        
        apiHelper.getAllClients(new ApiHelper.DataCallback<List<Client>>() {
            @Override
            public void onSuccess(List<Client> clients) {
                runOnUiThread(() -> {
                    showProgress(false);
                    clientsList = clients;
                    setupClientSpinner(clients);
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(AddDetteActivity.this, 
                        "Erreur chargement clients: " + error, 
                        Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void setupClientSpinner(List<Client> clients) {
        if (clients == null || clients.isEmpty()) {
            String[] emptyArray = {"Aucun client disponible"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                emptyArray
            );
            spinnerClient.setAdapter(adapter);
            spinnerClient.setEnabled(false);
            return;
        }
        
        // Créer un tableau de noms pour le spinner
        String[] clientNames = new String[clients.size()];
        for (int i = 0; i < clients.size(); i++) {
            Client client = clients.get(i);
            clientNames[i] = client.getNom() + 
                (client.getPrenom() != null && !client.getPrenom().isEmpty() ? 
                    " " + client.getPrenom() : "") +
                " - " + client.getTelephone();
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            clientNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClient.setAdapter(adapter);
        
        // Sélectionner le client passé en paramètre
        if (selectedClientId != null && !selectedClientId.isEmpty()) {
            for (int i = 0; i < clients.size(); i++) {
                // Comparaison de Strings avec equals()
                if (clients.get(i).getId().equals(selectedClientId)) {
                    spinnerClient.setSelection(i);
                    break;
                }
            }
        }
    }
    
    private void setupListeners() {
        buttonSave.setOnClickListener(v -> {
            if (validateForm()) {
                saveDette();
            }
        });
        
        buttonCancel.setOnClickListener(v -> {
            finish();
        });
    }
    
    private boolean validateForm() {
        boolean isValid = true;
        
        // Vérifier qu'un client est sélectionné
        if (spinnerClient.getSelectedItemPosition() < 0 || clientsList == null || 
            spinnerClient.getSelectedItemPosition() >= clientsList.size()) {
            Toast.makeText(this, "Veuillez sélectionner un client", 
                Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        
        // Vérifier le montant
        String montantStr = editTextMontant.getText().toString().trim();
        if (montantStr.isEmpty()) {
            editTextMontant.setError("Le montant est requis");
            editTextMontant.requestFocus();
            isValid = false;
        } else {
            try {
                double montant = Double.parseDouble(montantStr);
                if (montant <= 0) {
                    editTextMontant.setError("Le montant doit être supérieur à 0");
                    editTextMontant.requestFocus();
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                editTextMontant.setError("Montant invalide");
                editTextMontant.requestFocus();
                isValid = false;
            }
        }
        
        return isValid;
    }
    
    private void saveDette() {
        showProgress(true);
        buttonSave.setEnabled(false);
        
        Dette dette = new Dette();
        
        // Client
        int selectedPosition = spinnerClient.getSelectedItemPosition();
        if (selectedPosition >= 0 && clientsList != null && 
            selectedPosition < clientsList.size()) {
            Client selectedClient = clientsList.get(selectedPosition);
            dette.setClientId(selectedClient.getId()); // Déjà un String, pas besoin de conversion
        }
        
        // Montant
        try {
            double montant = Double.parseDouble(editTextMontant.getText().toString().trim());
            dette.setMontant(montant);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Montant invalide", Toast.LENGTH_SHORT).show();
            showProgress(false);
            buttonSave.setEnabled(true);
            return;
        }
        
        // Description
        String description = editTextDescription.getText().toString().trim();
        if (!description.isEmpty()) {
            dette.setDescription(description);
        }
        
        // Dates
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        // Date de la dette
        int day = datePickerDette.getDayOfMonth();
        int month = datePickerDette.getMonth();
        int year = datePickerDette.getYear();
        Calendar calDette = Calendar.getInstance();
        calDette.set(year, month, day);
        dette.setDateDette(sdf.format(calDette.getTime()));
        
        // Date d'échéance
        day = datePickerEcheance.getDayOfMonth();
        month = datePickerEcheance.getMonth();
        year = datePickerEcheance.getYear();
        Calendar calEcheance = Calendar.getInstance();
        calEcheance.set(year, month, day);
        dette.setDateEcheance(sdf.format(calEcheance.getTime()));
        
        // Statut
        String statut = spinnerStatut.getSelectedItem().toString();
        dette.setStatut(statut);
        
        // User ID (à récupérer depuis les préférences)
        String userId = getCurrentUserId();
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            showProgress(false);
            buttonSave.setEnabled(true);
            return;
        }
        dette.setUserId(userId);
        
        // Enregistrer la dette
        apiHelper.createDette(dette, new ApiHelper.DataCallback<Dette>() {
            @Override
            public void onSuccess(Dette createdDette) {
                runOnUiThread(() -> {
                    showProgress(false);
                    buttonSave.setEnabled(true);
                    
                    Toast.makeText(AddDetteActivity.this, 
                        "Dette enregistrée avec succès", 
                        Toast.LENGTH_SHORT).show();
                    
                    setResult(RESULT_OK);
                    finish();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showProgress(false);
                    buttonSave.setEnabled(true);
                    
                    Toast.makeText(AddDetteActivity.this, 
                        "Erreur: " + error, 
                        Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private String getCurrentUserId() {
        // Récupérer l'ID utilisateur depuis SharedPreferences
        return getSharedPreferences("auth_prefs", MODE_PRIVATE)
            .getString("user_id", null);
    }
    
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        buttonSave.setText(show ? "Enregistrement..." : "Enregistrer");
    }
}