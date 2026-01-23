package bf.amido.sawadogo.boutiquedette;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

public class AddDetteActivity extends AppCompatActivity {
    
    private EditText editTextDescription, editTextMontant;
    private Button buttonSave;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dette);
        
        initViews();
        setupListeners();
    }
    
    private void initViews() {
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextMontant = findViewById(R.id.editTextMontant);
        buttonSave = findViewById(R.id.buttonSave);
    }
    
    private void setupListeners() {
        buttonSave.setOnClickListener(v -> {
            String description = editTextDescription.getText().toString().trim();
            String montantStr = editTextMontant.getText().toString().trim();
            
            // Validation simple
            if (description.isEmpty() || montantStr.isEmpty()) {
                Toast.makeText(AddDetteActivity.this, 
                    "Veuillez remplir tous les champs", 
                    Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                double montant = Double.parseDouble(montantStr);
                if (montant <= 0) {
                    Toast.makeText(AddDetteActivity.this, 
                        "Le montant doit être supérieur à 0", 
                        Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // TODO: Implémenter la sauvegarde de la dette
                Toast.makeText(AddDetteActivity.this, 
                    "Dette de " + montant + " CFA enregistrée pour: " + description, 
                    Toast.LENGTH_SHORT).show();
                
                // Retour à l'activité précédente
                finish();
                
            } catch (NumberFormatException e) {
                Toast.makeText(AddDetteActivity.this, 
                    "Montant invalide", 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
}