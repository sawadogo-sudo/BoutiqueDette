package bf.amido.sawadogo.boutiquedette.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import bf.amido.sawadogo.boutiquedette.R;
import bf.amido.sawadogo.boutiquedette.models.Client;
import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ViewHolder> {
    
    public interface OnClientClickListener {
        void onClientClick(Client client);
    }
    
    private Context context;
    private List<Client> clientList;
    private OnClientClickListener listener;
    
    public ClientAdapter(Context context, List<Client> clientList, OnClientClickListener listener) {
        this.context = context;
        this.clientList = clientList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_client, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Client client = clientList.get(position);
        
        holder.tvClientName.setText(client.getNom() + " " + client.getPrenom());
        holder.tvTelephone.setText(client.getTelephone());
        holder.tvEmail.setText(client.getEmail());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClientClick(client);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return clientList != null ? clientList.size() : 0;
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvClientName, tvTelephone, tvEmail;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClientName = itemView.findViewById(R.id.tvClientName);
            tvTelephone = itemView.findViewById(R.id.tvTelephone);
            tvEmail = itemView.findViewById(R.id.tvEmail);
        }
    }
    
    // Méthodes additionnelles
    public void setClients(List<Client> clients) {
        this.clientList = clients;
        notifyDataSetChanged();
    }
    
    public void filterList(List<Client> filteredList) {
        this.clientList = filteredList;
        notifyDataSetChanged();
    }
    
    public void updateList(List<Client> newList) {
        clientList = newList;
        notifyDataSetChanged();
    }
}