package bf.amido.sawadogo.boutiquedette;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import bf.amido.sawadogo.boutiquedette.adapters.ClientAdapter;
import bf.amido.sawadogo.boutiquedette.models.Client;
import bf.amido.sawadogo.boutiquedette.adapters.api.ApiHelper;
import java.util.ArrayList;
import java.util.List;

public class ClientsActivity extends AppCompatActivity {
    
    private RecyclerView recyclerViewClients;
    private ClientAdapter clientAdapter;
    private List<Client> clientList;
    private List<Client> clientListFull;
    private EditText editTextSearch;
    private TextView textViewEmpty;
    private ImageButton buttonAdd;
    private ApiHelper apiHelper;
    
    // Constantes pour les requêtes
    private static final int REQUEST_ADD_CLIENT = 1;
    private static final int REQUEST_EDIT_CLIENT = 2;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);
        
        // Initialiser l'API helper
        apiHelper = new ApiHelper(this);
        
        // Initialiser les vues
        initViews();
         
        // Initialiser la liste des clients
        clientList = new ArrayList<>();
        clientListFull = new ArrayList<>();
        
        // Configurer le RecyclerView
        setupRecyclerView();
        
        // Configurer la recherche
        setupSearch();
        
        // Configurer les boutons
        setupButtons();
        
        // Charger les clients depuis Supabase
        loadClientsFromSupabase();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Recharger les données quand on revient sur l'activité
        loadClientsFromSupabase();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            // Recharger la liste après ajout/modification
            loadClientsFromSupabase();
        }
    }
    
    private void initViews() {
        recyclerViewClients = findViewById(R.id.recyclerViewClients);
        editTextSearch = findViewById(R.id.editTextSearch);
        textViewEmpty = findViewById(R.id.textViewEmpty);
        buttonAdd = findViewById(R.id.buttonAdd);
    }
    
    private void setupRecyclerView() {
        clientAdapter = new ClientAdapter(this, clientList, new ClientAdapter.OnClientClickListener() {
            @Override
            public void onClientClick(Client client) {
                // Afficher le menu d'options pour le client
                showClientOptionsDialog(client);
            }
            
            @Override
            public void onWhatsAppClick(Client client) {
                // Envoyer notification WhatsApp
                sendWhatsAppMessage(client);
            }
        });
        
        recyclerViewClients.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewClients.setAdapter(clientAdapter);
    }
    
    private void setupSearch() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterClients(s.toString());
            }
            
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    
    private void setupButtons() {
        buttonAdd.setOnClickListener(v -> {
            // Ouvrir l'activité d'ajout de client
            Intent intent = new Intent(ClientsActivity.this, AddEditClientActivity.class);
            intent.putExtra("MODE", "ADD");
            startActivityForResult(intent, REQUEST_ADD_CLIENT);
        });
    }
    
    private void loadClientsFromSupabase() {
        showLoading(true);
        
        apiHelper.getAllClients(new ApiHelper.DataCallback<List<Client>>() {
            @Override
            public void onSuccess(List<Client> clients) {
                runOnUiThread(() -> {
                    showLoading(false);
                    clientList.clear();
                    clientListFull.clear();
                    
                    if (clients != null && !clients.isEmpty()) {
                        clientList.addAll(clients);
                        clientListFull.addAll(clients);
                        clientAdapter.setClients(clientList);
                        
                        textViewEmpty.setVisibility(View.GONE);
                        recyclerViewClients.setVisibility(View.VISIBLE);
                        
                        Toast.makeText(ClientsActivity.this, 
                            clients.size() + " clients chargés", Toast.LENGTH_SHORT).show();
                    } else {
                        textViewEmpty.setVisibility(View.VISIBLE);
                        recyclerViewClients.setVisibility(View.GONE);
                        textViewEmpty.setText("Aucun client trouvé\nAppuyez sur + pour ajouter un client");
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(ClientsActivity.this, 
                        "Erreur de connexion à Supabase: " + error, Toast.LENGTH_LONG).show();
                    
                    textViewEmpty.setVisibility(View.VISIBLE);
                    recyclerViewClients.setVisibility(View.GONE);
                    textViewEmpty.setText("Erreur de connexion\nVérifiez votre connexion internet");
                });
            }
        });
    }
    
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            textViewEmpty.setVisibility(View.VISIBLE);
            textViewEmpty.setText("Chargement des clients...");
            recyclerViewClients.setVisibility(View.GONE);
        }
    }
    
    private void filterClients(String query) {
        List<Client> filteredList = new ArrayList<>();
        
        if (query.isEmpty()) {
            filteredList.addAll(clientListFull);
        } else {
            String queryLower = query.toLowerCase().trim();
            
            for (Client client : clientListFull) {
                boolean matches = (client.getNom() != null && client.getNom().toLowerCase().contains(queryLower)) ||
                                 (client.getPrenom() != null && client.getPrenom().toLowerCase().contains(queryLower)) ||
                                 (client.getTelephone() != null && client.getTelephone().contains(query)) ||
                                 (client.getEmail() != null && client.getEmail().toLowerCase().contains(queryLower)) ||
                                 (client.getVille() != null && client.getVille().toLowerCase().contains(queryLower));
                
                if (matches) {
                    filteredList.add(client);
                }
            }
        }
        
        clientAdapter.filterList(filteredList);
        
        // Afficher/cacher le message "liste vide"
        if (filteredList.isEmpty()) {
            textViewEmpty.setVisibility(View.VISIBLE);
            textViewEmpty.setText("Aucun client correspondant à la recherche");
            recyclerViewClients.setVisibility(View.GONE);
        } else {
            textViewEmpty.setVisibility(View.GONE);
            recyclerViewClients.setVisibility(View.VISIBLE);
        }
    }
    
    // ============ MÉTHODES POUR GÉRER LES ACTIONS SUR LES CLIENTS ============
    
    private void showClientOptionsDialog(Client client) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(client.getNom() + " " + client.getPrenom());
        
        String[] options = {
            "Voir détails", 
            "Modifier", 
            "Supprimer", 
            "Ajouter une dette",
            "Envoyer message WhatsApp",
            "Appeler"
        };
        
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Voir détails
                        openClientDetails(client);
                        break;
                        
                    case 1: // Modifier
                        editClient(client);
                        break;
                        
                    case 2: // Supprimer
                        showDeleteConfirmationDialog(client);
                        break;
                        
                    case 3: // Ajouter une dette
                        addDetteForClient(client);
                        break;
                        
                    case 4: // WhatsApp
                        sendWhatsAppMessage(client);
                        break;
                        
                    case 5: // Appeler
                        callClient(client);
                        break;
                }
            }
        });
        
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }
    
    private void openClientDetails(Client client) {
        Intent intent = new Intent(this, ClientDetailsActivity.class);
        intent.putExtra("CLIENT_ID", String.valueOf(client.getId()));
        startActivity(intent);
    }
    
    private void editClient(Client client) {
        Intent intent = new Intent(this, AddEditClientActivity.class);
        intent.putExtra("MODE", "EDIT");
        intent.putExtra("CLIENT_ID", String.valueOf(client.getId()));
        intent.putExtra("CLIENT_NOM", client.getNom());
        intent.putExtra("CLIENT_PRENOM", client.getPrenom());
        intent.putExtra("CLIENT_TELEPHONE", client.getTelephone());
        intent.putExtra("CLIENT_EMAIL", client.getEmail());
        intent.putExtra("CLIENT_VILLE", client.getVille());
        intent.putExtra("CLIENT_ADRESSE", client.getAdresse());
        startActivityForResult(intent, REQUEST_EDIT_CLIENT);
    }
    
    private void showDeleteConfirmationDialog(Client client) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmer la suppression");
        builder.setMessage("Êtes-vous sûr de vouloir supprimer " + 
                          client.getNom() + " " + client.getPrenom() + 
                          " ?\n\nAttention : Toutes ses dettes seront également supprimées.");
        
        builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteClientFromSupabase(client);
            }
        });
        
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }
    
    private void deleteClientFromSupabase(Client client) {
        apiHelper.deleteClient(String.valueOf(client.getId()), new ApiHelper.SimpleCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ClientsActivity.this, 
                            "Client supprimé avec succès", Toast.LENGTH_SHORT).show();
                        
                        // Retirer le client des listes locales
                        clientList.remove(client);
                        clientListFull.remove(client);
                        
                        // Mettre à jour l'adapter
                        clientAdapter.setClients(clientList);
                        
                        // Vérifier si la liste est vide
                        if (clientList.isEmpty()) {
                            textViewEmpty.setVisibility(View.VISIBLE);
                            textViewEmpty.setText("Aucun client\nAppuyez sur + pour ajouter un client");
                            recyclerViewClients.setVisibility(View.GONE);
                        }
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ClientsActivity.this, 
                            "Erreur: " + error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    
    private void addDetteForClient(Client client) {
        Intent intent = new Intent(this, AddEditDetteActivity.class);
        intent.putExtra("CLIENT_ID", String.valueOf(client.getId()));
        intent.putExtra("CLIENT_NOM", client.getNom() + " " + client.getPrenom());
        startActivity(intent);
    }
    
    // ============ MÉTHODES POUR LES NOTIFICATIONS WHATSAPP ============
    
    private void sendWhatsAppMessage(Client client) {
        String phoneNumber = client.getTelephone();
        
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Ce client n'a pas de numéro de téléphone", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Nettoyer le numéro de téléphone
        String cleanNumber = cleanPhoneNumber(phoneNumber);
        
        if (!isWhatsAppInstalled()) {
            Toast.makeText(this, "WhatsApp n'est pas installé sur votre appareil", Toast.LENGTH_LONG).show();
            return;
        }
        
        // Créer un dialogue pour choisir le type de message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Envoyer un message à " + client.getNom());
        
        String[] messageTypes = {
            "Rappel de dette",
            "Confirmation de paiement",
            "Message personnalisé"
        };
        
        builder.setItems(messageTypes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String message = "";
                
                switch (which) {
                    case 0: // Rappel de dette
                        message = "Bonjour " + client.getNom() + 
                                ",\nJe vous contacte concernant votre dette.\n" +
                                "Merci de régulariser votre situation au plus vite.\n" +
                                "Cordialement.";
                        break;
                        
                    case 1: // Confirmation de paiement
                        message = "Bonjour " + client.getNom() + 
                                ",\nJe vous confirme la réception de votre paiement.\n" +
                                "Merci pour votre confiance.\n" +
                                "Cordialement.";
                        break;
                        
                    case 2: // Message personnalisé
                        showCustomMessageDialog(client, cleanNumber);
                        return; // Ne pas envoyer tout de suite
                }
                
                openWhatsAppWithMessage(cleanNumber, message);
            }
        });
        
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }
    
    private void showCustomMessageDialog(Client client, String phoneNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Message personnalisé pour " + client.getNom());
        
        // Créer un champ de texte pour le message
        final EditText input = new EditText(this);
        input.setHint("Tapez votre message ici...");
        input.setText("Bonjour " + client.getNom() + ",\n\n");
        input.setMinHeight(200);
        builder.setView(input);
        
        builder.setPositiveButton("Envoyer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String message = input.getText().toString().trim();
                if (!message.isEmpty()) {
                    openWhatsAppWithMessage(phoneNumber, message);
                }
            }
        });
        
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }
    
    private String cleanPhoneNumber(String phone) {
        // Supprimer les espaces, tirets, parenthèses
        String cleaned = phone.replaceAll("[\\s\\-\\(\\)\\.]", "");
        
        // Ajouter l'indicatif international si nécessaire
        if (cleaned.startsWith("0")) {
            // Remplacer le 0 initial par +226 pour le Burkina Faso
            cleaned = "+226" + cleaned.substring(1);
        } else if (!cleaned.startsWith("+")) {
            // Ajouter + si absent
            cleaned = "+" + cleaned;
        }
        
        return cleaned;
    }
    
    private boolean isWhatsAppInstalled() {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            // WhatsApp Business
            try {
                pm.getPackageInfo("com.whatsapp.w4b", PackageManager.GET_ACTIVITIES);
                return true;
            } catch (PackageManager.NameNotFoundException e2) {
                return false;
            }
        }
    }
    
    private void openWhatsAppWithMessage(String phoneNumber, String message) {
        try {
            // URL pour WhatsApp avec numéro et message
            String url = "https://wa.me/" + phoneNumber + "?text=" + Uri.encode(message);
            
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            
            // Vérifier si WhatsApp peut gérer l'intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Impossible d'ouvrir WhatsApp", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    
    private void callClient(Client client) {
        String phoneNumber = client.getTelephone();
        
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Ce client n'a pas de numéro de téléphone", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Aucune application d'appel trouvée", Toast.LENGTH_SHORT).show();
        }
    }
}