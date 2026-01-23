package bf.amido.sawadogo.boutiquedette;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import bf.amido.sawadogo.boutiquedette.adapters.ClientAdapter;
import bf.amido.sawadogo.boutiquedette.models.Client;

import java.util.ArrayList;
import java.util.List;

public class ClientsActivity extends AppCompatActivity {
    
    private RecyclerView recyclerViewClients;
    private ClientAdapter clientAdapter;
    private List<Client> clientList;
    private EditText editTextSearch;
    private Button buttonSearch;
    private Button buttonAddClient;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);
        
        initViews();
        setupRecyclerView();
        setupListeners();
        loadClients();
    }
    
    private void initViews() {
        recyclerViewClients = findViewById(R.id.recyclerViewClients);
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonAddClient = findViewById(R.id.buttonAddClient);
    }
    
    private void setupRecyclerView() {
        clientList = new ArrayList<>();
        clientAdapter = new ClientAdapter(this, clientList);
        
        recyclerViewClients.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewClients.setAdapter(clientAdapter);
    }
    
    private void setupListeners() {
        buttonSearch.setOnClickListener(v -> {
            String searchText = editTextSearch.getText().toString().trim();
            searchClients(searchText);
        });
        
        buttonAddClient.setOnClickListener(v -> {
            // TODO: Ouvrir l'activité d'ajout de client
            Toast.makeText(this, "Ajouter un client", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void loadClients() {
        // Données de test
        clientList.add(new Client("Sawadogo Oumar", "+226 70 12 34 56", "Ouagadougou"));
        clientList.add(new Client("Marie Konaté", "+226 65 23 45 67", "Bobo-Dioulasso"));
        clientList.add(new Client("Ousmane Traoré", "+226 76 34 56 78", "Koudougou"));
        
        clientAdapter.updateList(clientList);
    }
    
    private void searchClients(String query) {
        if (query.isEmpty()) {
            loadClients();
            return;
        }
        
        List<Client> filteredList = new ArrayList<>();
        for (Client client : clientList) {
            if (client.getNom().toLowerCase().contains(query.toLowerCase()) ||
                client.getTelephone().contains(query)) {
                filteredList.add(client);
            }
        }
        
        clientAdapter.updateList(filteredList);
    }
}