package bf.amido.sawadogo.boutiquedette.models;

public class Client {
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String ville;
    private String adresse;
    private String dateCreation;
    private String createdAt; 
    
    public Client() {}
    
    public Client(int id, String nom, String prenom, String telephone, String email, 
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
    public Client(int id, String nom, String prenom, String telephone, String email, 
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
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    
    public String getDateCreation() { return dateCreation; }
    public void setDateCreation(String dateCreation) { this.dateCreation = dateCreation; }
    
    // Ajout de la méthode getCreatedAt() pour résoudre les erreurs
    public String getCreatedAt() { 
        // Si createdAt est null, retourner dateCreation comme valeur par défaut
        return createdAt != null ? createdAt : dateCreation; 
    }
    
    public void setCreatedAt(String createdAt) { 
        this.createdAt = createdAt; 
    }
}