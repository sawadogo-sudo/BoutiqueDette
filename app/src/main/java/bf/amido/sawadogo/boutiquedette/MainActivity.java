package bf.amido.sawadogo.boutiquedette;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import bf.amido.sawadogo.boutiquedette.fragments.ClientsFragment;
import bf.amido.sawadogo.boutiquedette.fragments.DashboardFragment;
import bf.amido.sawadogo.boutiquedette.fragments.DettesFragment;
import bf.amido.sawadogo.boutiquedette.fragments.HistoriqueFragment;
import bf.amido.sawadogo.boutiquedette.fragments.PaiementFragment;

public class MainActivity extends AppCompatActivity {
    
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        sharedPreferences = getSharedPreferences("boutique_prefs", MODE_PRIVATE);
        
        toolbar = findViewById(R.id.toolbar);
        // SUPPRIMEZ CETTE LIGNE : setSupportActionBar(toolbar);
        
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        
        // Charger le fragment Dashboard par défaut
        loadFragment(new DashboardFragment());
        updateToolbarTitle("Tableau de Bord");
        
        // Gérer les clics sur la navigation
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                String title = "Tableau de Bord";
                
                if (item.getItemId() == R.id.nav_clients) {
                    fragment = new ClientsFragment();
                    title = "Clients";
                } else if (item.getItemId() == R.id.nav_dettes) {
                    fragment = new DettesFragment();
                    title = "Dettes";
                } else if (item.getItemId() == R.id.nav_paiement) {
                    fragment = new PaiementFragment();
                    title = "Paiement";
                } else if (item.getItemId() == R.id.nav_historique) {
                    fragment = new HistoriqueFragment();
                    title = "Historique";
                }
                
                if (fragment != null) {
                    loadFragment(fragment);
                    updateToolbarTitle(title);
                    return true;
                }
                
                return false;
            }
        });
    }
    
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit();
    }
    
    private void updateToolbarTitle(String title) {
        // Mettez à jour le titre directement sur le Toolbar
        toolbar.setTitle(title);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            logout();
            return true;
        } else if (item.getItemId() == R.id.menu_settings) {
            Toast.makeText(this, "Paramètres", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", false);
        editor.remove("user_email");
        editor.apply();
        
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }
}