package bf.amido.sawadogo.boutiquedette.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Dette {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("client_id")
    private String clientId;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("montant")
    private double montant;
    
    @SerializedName("date_dette")
    private Date dateDette;
    
    @SerializedName("created_at")
    private Date createdAt;
    
    // Getters et Setters...
}