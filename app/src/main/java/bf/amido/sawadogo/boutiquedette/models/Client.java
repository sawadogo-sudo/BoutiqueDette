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
    
    @SerializedName("ville")
    private String ville;
    
    @SerializedName("adresse")
    private String adresse;
    
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("created_at")
    private String createdAt;
    
    public Client() {}
    
    public Client(String nom, String telephone) {
        this.nom = nom;
        this.telephone = telephone;
    }
    
    // Getters et setters
    public String getId() { 
        return id != null ? id : ""; 
    }
    
    public void setId(String id) { 
        this.id = id; 
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
    
    // Méthodes utilitaires
    public String getNomComplet() {
        if (prenom != null && !prenom.isEmpty() && nom != null && !nom.isEmpty()) {
            return nom + " " + prenom;
        } else if (nom != null && !nom.isEmpty()) {
            return nom;
        } else if (prenom != null && !prenom.isEmpty()) {
            return prenom;
        } else {
            return "Client #" + id;
        }
    }
    
    public boolean isValid() {
        return nom != null && !nom.trim().isEmpty();
    }
    
    public String getInfosContact() {
        StringBuilder infos = new StringBuilder();
        
        if (telephone != null && !telephone.isEmpty()) {
            infos.append("Tel: ").append(telephone);
        }
        
        if (email != null && !email.isEmpty()) {
            if (infos.length() > 0) infos.append(" | ");
            infos.append("Email: ").append(email);
        }
        
        return infos.toString();
    }
    
    @Override
    public String toString() {
        return getNomComplet() + " (" + telephone + ")";
    }
}