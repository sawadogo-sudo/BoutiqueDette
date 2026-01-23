package bf.amido.sawadogo.boutiquedette.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Paiement {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("client_id")
    private String clientId;
    
    @SerializedName("montant")
    private double montant;
    
    @SerializedName("date_paiement")
    private Date datePaiement;
    
    @SerializedName("created_at")
    private Date createdAt;
    
    // Getters et Setters...
}