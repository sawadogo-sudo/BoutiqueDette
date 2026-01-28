package bf.amido.sawadogo.boutiquedette.models;

import com.google.gson.annotations.SerializedName;

public class Paiement {
    @SerializedName("id")
    private String id; // Changer de int à String
    
    @SerializedName("dette_id")
    private String detteId;
    
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
    private String clientId;
    
    public Paiement() {}
    
    // Constructeur simplifié
    public Paiement(String id, String clientName, double montant, String date, String methode, String reference) {
        this.id = id;
        this.clientName = clientName;
        this.montant = montant;
        this.datePaiement = date;
        this.modePaiement = methode;
        this.reference = reference;
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
    
    public String getModePaiement() { 
        return modePaiement != null ? modePaiement : ""; 
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
        return reference != null ? reference : ""; 
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
    
    public String getClientId() { 
        return clientId != null ? clientId : ""; 
    }
    
    public void setClientId(String clientId) { 
        this.clientId = clientId; 
    }
    
    @Override
    public String toString() {
        return "Paiement #" + id + " - " + clientName + ": " + montant + " FCFA";
    }
}