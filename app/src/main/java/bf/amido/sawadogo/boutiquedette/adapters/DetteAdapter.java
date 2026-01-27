package bf.amido.sawadogo.boutiquedette.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import bf.amido.sawadogo.boutiquedette.R;
import bf.amido.sawadogo.boutiquedette.models.Dette;
import java.util.List;

public class DetteAdapter extends RecyclerView.Adapter<DetteAdapter.ViewHolder> {
    
    public interface OnDetteClickListener {
        void onDetteClick(Dette dette);
    }
    
    private Context context;
    private List<Dette> detteList;
    private OnDetteClickListener listener;
    
    public DetteAdapter(Context context, List<Dette> detteList) {
        this(context, detteList, null);
    }
    
    public DetteAdapter(Context context, List<Dette> detteList, OnDetteClickListener listener) {
        this.context = context;
        this.detteList = detteList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dette, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dette dette = detteList.get(position);
        
        // Utiliser les getters avec vérifications
        if (dette.getClientName() != null) {
            holder.tvClientName.setText(dette.getClientName());
        } else {
            holder.tvClientName.setText("Client inconnu");
        }
        
        holder.tvMontant.setText(String.format("%,.0f FCFA", dette.getMontant()));
        
        if (dette.getDate() != null) {
            holder.tvDate.setText(dette.getDate());
        } else {
            holder.tvDate.setText("Date inconnue");
        }
        
        // Changer la couleur selon le statut
        if (dette.isPaye()) {
            holder.tvStatut.setText("Payée");
            holder.tvStatut.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.tvStatut.setText("En cours");
            holder.tvStatut.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDetteClick(dette);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return detteList != null ? detteList.size() : 0;
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvClientName, tvMontant, tvDate, tvStatut;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialisez avec des IDs valides
            tvClientName = itemView.findViewById(R.id.tvClientName);
            tvMontant = itemView.findViewById(R.id.tvMontant);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatut = itemView.findViewById(R.id.tvStatut);
        }
    }
    
    // Méthode pour mettre à jour la liste
    public void updateList(List<Dette> newList) {
        detteList = newList;
        notifyDataSetChanged();
    }
    
    public void setDettes(List<Dette> dettes) {
        this.detteList = dettes;
        notifyDataSetChanged();
    }
}