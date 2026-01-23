package bf.amido.sawadogo.boutiquedette;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

public class AddPaiementActivity extends AppCompatActivity {
    
    private EditText editTextMontant;
    private Button buttonSave;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_paiement);
        
        initViews();
        setupListeners();
    }
    
    private void initViews() {
        editTextMontant = findViewById(R.id.editTextMontant);
        buttonSave = findViewById(R.id.buttonSave);
    }
    
    private void setupListeners() {
        buttonSave.setOnClickListener(v -> {
            // TODO: Implémenter la sauvegarde du paiement
            Toast.makeText(this, "Fonctionnalité à implémenter", Toast.LENGTH_SHORT).show();
        });
    }
}
        
        // Appeler l'API pour authentifier l'utilisateur