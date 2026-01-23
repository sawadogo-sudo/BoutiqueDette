package bf.amido.sawadogo.boutiquedette.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import bf.amido.sawadogo.boutiquedette.models.Client;
import bf.amido.sawadogo.boutiquedette.R;

import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ViewHolder> {
    
    private Context context;
    private List<Client> clientList;
    
    public ClientAdapter(Context context, List<Client> clientList) {
        this.context = context;
        this.clientList = clientList;
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
        
        holder.textNom.setText(client.getNom());
        holder.textTelephone.setText(client.getTelephone());
        holder.textSolde.setText(String.format("%.2f F", client.getSolde()));
        
        // Changer la couleur du solde selon le montant
        if (client.getSolde() > 1000) {
            holder.textSolde.setTextColor(context.getResources().getColor(R.color.danger_color));
        } else if (client.getSolde() > 500) {
            holder.textSolde.setTextColor(context.getResources().getColor(R.color.warning_color));
        } else {
            holder.textSolde.setTextColor(context.getResources().getColor(R.color.success_color));
        }
        
        // Click listener temporaire
        holder.cardClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implémenter l'ouverture de ClientDetailsActivity
                Toast.makeText(context, "Détails de " + client.getNom(), Toast.LENGTH_SHORT).show();
                
                // Pour l'instant, afficher un simple message
                // Quand vous créerez ClientDetailsActivity, décommentez ces lignes :
                /*
                Intent intent = new Intent(context, ClientDetailsActivity.class);
                intent.putExtra("client_id", client.getId());
                intent.putExtra("client_nom", client.getNom());
                context.startActivity(intent);
                */
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return clientList.size();
    }
    
    public void updateList(List<Client> newList) {
        clientList = newList;
        notifyDataSetChanged();
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardClient;
        TextView textNom, textTelephone, textSolde;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardClient = itemView.findViewById(R.id.cardClient);
            textNom = itemView.findViewById(R.id.textNom);
            textTelephone = itemView.findViewById(R.id.textTelephone);
            textSolde = itemView.findViewById(R.id.textSolde);
        }
    }
}