package bf.amido.sawadogo.boutiquedette.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import bf.amido.sawadogo.boutiquedette.R;
import bf.amido.sawadogo.boutiquedette.adapters.ClientAdapter;
import bf.amido.sawadogo.boutiquedette.models.Client;
import bf.amido.sawadogo.boutiquedette.adapters.api.ApiHelper;
import java.util.ArrayList;
import java.util.List;

public class ClientsFragment extends Fragment {
    
    private RecyclerView recyclerViewClients;
    private ClientAdapter clientAdapter;
    private List<Client> clientList;
    private List<Client> originalClientList;
    private EditText editTextSearch;
    private TextView textViewEmpty;
    private Button buttonAdd, buttonEdit, buttonDelete;
    private ApiHelper apiHelper;
    private Client selectedClient = null;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, 
                             @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clients, container, false);
        
        apiHelper = new ApiHelper(requireContext());
        
        initViews(view);
        setupRecyclerView();
        setupSearch();
        setupButtons();
        
        loadClientsFromSupabase();
        
        return view;
    }
    
    private void initViews(View view) {
        recyclerViewClients = view.findViewById(R.id.recyclerViewClients);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);
        buttonAdd = view.findViewById(R.id.buttonAdd);
        buttonEdit = view.findViewById(R.id.buttonEdit);
        buttonDelete = view.findViewById(R.id.buttonDelete);
        
        clientList = new ArrayList<>();
        originalClientList = new ArrayList<>();
        
        // Désactiver les boutons Modifier/Supprimer au début
        buttonEdit.setEnabled(false);
        buttonDelete.setEnabled(false);
    }
    
    private void setupRecyclerView() {
        clientAdapter = new ClientAdapter(requireContext(), clientList, new ClientAdapter.OnClientClickListener() {
            @Override
            public void onClientClick(Client client) {
                // Sélectionner un client
                selectedClient = client;
                updateButtonStates();
                Toast.makeText(getContext(), "Sélectionné: " + client.getNom(), Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onWhatsAppClick(Client client) {
                sendWhatsAppMessage(client);
            }
        });
        
        recyclerViewClients.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewClients.setAdapter(clientAdapter);
    }
    
    private void setupSearch() {
        editTextSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterClients(s.toString());
            }
            
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }
    
    private void setupButtons() {
        // Bouton Ajouter
        buttonAdd.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), 
                bf.amido.sawadogo.boutiquedette.AddEditClientActivity.class);
            intent.putExtra("MODE", "ADD");
            startActivity(intent);
        });
        
        // Bouton Modifier
        buttonEdit.setOnClickListener(v -> {
            if (selectedClient != null) {
                Intent intent = new Intent(requireActivity(), 
                    bf.amido.sawadogo.boutiquedette.AddEditClientActivity.class);
                intent.putExtra("MODE", "EDIT");
                intent.putExtra("CLIENT_ID", selectedClient.getId());
                startActivity(intent);
                selectedClient = null;
                updateButtonStates();
            }
        });
        
        // Bouton Supprimer
        buttonDelete.setOnClickListener(v -> {
            if (selectedClient != null) {
                showDeleteConfirmationDialog(selectedClient);
            }
        });
    }
    
    private void updateButtonStates() {
        boolean isClientSelected = (selectedClient != null);
        buttonEdit.setEnabled(isClientSelected);
        buttonDelete.setEnabled(isClientSelected);
        
        if (isClientSelected) {
            buttonEdit.setAlpha(1.0f);
            buttonDelete.setAlpha(1.0f);
        } else {
            buttonEdit.setAlpha(0.5f);
            buttonDelete.setAlpha(0.5f);
        }
    }
    
    private void showDeleteConfirmationDialog(Client client) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Supprimer client")
            .setMessage("Voulez-vous supprimer " + client.getNom() + "?")
            .setPositiveButton("OUI", (dialog, which) -> deleteClient(client))
            .setNegativeButton("NON", null)
            .show();
    }
    
    private void deleteClient(Client client) {
        Toast.makeText(getContext(), "Suppression: " + client.getNom(), Toast.LENGTH_SHORT).show();
        // TODO: Ajouter l'appel API pour supprimer
        loadClientsFromSupabase();
        selectedClient = null;
        updateButtonStates();
    }
    
    private void loadClientsFromSupabase() {
        if (!isAdded()) return;
        
        textViewEmpty.setVisibility(View.VISIBLE);
        textViewEmpty.setText("Chargement...");
        recyclerViewClients.setVisibility(View.GONE);
        
        new Thread(() -> {
            apiHelper.getAllClients(new ApiHelper.DataCallback<List<Client>>() {
                @Override
                public void onSuccess(List<Client> clients) {
                    requireActivity().runOnUiThread(() -> {
                        clientList.clear();
                        originalClientList.clear();
                        
                        if (clients != null && !clients.isEmpty()) {
                            clientList.addAll(clients);
                            originalClientList.addAll(clients);
                            clientAdapter.notifyDataSetChanged();
                            
                            textViewEmpty.setVisibility(View.GONE);
                            recyclerViewClients.setVisibility(View.VISIBLE);
                        } else {
                            textViewEmpty.setVisibility(View.VISIBLE);
                            recyclerViewClients.setVisibility(View.GONE);
                            textViewEmpty.setText("Aucun client trouvé");
                        }
                    });
                }
                
                @Override
                public void onError(String error) {
                    requireActivity().runOnUiThread(() -> {
                        textViewEmpty.setVisibility(View.VISIBLE);
                        recyclerViewClients.setVisibility(View.GONE);
                        textViewEmpty.setText("Erreur de chargement");
                        Toast.makeText(requireContext(), "Erreur: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }).start();
    }
    
    private void filterClients(String query) {
        List<Client> filteredList = new ArrayList<>();
        
        if (query.isEmpty()) {
            filteredList.addAll(originalClientList);
        } else {
            String queryLower = query.toLowerCase();
            for (Client client : originalClientList) {
                String nom = client.getNom() != null ? client.getNom().toLowerCase() : "";
                String prenom = client.getPrenom() != null ? client.getPrenom().toLowerCase() : "";
                String telephone = client.getTelephone() != null ? client.getTelephone() : "";
                
                if (nom.contains(queryLower) || prenom.contains(queryLower) || telephone.contains(query)) {
                    filteredList.add(client);
                }
            }
        }
        
        clientList.clear();
        clientList.addAll(filteredList);
        clientAdapter.notifyDataSetChanged();
        
        if (filteredList.isEmpty()) {
            textViewEmpty.setVisibility(View.VISIBLE);
            recyclerViewClients.setVisibility(View.GONE);
            textViewEmpty.setText("Aucun client trouvé");
        } else {
            textViewEmpty.setVisibility(View.GONE);
            recyclerViewClients.setVisibility(View.VISIBLE);
        }
    }
    
    private void sendWhatsAppMessage(Client client) {
        String phoneNumber = client.getTelephone();
        
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Toast.makeText(requireContext(), "Pas de numéro", Toast.LENGTH_SHORT).show();
            return;
        }
        
        new Thread(() -> {
            String cleanNumber = phoneNumber.replaceAll("[^0-9+]", "");
            
            if (!cleanNumber.startsWith("+")) {
                if (cleanNumber.startsWith("0")) {
                    cleanNumber = "+226" + cleanNumber.substring(1);
                } else {
                    cleanNumber = "+" + cleanNumber;
                }
            }
            
            final String finalNumber = cleanNumber;
            
            requireActivity().runOnUiThread(() -> {
                try {
                    String message = "Bonjour " + client.getNom();
                    if (client.getPrenom() != null && !client.getPrenom().isEmpty()) {
                        message += " " + client.getPrenom();
                    }
                    message += ",\n\nMessage concernant votre dette.\nCordialement.";
                    
                    String url = "https://wa.me/" + finalNumber + "?text=" + 
                                android.net.Uri.encode(message, "UTF-8");
                    
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(android.net.Uri.parse(url));
                    startActivity(intent);
                    
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Erreur WhatsApp", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()) {
            loadClientsFromSupabase();
        }
    }
}