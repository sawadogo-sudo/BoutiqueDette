package bf.amido.sawadogo.boutiquedette;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AuthActivity extends AppCompatActivity {
    
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private SharedPreferences sharedPreferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        
        // Vérifier si l'utilisateur est déjà connecté
        sharedPreferences = getSharedPreferences("boutique_prefs", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("is_logged_in", false)) {
            startActivity(new Intent(AuthActivity.this, MainActivity.class));
            finish();
            return;
        }
        
        initViews();
        setupListeners();
    }
    
    private void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
    }
    
    private void setupListeners() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }
    
    private void login() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Simulation d'authentification
        if (email.equals("admin@boutique.com") && password.equals("admin123")) {
            // Sauvegarder l'état de connexion
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_logged_in", true);
            editor.putString("user_email", email);
            editor.apply();
            
            // Rediriger vers l'activité principale
            startActivity(new Intent(AuthActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
        }
    }
}