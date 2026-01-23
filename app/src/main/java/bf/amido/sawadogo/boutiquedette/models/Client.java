package bf.amido.sawadogo.boutiquedette.models;

import com.google.gson.annotations.SerializedName;

public class Client {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("nom")
    private String nom;
    
    @SerializedName("prenom")
    private String prenom;
    
    @SerializedName("telephone")
    private String telephone;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("adresse")
    private String adresse;
    
    @SerializedName("ville")  // CE CHAMP DOIT EXISTER
    private String ville;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("user_id")
    private String userId;
    
    // Constructeurs
    public Client() {
    }
    
    public Client(String nom, String prenom, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
    }
    
    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    
    // TRÈS IMPORTANT : CES MÉTHODES DOIVENT EXISTER
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    @Override
    public String toString() {
        return nom + " " + prenom;
    }
}