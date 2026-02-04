package bf.amido.sawadogo.boutiquedette.models;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Paiement {
    @SerializedName("id")
    private String id;
    
    @SerializedName("dette_id")
    private String detteId;
    
    @SerializedName("client_id")
    private String clientId;
    
    @SerializedName("montant")
    private double montant;
    
    @SerializedName("date_paiement")
    private String datePaiement;
    
    @SerializedName("mode_paiement")
    private String modePaiement;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("reference")
    private String reference;
    
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("created_at")
    private String createdAt;
    
    // Champs calculés ou non persistés
    private String clientName;
    private String clientTelephone;
    
    public Paiement() {
        // Date par défaut : aujourd'hui
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
        this.datePaiement = sdf.format(new Date());
        this.modePaiement = "Espèces";
        this.reference = generateReference();
    }
    
    // Constructeur simplifié pour paiement rapide
    public Paiement(String detteId, String clientId, double montant) {
        this();
        this.detteId = detteId;
        this.clientId = clientId;
        this.montant = montant;
        this.description = "Paiement de dette";
    }
    
    // Constructeur complet
    public Paiement(String detteId, String clientId, double montant, 
                   String modePaiement, String description) {
        this();
        this.detteId = detteId;
        this.clientId = clientId;
        this.montant = montant;
        this.modePaiement = modePaiement != null ? modePaiement : "Espèces";
        this.description = description != null ? description : "Paiement de dette";
    }
    
    // Getters et setters
    public String getId() { 
        return id != null ? id : ""; 
    }
    
    public void setId(String id) { 
        this.id = id; 
    }
    
    // Pour la compatibilité
    public int getIdAsInt() {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public String getDetteId() { 
        return detteId != null ? detteId : ""; 
    }
    
    public void setDetteId(String detteId) { 
        this.detteId = detteId; 
    }
    
    // Getter et setter pour client_id - TRÈS IMPORTANT
    public String getClientId() { 
        return clientId != null ? clientId : ""; 
    }
    
    public void setClientId(String clientId) { 
        this.clientId = clientId; 
    }
    
    public double getMontant() { 
        return montant; 
    }
    
    public void setMontant(double montant) { 
        this.montant = montant; 
    }
    
    public String getDatePaiement() { 
        return datePaiement != null ? datePaiement : ""; 
    }
    
    public void setDatePaiement(String datePaiement) { 
        this.datePaiement = datePaiement; 
    }
    
    // Pour compatibilité avec l'ancien code
    public String getDate() {
        return getDatePaiement();
    }
    
    public void setDate(String date) {
        this.datePaiement = date;
    }
    
    // Formatte la date pour l'affichage
    public String getDateFormatted() {
        if (datePaiement == null || datePaiement.isEmpty()) {
            return "N/A";
        }
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
            SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
            Date date = input.parse(datePaiement);
            return output.format(date);
        } catch (Exception e) {
            return datePaiement;
        }
    }
    
    public String getModePaiement() { 
        return modePaiement != null ? modePaiement : "Espèces"; 
    }
    
    public void setModePaiement(String modePaiement) { 
        this.modePaiement = modePaiement; 
    }
    
    // Pour compatibilité avec l'ancien code
    public String getMethode() {
        return getModePaiement();
    }
    
    public void setMethode(String methode) {
        this.modePaiement = methode;
    }
    
    public String getDescription() { 
        return description != null ? description : ""; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public String getReference() { 
        if (reference == null || reference.isEmpty()) {
            reference = generateReference();
        }
        return reference; 
    }
    
    public void setReference(String reference) { 
        this.reference = reference; 
    }
    
    public String getUserId() { 
        return userId != null ? userId : ""; 
    }
    
    public void setUserId(String userId) { 
        this.userId = userId; 
    }
    
    public String getCreatedAt() { 
        return createdAt != null ? createdAt : ""; 
    }
    
    public void setCreatedAt(String createdAt) { 
        this.createdAt = createdAt; 
    }
    
    // Méthodes pour les champs calculés
    public String getClientName() { 
        return clientName != null ? clientName : ""; 
    }
    
    public void setClientName(String clientName) { 
        this.clientName = clientName; 
    }
    
    public String getClientTelephone() { 
        return clientTelephone != null ? clientTelephone : ""; 
    }
    
    public void setClientTelephone(String clientTelephone) { 
        this.clientTelephone = clientTelephone; 
    }
    
    // Méthodes utilitaires
    public String getMontantFormatted() {
        return String.format("%,.0f FCFA", montant);
    }
    
    // Génère une référence unique
    private String generateReference() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.FRANCE);
        return "PAY-" + sdf.format(new Date()) + "-" + (int)(Math.random() * 1000);
    }
    
    // Vérifie si le paiement est valide
    public boolean isValid() {
        return !detteId.isEmpty() && 
               !clientId.isEmpty() && // Vérifie que client_id n'est pas vide
               montant > 0;
    }
    
    // Crée un JSONObject pour l'envoi à l'API
    public org.json.JSONObject toJsonObject() throws org.json.JSONException {
        org.json.JSONObject json = new org.json.JSONObject();
        json.put("dette_id", detteId);
        json.put("client_id", clientId); // IMPORTANT
        json.put("montant", montant);
        json.put("date_paiement", datePaiement);
        json.put("mode_paiement", modePaiement);
        
        if (description != null && !description.isEmpty()) {
            json.put("description", description);
        }
        
        if (reference != null && !reference.isEmpty()) {
            json.put("reference", reference);
        }
        
        return json;
    }
    
    // Méthode statique pour créer un paiement à partir d'une dette
    public static Paiement createFromDette(Dette dette, double montant, String modePaiement) {
        if (dette == null) {
            return null;
        }
        
        Paiement paiement = new Paiement();
        paiement.setDetteId(dette.getId());
        paiement.setClientId(dette.getClientId()); // ESSENTIEL
        paiement.setMontant(montant);
        paiement.setModePaiement(modePaiement != null ? modePaiement : "Espèces");
        paiement.setDescription("Paiement pour dette #" + dette.getId());
        paiement.setClientName(dette.getNomComplet());
        paiement.setClientTelephone(dette.getClientTelephone());
        
        return paiement;
    }
    
    @Override
    public String toString() {
        return "Paiement #" + id + 
               " | Dette: " + detteId + 
               " | Client: " + (clientName != null ? clientName : clientId) +
               " | Montant: " + getMontantFormatted() +
               " | Date: " + getDateFormatted();
    }
}