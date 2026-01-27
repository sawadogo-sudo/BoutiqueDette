package bf.amido.sawadogo.boutiquedette.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import bf.amido.sawadogo.boutiquedette.R;
import bf.amido.sawadogo.boutiquedette.models.HistoriqueItem;
import java.util.List;

public class HistoriqueAdapter extends RecyclerView.Adapter<HistoriqueAdapter.ViewHolder> {
    
    public interface OnHistoriqueClickListener {
        void onHistoriqueClick(HistoriqueItem item);
    }
    
    private Context context;
    private List<HistoriqueItem> historiqueList;
    private OnHistoriqueClickListener listener;
    
    public HistoriqueAdapter(Context context, List<HistoriqueItem> historiqueList) {
        this(context, historiqueList, null);
    }
    
    public HistoriqueAdapter(Context context, List<HistoriqueItem> historiqueList, OnHistoriqueClickListener listener) {
        this.context = context;
        this.historiqueList = historiqueList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_historique, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoriqueItem item = historiqueList.get(position);
        
        holder.tvDescription.setText(item.getDescription());
        holder.tvDate.setText(item.getDate());
        holder.tvType.setText(item.getType());
        holder.tvAction.setText(item.getAction());
        
        // Changer la couleur selon le type
        if ("client".equalsIgnoreCase(item.getType())) {
            holder.tvType.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
        } else if ("dette".equalsIgnoreCase(item.getType())) {
            holder.tvType.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        } else if ("paiement".equalsIgnoreCase(item.getType())) {
            holder.tvType.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHistoriqueClick(item);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return historiqueList != null ? historiqueList.size() : 0;
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvDate, tvType, tvAction;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvType = itemView.findViewById(R.id.tvType);
            tvAction = itemView.findViewById(R.id.tvAction);
        }
    }
    
    // Méthode pour mettre à jour la liste
    public void updateList(List<HistoriqueItem> newList) {
        historiqueList = newList;
        notifyDataSetChanged();
    }
    
    public void setHistorique(List<HistoriqueItem> historique) {
        this.historiqueList = historique;
        notifyDataSetChanged();
    }
}