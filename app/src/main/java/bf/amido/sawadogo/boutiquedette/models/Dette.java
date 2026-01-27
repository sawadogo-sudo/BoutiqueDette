package bf.amido.sawadogo.boutiquedette.models;

public class Dette {
    private int id;
    private String clientName;
    private double montant;
    private String date;
    private boolean paye;
    private String clientId;
    private String description;
    private String dateDette;
    private String dateEcheance;
    private String statut;
    private String userId;
    private String createdAt;
    
    public Dette() {}
    
    public Dette(int id, String clientName, double montant, String date, boolean paye) {
        this.id = id;
        this.clientName = clientName;
        this.montant = montant;
        this.date = date;
        this.paye = paye;
    }
    
    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    
    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public boolean isPaye() { return paye; }
    public void setPaye(boolean paye) { this.paye = paye; }
    
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getDateDette() { return dateDette; }
    public void setDateDette(String dateDette) { this.dateDette = dateDette; }
    
    public String getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(String dateEcheance) { this.dateEcheance = dateEcheance; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}