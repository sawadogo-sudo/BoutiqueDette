package bf.amido.sawadogo.boutiquedette.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import bf.amido.sawadogo.boutiquedette.R;
import bf.amido.sawadogo.boutiquedette.models.Paiement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaiementAdapter extends RecyclerView.Adapter<PaiementAdapter.ViewHolder> {
    
    public interface OnPaiementClickListener {
        void onPaiementClick(Paiement paiement);
    }
    
    private Context context;
    private List<Paiement> paiementList;
    private OnPaiementClickListener listener;
    private SimpleDateFormat dateFormat;
    
    public PaiementAdapter(Context context, List<Paiement> paiementList) {
        this(context, paiementList, null);
    }
    
    public PaiementAdapter(Context context, List<Paiement> paiementList, OnPaiementClickListener listener) {
        this.context = context;
        this.paiementList = paiementList;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH);
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_paiement, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Paiement paiement = paiementList.get(position);
        
        // Utiliser les getters avec vérifications
        if (paiement.getClientName() != null) {
            holder.tvClientName.setText(paiement.getClientName());
        } else {
            holder.tvClientName.setText("Client inconnu");
        }
        
        holder.tvMontant.setText(String.format("%,.0f FCFA", paiement.getMontant()));
        
        // Formater la date
        String formattedDate = "";
        if (paiement.getDate() != null && !paiement.getDate().isEmpty()) {
            try {
                // Essayer de parser la date
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRENCH).parse(paiement.getDate());
                formattedDate = dateFormat.format(date);
            } catch (Exception e) {
                // Garder la date originale si erreur de parsing
                formattedDate = paiement.getDate();
            }
        }
        holder.tvDate.setText(formattedDate);
        
        // Afficher la méthode de paiement
        if (paiement.getMethode() != null && !paiement.getMethode().isEmpty()) {
            holder.tvMethode.setText(paiement.getMethode());
            holder.tvMethode.setVisibility(View.VISIBLE);
        } else {
            holder.tvMethode.setVisibility(View.GONE);
        }
        
        // Afficher la référence si disponible
        if (paiement.getReference() != null && !paiement.getReference().isEmpty()) {
            holder.tvReference.setText("Ref: " + paiement.getReference());
            holder.tvReference.setVisibility(View.VISIBLE);
        } else {
            holder.tvReference.setVisibility(View.GONE);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPaiementClick(paiement);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return paiementList != null ? paiementList.size() : 0;
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvClientName, tvMontant, tvDate, tvMethode, tvReference;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialisez les vues avec des IDs existants
            tvClientName = itemView.findViewById(R.id.tvClientName);
            tvMontant = itemView.findViewById(R.id.tvMontant);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvMethode = itemView.findViewById(R.id.tvMethode);
            tvReference = itemView.findViewById(R.id.tvReference);
        }
    }
    
    // Méthode pour mettre à jour la liste
    public void updateList(List<Paiement> newList) {
        paiementList = newList;
        notifyDataSetChanged();
    }
    
    public void setPaiements(List<Paiement> paiements) {
        this.paiementList = paiements;
        notifyDataSetChanged();
    }
    
    // Méthode pour filtrer les paiements par date (aujourd'hui)
    public void filterTodayPayments() {
        // Implémentez la logique de filtrage si nécessaire
        notifyDataSetChanged();
    }
}