package bf.amido.sawadogo.boutiquedette;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import bf.amido.sawadogo.boutiquedette.adapters.api.SimpleAuthService;

public class RegisterActivity extends AppCompatActivity {
    
    private EditText editTextNom, editTextPrenom, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonRegister;
    private SimpleAuthService authService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        // Utiliser SimpleAuthService pour l'inscription
        authService = new SimpleAuthService(this);
        
        initViews();
        setupListeners();
    }
    
    private void initViews() {
        editTextNom = findViewById(R.id.editTextNom);
        editTextPrenom = findViewById(R.id.editTextPrenom);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        
        TextView textViewLogin = findViewById(R.id.textViewLogin);
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToLogin();
            }
        });
    }
    
    private void setupListeners() {
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }
    
    private void register() {
        String nom = editTextNom.getText().toString().trim();
        String prenom = editTextPrenom.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        
        if (!validateForm(nom, prenom, email, password, confirmPassword)) {
            return;
        }
        
        // Désactiver le bouton pendant l'inscription
        buttonRegister.setEnabled(false);
        buttonRegister.setText("Inscription en cours...");
        
        authService.register(email, password, nom, prenom, new SimpleAuthService.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buttonRegister.setEnabled(true);
                        buttonRegister.setText("S'inscrire");
                        
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                        
                        // Retour à la connexion avec l'email pré-rempli
                        Intent intent = new Intent(RegisterActivity.this, AuthActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buttonRegister.setEnabled(true);
                        buttonRegister.setText("S'inscrire");
                        
                        Toast.makeText(RegisterActivity.this, "Erreur: " + error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    
    private boolean validateForm(String nom, String prenom, String email, 
                                String password, String confirmPassword) {
        boolean valid = true;
        
        if (nom.isEmpty()) {
            editTextNom.setError("Nom requis");
            editTextNom.requestFocus();
            valid = false;
        }
        
        if (prenom.isEmpty()) {
            editTextPrenom.setError("Prénom requis");
            editTextPrenom.requestFocus();
            valid = false;
        }
        
        if (email.isEmpty()) {
            editTextEmail.setError("Email requis");
            editTextEmail.requestFocus();
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Format d'email invalide");
            editTextEmail.requestFocus();
            valid = false;
        }
        
        if (password.isEmpty()) {
            editTextPassword.setError("Mot de passe requis");
            editTextPassword.requestFocus();
            valid = false;
        } else if (password.length() < 4) {
            editTextPassword.setError("4 caractères minimum");
            editTextPassword.requestFocus();
            valid = false;
        }
        
        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Les mots de passe ne correspondent pas");
            editTextConfirmPassword.requestFocus();
            valid = false;
        }
        
        return valid;
    }
    
    private void backToLogin() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }
    
    @Override
    public void onBackPressed() {
        backToLogin();
    }
}