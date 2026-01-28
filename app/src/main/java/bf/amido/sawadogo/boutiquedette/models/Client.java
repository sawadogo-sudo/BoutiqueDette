package bf.amido.sawadogo.boutiquedette.models;

import com.google.gson.annotations.SerializedName;

public class Client {
    @SerializedName("id")
    private String id; // Changer de int à String
    
    @SerializedName("nom")
    private String nom;
    
    @SerializedName("prenom")
    private String prenom;
    
    @SerializedName("telephone")
    private String telephone;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("ville")
    private String ville;
    
    @SerializedName("adresse")
    private String adresse;
    
    @SerializedName("dateCreation")
    private String dateCreation;
    
    @SerializedName("created_at") // Note: Supabase utilise des underscores
    private String createdAt;
    
    public Client() {}
    
    // Mettre à jour les constructeurs
    public Client(String id, String nom, String prenom, String telephone, String email, 
                  String ville, String adresse, String dateCreation) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.ville = ville;
        this.adresse = adresse;
        this.dateCreation = dateCreation;
    }
    
    // Constructeur avec createdAt
    public Client(String id, String nom, String prenom, String telephone, String email, 
                  String ville, String adresse, String dateCreation, String createdAt) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.ville = ville;
        this.adresse = adresse;
        this.dateCreation = dateCreation;
        this.createdAt = createdAt;
    }
    
    // Getters et setters
    public String getId() { 
        return id != null ? id : ""; 
    }
    
    public void setId(String id) { 
        this.id = id; 
    }
    
    // Pour la compatibilité, vous pouvez garder un getter qui retourne un int
    public int getIdAsInt() {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public String getNom() { 
        return nom != null ? nom : ""; 
    }
    
    public void setNom(String nom) { 
        this.nom = nom; 
    }
    
    public String getPrenom() { 
        return prenom != null ? prenom : ""; 
    }
    
    public void setPrenom(String prenom) { 
        this.prenom = prenom; 
    }
    
    public String getTelephone() { 
        return telephone != null ? telephone : ""; 
    }
    
    public void setTelephone(String telephone) { 
        this.telephone = telephone; 
    }
    
    public String getEmail() { 
        return email != null ? email : ""; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    public String getVille() { 
        return ville != null ? ville : ""; 
    }
    
    public void setVille(String ville) { 
        this.ville = ville; 
    }
    
    public String getAdresse() { 
        return adresse != null ? adresse : ""; 
    }
    
    public void setAdresse(String adresse) { 
        this.adresse = adresse; 
    }
    
    public String getDateCreation() { 
        return dateCreation != null ? dateCreation : ""; 
    }
    
    public void setDateCreation(String dateCreation) { 
        this.dateCreation = dateCreation; 
    }
    
    public String getCreatedAt() { 
        return createdAt != null ? createdAt : dateCreation; 
    }
    
    public void setCreatedAt(String createdAt) { 
        this.createdAt = createdAt; 
    }
    
    @Override
    public String toString() {
        return nom + " " + prenom;
    }
}