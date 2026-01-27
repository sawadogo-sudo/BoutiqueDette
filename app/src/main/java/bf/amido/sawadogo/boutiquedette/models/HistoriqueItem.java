package bf.amido.sawadogo.boutiquedette.models;

public class HistoriqueItem {
    private int id;
    private String type;
    private String action;
    private String description;
    private String date;
    
    public HistoriqueItem() {}
    
    public HistoriqueItem(int id, String type, String action, String description, String date) {
        this.id = id;
        this.type = type;
        this.action = action;
        this.description = description;
        this.date = date;
    }
    
    // Constructeur avec 4 paramètres pour l'utilisation actuelle
    public HistoriqueItem(String idStr, String type, String action, String description) {
        try {
            this.id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            this.id = 0;
        }
        this.type = type;
        this.action = action;
        this.description = description;
        this.date = ""; // Date vide
    }
    
    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}