package bf.amido.sawadogo.boutiquedette.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bf.amido.sawadogo.boutiquedette.R;
import bf.amido.sawadogo.boutiquedette.AddEditDetteActivity;
import bf.amido.sawadogo.boutiquedette.models.Dette;

public class DetteAdapter extends RecyclerView.Adapter<DetteAdapter.DetteViewHolder> {
    
    private Context context;
    private List<Dette> detteList;
    
    public DetteAdapter(Context context, List<Dette> detteList) {
        this.context = context;
        this.detteList = detteList;
    }
    
    @NonNull
    @Override
    public DetteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // VÉRIFIEZ ICI: Utilisez le bon nom de layout
        // Si votre fichier s'appelle item_dette.xml, utilisez R.layout.item_dette
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dette, parent, false); 
        return new DetteViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull DetteViewHolder holder, int position) {
        Dette dette = detteList.get(position);
        
        // 1. Nom du client
        holder.tvClientName.setText(dette.getNomComplet());
        
        // 2. Montant formaté
        holder.tvMontant.setText(dette.getMontantFormatted());
        
        // 3. Date formatée
        String dateFormatted = formatDate(dette.getDateDette());
        holder.tvDate.setText(dateFormatted);
        
        // 4. Statut
        String statutText = getStatusText(dette);
        holder.tvStatut.setText(statutText);
        
        // Appliquer la couleur de texte selon le statut
        int textColor = getStatusColor(dette);
        holder.tvStatut.setTextColor(context.getResources().getColor(textColor));
        
        // 5. Clic sur l'item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditDetteActivity.class);
            intent.putExtra("MODE", "EDIT");
            intent.putExtra("DETTE_ID", dette.getId());
            context.startActivity(intent);
        });
    }
    
    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "Date non définie";
        }
        
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
            
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateString;
        }
    }
    
    private String getStatusText(Dette dette) {
        if (dette.isPaye()) {
            return "Payé";
        } else {
            return "En cours";
        }
    }
    
    private int getStatusColor(Dette dette) {
        if (dette.isPaye()) {
            return android.R.color.holo_green_dark;
        } else {
            return android.R.color.holo_orange_dark;
        }
    }
    
    @Override
    public int getItemCount() {
        return detteList != null ? detteList.size() : 0;
    }
    
    public void updateData(List<Dette> newList) {
        detteList.clear();
        detteList.addAll(newList);
        notifyDataSetChanged();
    }
    
    static class DetteViewHolder extends RecyclerView.ViewHolder {
        TextView tvClientName, tvMontant, tvDate, tvStatut;
        
        public DetteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClientName = itemView.findViewById(R.id.tvClientName);
            tvMontant = itemView.findViewById(R.id.tvMontant);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatut = itemView.findViewById(R.id.tvStatut);
        }
    }
}