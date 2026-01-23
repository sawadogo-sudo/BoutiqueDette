package bf.amido.sawadogo.boutiquedette.models;

import com.google.gson.annotations.SerializedName;

public class Dette {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("client_id")
    private String clientId;
    
    @SerializedName("montant")
    private double montant;
    
    @SerializedName("date_dette")
    private String dateDette;
    
    @SerializedName("date_echeance")
    private String dateEcheance;
    
    @SerializedName("statut")
    private String statut; // "impayé", "partiel", "payé"
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("user_id")
    private String userId;
    
    // Getters et setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    
    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }
    
    public String getDateDette() { return dateDette; }
    public void setDateDette(String dateDette) { this.dateDette = dateDette; }
    
    public String getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(String dateEcheance) { this.dateEcheance = dateEcheance; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}