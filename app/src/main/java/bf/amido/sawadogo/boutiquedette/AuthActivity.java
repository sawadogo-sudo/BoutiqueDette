package bf.amido.sawadogo.boutiquedette;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import bf.amido.sawadogo.boutiquedette.adapters.api.SimpleAuthService;

public class AuthActivity extends AppCompatActivity {
    
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    private ProgressBar progressBar;
    private SimpleAuthService authService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        
        authService = new SimpleAuthService(this);
        
        // Vérifier si déjà connecté
        if (authService.isLoggedIn()) {
            redirectToMain();
            return;
        }
        
        initViews();
        setupListeners();
    }
    
    private void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
        progressBar = findViewById(R.id.progressBar);
        
        // Cacher la progress bar initialement
        progressBar.setVisibility(View.GONE);
    }
    
    private void setupListeners() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    
    private void login() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        
        // Validation
        if (!validateInput(email, password)) {
            return;
        }
        
        // Désactiver l'interface pendant la connexion
        setLoading(true);
        
        // Appeler le service d'authentification
        authService.login(email, password, new SimpleAuthService.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoading(false);
                        Toast.makeText(AuthActivity.this, message, Toast.LENGTH_SHORT).show();
                        redirectToMain();
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoading(false);
                        
                        String fullError = error;
                        if (error.contains("Aucun compte trouvé")) {
                            fullError += "\n\nVeuillez d'abord créer un compte.";
                        }
                        
                        Toast.makeText(AuthActivity.this, fullError, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    
    private boolean validateInput(String email, String password) {
        boolean valid = true;
        
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
        
        return valid;
    }
    
    private void setLoading(boolean loading) {
        buttonLogin.setEnabled(!loading);
        textViewRegister.setEnabled(!loading);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        buttonLogin.setText(loading ? "Connexion..." : "Se connecter");
    }
    
    private void redirectToMain() {
        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}