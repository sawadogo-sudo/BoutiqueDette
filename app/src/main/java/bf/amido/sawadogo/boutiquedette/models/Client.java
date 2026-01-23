package bf.amido.sawadogo.boutiquedette.models;

public class Client {
    private String id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String adresse;
    private String ville;
    private double solde; // AJOUTÉ
    private String created_at;
    
    // Constructeurs
    public Client() {}
    
    public Client(String nom, String telephone, String ville) {
        this.nom = nom;
        this.telephone = telephone;
        this.ville = ville;
    }
    
    // Getters et setters
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
    
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    
    public double getSolde() { return solde; } // AJOUTÉ
    public void setSolde(double solde) { this.solde = solde; } // AJOUTÉ
    
    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
    
    // Méthode utilitaire
    public String getFullName() {
        if (prenom != null && !prenom.isEmpty()) {
            return nom + " " + prenom;
        }
        return nom;
    }
}