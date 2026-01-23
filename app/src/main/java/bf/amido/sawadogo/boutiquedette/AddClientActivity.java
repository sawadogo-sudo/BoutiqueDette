package bf.amido.sawadogo.boutiquedette;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ProgressBar;

public class AddClientActivity extends AppCompatActivity {

    private EditText editTextNom, editTextPrenom, editTextTelephone, 
                     editTextEmail, editTextAdresse, editTextVille;
    private Button buttonSaveClient, buttonCancel;
    private ProgressBar progressBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);
        
        initViews();
        setupListeners();
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
    
    private void setupListeners() {
        buttonSaveClient.setOnClickListener(v -> {
            if (validateForm()) {
                // Temporairement, juste un toast
                Toast.makeText(this, "Client ajouté (fonctionnalité Supabase à implémenter)", 
                    Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        
        buttonCancel.setOnClickListener(v -> {
            finish();
        });
    }
    
    private boolean validateForm() {
        String nom = editTextNom.getText().toString().trim();
        if (nom.isEmpty()) {
            editTextNom.setError("Le nom est requis");
            editTextNom.requestFocus();
            return false;
        }
        
        String telephone = editTextTelephone.getText().toString().trim();
        if (telephone.isEmpty()) {
            editTextTelephone.setError("Le téléphone est requis");
            editTextTelephone.requestFocus();
            return false;
        }
        
        return true;
    }
}