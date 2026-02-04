package bf.amido.sawadogo.boutiquedette.models;

import com.google.gson.annotations.SerializedName;

public class Dette {
    @SerializedName("id")
    private String id;
    
    @SerializedName("client_id")
    private String clientId;
    
    @SerializedName("montant")
    private double montant;
    
    @SerializedName("montant_paye")
    private double montantPaye = 0;
    
    @SerializedName("montant_restant")
    private double montantRestant = 0;
    
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
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    // Champs pour les informations client
    @SerializedName("client_nom")
    private String clientNom;
    
    @SerializedName("client_prenom")
    private String clientPrenom;
    
    @SerializedName("client_telephone")
    private String clientTelephone;
    
    // Pour la jointure
    private Client client;
    
    public Dette() {
        this.statut = "en_cours";
    }
    
    // Getters et setters
    public String getId() { 
        return id != null ? id : ""; 
    }
    
    public void setId(String id) { 
        this.id = id; 
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
    
    public double getMontantPaye() { 
        return montantPaye; 
    }
    
    public void setMontantPaye(double montantPaye) { 
        this.montantPaye = montantPaye; 
    }
    
    public double getMontantRestant() { 
        return montantRestant; 
    }
    
    public void setMontantRestant(double montantRestant) { 
        this.montantRestant = montantRestant; 
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
    
    public String getUpdatedAt() { 
        return updatedAt != null ? updatedAt : ""; 
    }
    
    public void setUpdatedAt(String updatedAt) { 
        this.updatedAt = updatedAt; 
    }
    
    public String getClientNom() { 
        return clientNom != null ? clientNom : ""; 
    }
    
    public void setClientNom(String clientNom) { 
        this.clientNom = clientNom; 
    }
    
    public String getClientPrenom() { 
        return clientPrenom != null ? clientPrenom : ""; 
    }
    
    public void setClientPrenom(String clientPrenom) { 
        this.clientPrenom = clientPrenom; 
    }
    
    public String getClientTelephone() { 
        return clientTelephone != null ? clientTelephone : ""; 
    }
    
    public void setClientTelephone(String clientTelephone) { 
        this.clientTelephone = clientTelephone; 
    }
    
    public Client getClient() {
        return client;
    }
    
    public void setClient(Client client) {
        this.client = client;
        if (client != null) {
            this.clientNom = client.getNom();
            this.clientPrenom = client.getPrenom();
            this.clientTelephone = client.getTelephone();
        }
    }
    
    // Méthodes utilitaires
    public String getNomComplet() {
        if (client != null) {
            String nomComplet = client.getNomComplet();
            if (!nomComplet.isEmpty()) {
                return nomComplet;
            }
        }
        
        String nom = getClientNom();
        String prenom = getClientPrenom();
        
        if (!nom.isEmpty() && !prenom.isEmpty()) {
            return nom + " " + prenom;
        } else if (!nom.isEmpty()) {
            return nom;
        } else if (!prenom.isEmpty()) {
            return prenom;
        } else {
            return "Client #" + getClientId();
        }
    }
    
    public String getMontantFormatted() {
        return String.format("%,.0f FCFA", montant);
    }
    
    public String getMontantPayeFormatted() {
        return String.format("%,.0f FCFA", montantPaye);
    }
    
    public String getMontantRestantFormatted() {
        return String.format("%,.0f FCFA", montantRestant);
    }
    
    public boolean isPaye() {
        if (statut != null) {
            return statut.equalsIgnoreCase("paye") || 
                   statut.equalsIgnoreCase("payée") ||
                   statut.equalsIgnoreCase("payé");
        }
        return false;
    }
    
    public boolean isEchue() {
        return false;
    }
    
    @Override
    public String toString() {
        return "Dette #" + id + " - " + getNomComplet() + ": " + getMontantFormatted();
    }
}