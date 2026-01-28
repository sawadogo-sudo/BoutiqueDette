package bf.amido.sawadogo.boutiquedette.models;

import com.google.gson.annotations.SerializedName;

public class Dette {
    @SerializedName("id")
    private String id; // Changer de int à String
    
    @SerializedName("client_id")
    private String clientId;
    
    @SerializedName("montant")
    private double montant;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("date_dette")
    private String dateDette;
    
    @SerializedName("date_echeance")
    private String dateEcheance;
    
    @SerializedName("statut")
    private String statut;
    
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("created_at")
    private String createdAt;
    
    // Champs calculés ou non persistés
    private String clientName;
    private boolean paye;
    
    public Dette() {}
    
    // Constructeur simplifié
    public Dette(String id, String clientName, double montant, String date, boolean paye) {
        this.id = id;
        this.clientName = clientName;
        this.montant = montant;
        this.dateDette = date;
        this.paye = paye;
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
    
    public String getDescription() { 
        return description != null ? description : ""; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public String getDateDette() { 
        return dateDette != null ? dateDette : ""; 
    }
    
    public void setDateDette(String dateDette) { 
        this.dateDette = dateDette; 
    }
    
    // Pour compatibilité avec l'ancien code
    public String getDate() {
        return getDateDette();
    }
    
    public void setDate(String date) {
        this.dateDette = date;
    }
    
    public String getDateEcheance() { 
        return dateEcheance != null ? dateEcheance : ""; 
    }
    
    public void setDateEcheance(String dateEcheance) { 
        this.dateEcheance = dateEcheance; 
    }
    
    public String getStatut() { 
        return statut != null ? statut : "en_cours"; 
    }
    
    public void setStatut(String statut) { 
        this.statut = statut; 
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
    
    public boolean isPaye() {
        // Si statut existe, l'utiliser pour déterminer si payé
        if (statut != null) {
            return statut.equalsIgnoreCase("paye") || statut.equalsIgnoreCase("payée");
        }
        return paye;
    }
    
    public void setPaye(boolean paye) { 
        this.paye = paye;
        // Mettre à jour le statut en conséquence
        if (paye) {
            this.statut = "paye";
        } else {
            this.statut = "en_cours";
        }
    }
    
    @Override
    public String toString() {
        return "Dette #" + id + " - " + clientName + ": " + montant + " FCFA";
    }
}