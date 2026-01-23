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

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {
    
    private Context context;
    private List<Client> clientList;
    private OnClientClickListener listener;
    
    public interface OnClientClickListener {
        void onClientClick(Client client);
        void onClientLongClick(Client client);
    }
    
    public ClientAdapter(Context context, List<Client> clientList, OnClientClickListener listener) {
        this.context = context;
        this.clientList = clientList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_client, parent, false);
        return new ClientViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        Client client = clientList.get(position);
        
        holder.textViewName.setText(client.getNom());
        holder.textViewPhone.setText(client.getTelephone());
        
        if (client.getVille() != null && !client.getVille().isEmpty()) {
            holder.textViewCity.setText(client.getVille());
            holder.textViewCity.setVisibility(View.VISIBLE);
        } else {
            holder.textViewCity.setVisibility(View.GONE);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClientClick(client);
            }
        });
        
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onClientLongClick(client);
                return true;
            }
            return false;
        });
    }
    
    @Override
    public int getItemCount() {
        return clientList.size();
    }
    
    public void updateList(List<Client> newList) {
        clientList.clear();
        clientList.addAll(newList);
        notifyDataSetChanged();
    }
    
    static class ClientViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewPhone, textViewCity;
        
        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPhone = itemView.findViewById(R.id.textViewPhone);
            textViewCity = itemView.findViewById(R.id.textViewCity);
        }
    }
}