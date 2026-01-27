package bf.amido.sawadogo.boutiquedette.models;

public class Paiement {
    private int id;
    private String clientName;
    private double montant;
    private String date;
    private String methode;
    private String reference;
    private String detteId;
    private String clientId;
    private String userId;
    private String datePaiement;
    private String modePaiement;
    private String description;
    
    public Paiement() {}
    
    public Paiement(int id, String clientName, double montant, String date, String methode, String reference) {
        this.id = id;
        this.clientName = clientName;
        this.montant = montant;
        this.date = date;
        this.methode = methode;
        this.reference = reference;
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
    
    public String getMethode() { return methode; }
    public void setMethode(String methode) { this.methode = methode; }
    
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    
    public String getDetteId() { return detteId; }
    public void setDetteId(String detteId) { this.detteId = detteId; }
    
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    // Méthodes manquantes
    public String getDatePaiement() { return datePaiement; }
    public void setDatePaiement(String datePaiement) { this.datePaiement = datePaiement; }
    
    public String getModePaiement() { return modePaiement; }
    public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}