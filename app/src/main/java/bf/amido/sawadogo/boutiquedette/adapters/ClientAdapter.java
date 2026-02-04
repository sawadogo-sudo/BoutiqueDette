package bf.amido.sawadogo.boutiquedette.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bf.amido.sawadogo.boutiquedette.R;
import bf.amido.sawadogo.boutiquedette.models.Client;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ViewHolder> {
    
    public interface OnClientClickListener {
        void onClientClick(Client client);
        void onWhatsAppClick(Client client);
    }
    
    private Context context;
    private List<Client> clientList;
    private OnClientClickListener listener;
    
    public ClientAdapter(Context context, List<Client> clientList, OnClientClickListener listener) {
        this.context = context;
        this.clientList = clientList;
        this.listener = listener;
    }
    
    public void setClients(List<Client> clients) {
        this.clientList = clients;
        notifyDataSetChanged();
    }
    
    public void filterList(List<Client> filteredList) {
        this.clientList = filteredList;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_client, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Client client = clientList.get(position);
        
        // Nom complet
        String nomComplet = client.getNom();
        if (client.getPrenom() != null && !client.getPrenom().isEmpty()) {
            nomComplet += " " + client.getPrenom();
        }
        holder.tvClientName.setText(nomComplet);
        
        // Téléphone
        if (client.getTelephone() != null && !client.getTelephone().isEmpty()) {
            holder.tvClientPhone.setText(client.getTelephone());
            holder.tvClientPhone.setVisibility(View.VISIBLE);
        } else {
            holder.tvClientPhone.setVisibility(View.GONE);
        }
        
        // Ville (optionnel)
        if (client.getVille() != null && !client.getVille().isEmpty()) {
            holder.tvClientCity.setText(client.getVille());
            holder.tvClientCity.setVisibility(View.VISIBLE);
        } else {
            holder.tvClientCity.setVisibility(View.GONE);
        }
        
        // Bouton WhatsApp - seulement si le client a un téléphone
        if (client.getTelephone() != null && !client.getTelephone().isEmpty()) {
            holder.btnWhatsApp.setVisibility(View.VISIBLE);
            holder.btnWhatsApp.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onWhatsAppClick(client);
                }
            });
        } else {
            holder.btnWhatsApp.setVisibility(View.GONE);
        }
        
        // Click sur l'item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClientClick(client);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return clientList.size();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvClientName;
        TextView tvClientPhone;
        TextView tvClientCity;
        ImageButton btnWhatsApp;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClientName = itemView.findViewById(R.id.tvClientName);
            tvClientPhone = itemView.findViewById(R.id.tvClientPhone);
            tvClientCity = itemView.findViewById(R.id.tvClientCity);
            btnWhatsApp = itemView.findViewById(R.id.btnWhatsApp);
        }
    }
}