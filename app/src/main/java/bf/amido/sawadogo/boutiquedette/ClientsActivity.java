package bf.amido.sawadogo.boutiquedette;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
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
    private List<Client> allClientsList;
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
        allClientsList = new ArrayList<>();
        
        clientAdapter = new ClientAdapter(this, clientList, new ClientAdapter.OnClientClickListener() {
            @Override
            public void onClientClick(Client client) {
                Toast.makeText(ClientsActivity.this, 
                    "Client: " + client.getNom(), Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onClientLongClick(Client client) {
                Toast.makeText(ClientsActivity.this, 
                    "Long click sur: " + client.getNom(), Toast.LENGTH_SHORT).show();
            }
        });
        
        recyclerViewClients.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewClients.setAdapter(clientAdapter);
    }
    
    private void setupListeners() {
        buttonSearch.setOnClickListener(v -> {
            String searchText = editTextSearch.getText().toString().trim();
            searchClients(searchText);
        });
        
        buttonAddClient.setOnClickListener(v -> {
            Intent intent = new Intent(ClientsActivity.this, AddClientActivity.class);
            startActivity(intent);
        });
    }
    
    private void loadClients() {
        // Données de test
        clientList.clear();
        allClientsList.clear();
        
        Client client1 = new Client();
        client1.setNom("Sawadogo Oumar");
        client1.setTelephone("+226 70 12 34 56");
        client1.setVille("Ouagadougou");
        
        Client client2 = new Client();
        client2.setNom("Marie Konaté");
        client2.setTelephone("+226 65 23 45 67");
        client2.setVille("Bobo-Dioulasso");
        
        Client client3 = new Client();
        client3.setNom("Ousmane Traoré");
        client3.setTelephone("+226 76 34 56 78");
        client3.setVille("Koudougou");
        
        clientList.add(client1);
        clientList.add(client2);
        clientList.add(client3);
        
        allClientsList.addAll(clientList);
        clientAdapter.updateList(clientList);
    }
    
    private void searchClients(String query) {
        if (query.isEmpty()) {
            clientList.clear();
            clientList.addAll(allClientsList);
            clientAdapter.updateList(clientList);
            return;
        }
        
        List<Client> filteredList = new ArrayList<>();
        for (Client client : allClientsList) {
            if (client.getNom() != null && client.getNom().toLowerCase().contains(query.toLowerCase()) ||
                client.getTelephone() != null && client.getTelephone().contains(query) ||
                client.getVille() != null && client.getVille().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(client);
            }
        }
        
        clientList.clear();
        clientList.addAll(filteredList);
        clientAdapter.updateList(clientList);
        
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Aucun client trouvé", Toast.LENGTH_SHORT).show();
        }
    }
}