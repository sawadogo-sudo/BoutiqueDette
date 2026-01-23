package bf.amido.sawadogo.boutiquedette.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Client {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("nom")
    private String nom;
    
    @SerializedName("telephone")
    private String telephone;
    
    @SerializedName("adresse")
    private String adresse;
    
    @SerializedName("solde")
    private double solde;
    
    @SerializedName("created_at")
    private Date createdAt;
    
    // Constructeur par défaut
    public Client() {
        this.solde = 0.0;
        this.createdAt = new Date();
    }
    
    // Constructeur avec paramètres
    public Client(String nom, String telephone, String adresse) {
        this.nom = nom;
        this.telephone = telephone;
        this.adresse = adresse;
        this.solde = 0.0;
        this.createdAt = new Date();
    }
    
    // Getters et Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    public String getAdresse() {
        return adresse;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    
    public double getSolde() {
        return solde;
    }
    
    public void setSolde(double solde) {
        this.solde = solde;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Client{" +
                "nom='" + nom + '\'' +
                ", telephone='" + telephone + '\'' +
                ", solde=" + solde +
                '}';
    }
}