package bf.amido.sawadogo.boutiquedette.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import bf.amido.sawadogo.boutiquedette.R;
import bf.amido.sawadogo.boutiquedette.models.Client;
import java.util.ArrayList;
import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {
    
    private List<Client> clients;
    private OnItemClickListener listener;
    
    public interface OnItemClickListener {
        void onItemClick(Client client);
    }
    
    public ClientAdapter(List<Client> clients, OnItemClickListener listener) {
        this.clients = clients != null ? clients : new ArrayList<>();
        this.listener = listener;
    }
    
    public void setClients(List<Client> clients) {
        this.clients = clients != null ? clients : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void filterList(List<Client> filteredList) {
        this.clients = filteredList;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_client, parent, false);
        return new ClientViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        Client client = clients.get(position);
        
        // Nom complet
        String fullName = client.getNom() + " " + client.getPrenom();
        holder.textViewName.setText(fullName);
        
        // Téléphone
        holder.textViewPhone.setText(client.getTelephone());
        
        // Ville
        if (client.getVille() != null && !client.getVille().isEmpty()) {
            holder.textViewCity.setText(client.getVille());
            holder.textViewCity.setVisibility(View.VISIBLE);
        } else {
            holder.textViewCity.setVisibility(View.GONE);
        }
        
        // Gestion du clic
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(client);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return clients.size();
    }
    
    static class ClientViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewPhone;
        TextView textViewCity;
        
        ClientViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPhone = itemView.findViewById(R.id.textViewPhone);
            textViewCity = itemView.findViewById(R.id.textViewCity);
            // PAS DE textViewEmail ici !
        }
    }
}