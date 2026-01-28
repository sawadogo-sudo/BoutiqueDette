package bf.amido.sawadogo.boutiquedette.adapters.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SimpleAuthService {
    
    private static final String TAG = "SimpleAuth";
    private final SharedPreferences sharedPreferences;
    
    public SimpleAuthService(Context context) {
        this.sharedPreferences = context.getSharedPreferences("boutique_prefs", Context.MODE_PRIVATE);
    }
    
    public void login(String email, String password, AuthCallback callback) {
        Log.d(TAG, "Tentative de connexion: " + email);
        
        if (email.isEmpty() || password.isEmpty()) {
            callback.onError("Veuillez remplir tous les champs");
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            callback.onError("Format d'email invalide");
            return;
        }
        
        if (password.length() < 4) {
            callback.onError("Le mot de passe doit avoir au moins 4 caractères");
            return;
        }
        
        String savedEmail = sharedPreferences.getString("registered_email", "");
        String savedPassword = sharedPreferences.getString("registered_password", "");
        String savedNom = sharedPreferences.getString("registered_nom", "");
        String savedPrenom = sharedPreferences.getString("registered_prenom", "");
        
        if (!savedEmail.isEmpty() && savedEmail.equals(email)) {
            if (savedPassword.equals(password)) {
                saveUserToSession(email, savedNom, savedPrenom, "user");
                callback.onSuccess("Connexion réussie");
            } else {
                callback.onError("Mot de passe incorrect");
            }
        } else {
            callback.onError("Aucun compte trouvé avec cet email. Veuillez vous inscrire.");
        }
    }
    
    public void register(String email, String password, String nom, String prenom, AuthCallback callback) {
        Log.d(TAG, "Tentative d'inscription: " + email);
        
        if (email.isEmpty() || password.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
            callback.onError("Tous les champs sont requis");
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            callback.onError("Format d'email invalide");
            return;
        }
        
        if (password.length() < 4) {
            callback.onError("Le mot de passe doit avoir au moins 4 caractères");
            return;
        }
        
        String savedEmail = sharedPreferences.getString("registered_email", "");
        if (savedEmail.equals(email)) {
            callback.onError("Cet email est déjà utilisé");
            return;
        }
        
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("registered_email", email);
        editor.putString("registered_password", password);
        editor.putString("registered_nom", nom);
        editor.putString("registered_prenom", prenom);
        editor.apply();
        
        saveUserToSession(email, nom, prenom, "user");
        
        callback.onSuccess("Inscription réussie! Vous êtes maintenant connecté.");
    }
    
    private void saveUserToSession(String email, String nom, String prenom, String role) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", true);
        editor.putString("user_email", email);
        editor.putString("user_nom", nom);
        editor.putString("user_prenom", prenom);
        editor.putString("user_role", role);
        editor.putLong("login_time", System.currentTimeMillis());
        editor.apply();
        
        Log.d(TAG, "Session utilisateur sauvegardée: " + email);
    }
    
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false);
    }
    
    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("is_logged_in");
        editor.remove("user_email");
        editor.remove("user_nom");
        editor.remove("user_prenom");
        editor.remove("user_role");
        editor.remove("login_time");
        editor.apply();
        
        Log.d(TAG, "Utilisateur déconnecté");
    }
    
    public String getUserEmail() {
        return sharedPreferences.getString("user_email", "");
    }
    
    public String getUserName() {
        String nom = sharedPreferences.getString("user_nom", "");
        String prenom = sharedPreferences.getString("user_prenom", "");
        return nom + " " + prenom;
    }
    
    public String getUserRole() {
        return sharedPreferences.getString("user_role", "user");
    }
    
    public boolean isAdmin() {
        return "admin".equals(getUserRole());
    }
    
    public interface AuthCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}