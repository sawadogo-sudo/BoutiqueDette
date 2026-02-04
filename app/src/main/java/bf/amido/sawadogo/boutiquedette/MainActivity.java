package bf.amido.sawadogo.boutiquedette;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
    private TextView toolbarTitle;
    private ImageView imageMenu;
    private SharedPreferences sharedPreferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        sharedPreferences = getSharedPreferences("boutique_prefs", MODE_PRIVATE);
        
        // Initialiser la Toolbar
        initToolbar();
        
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        
        // Charger le fragment Dashboard par défaut
        loadFragment(new DashboardFragment());
        updateToolbarTitle("Tableau de Bord");
        
        // Gérer les clics sur la navigation
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                String title = "";
                
                // Vérifier chaque item dans l'ordre désiré
                if (item.getItemId() == R.id.nav_dashboard) {
                    fragment = new DashboardFragment();
                    title = "Tableau de Bord";
                } else if (item.getItemId() == R.id.nav_clients) {
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
        
        // Sélectionner l'item Tableau de Bord par défaut
        bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);
    }
    
    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        imageMenu = findViewById(R.id.imageMenu);
        
        // Gérer le clic sur le menu
        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }
    
    private void showPopupMenu(View view) {
        androidx.appcompat.widget.PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(this, view);
        popupMenu.inflate(R.menu.main_menu);
        
        popupMenu.setOnMenuItemClickListener(new androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                if (item.getItemId() == R.id.menu_logout) {
                    logout();
                    return true;
                } else if (item.getItemId() == R.id.menu_settings) {
                    Toast.makeText(MainActivity.this, "Paramètres", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (item.getItemId() == R.id.menu_refresh) {
                    refreshCurrentFragment();
                    return true;
                } else if (item.getItemId() == R.id.menu_profile) {
                    Toast.makeText(MainActivity.this, "Profil", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
        
        popupMenu.show();
    }
    
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit();
    }
    
    private void updateToolbarTitle(String title) {
        toolbarTitle.setText(title);
    }
    
    private void refreshCurrentFragment() {
        Fragment currentFragment = getSupportFragmentManager()
            .findFragmentById(R.id.fragment_container);
        
        if (currentFragment != null) {
            if (currentFragment instanceof DashboardFragment) {
                ((DashboardFragment) currentFragment).refreshData();
                Toast.makeText(this, "Dashboard actualisé", Toast.LENGTH_SHORT).show();
            } else if (currentFragment instanceof ClientsFragment) {
                Toast.makeText(this, "Liste des clients actualisée", Toast.LENGTH_SHORT).show();
            }
        }
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