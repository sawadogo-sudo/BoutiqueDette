package bf.amido.sawadogo.boutiquedette.api;

public class SupabaseConfig {
    
    // ============ CONFIGURATION SUPRABASE ============
    
    // VOTRE PUBLISHABLE KEY (remplacez par votre clé réelle)
    public static final String API_KEY = "sb_publishable_Ljtlgo608Ij4NKJaGPpJwg_WaKPrKdJ";
    
    // VOTRE URL DE PROJET
    public static final String PROJECT_URL = "https://rcsqmtihjrdpaxwzejle.supabase.co";
    
    // URLs de base
    public static final String BASE_URL = PROJECT_URL + "/rest/v1/";
    public static final String AUTH_URL = PROJECT_URL + "/auth/v1/";
    public static final String STORAGE_URL = PROJECT_URL + "/storage/v1/";
    
    // ============ NOMS DES TABLES ============
    
    public static final String TABLE_CLIENTS = "clients";
    public static final String TABLE_DETTES = "dettes";
    public static final String TABLE_PAIEMENTS = "paiements";
    public static final String TABLE_USERS = "users";
    
    // ============ HEADERS ============
    
    public static final String HEADER_API_KEY = "apikey";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_PREFER = "Prefer";
    
    // ============ VALEURS PAR DÉFAUT ============
    
    public static final String DEFAULT_SELECT = "*";
    public static final String DEFAULT_ORDER = "created_at.desc";
    
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String PREFER_REPRESENTATION = "return=representation";
    public static final String PREFER_MINIMAL = "return=minimal";
    
    // ============ PARAMÈTRES DE REQUÊTE ============
    
    public static final String QUERY_SELECT = "select";
    public static final String QUERY_ORDER = "order";
    public static final String QUERY_EQ = "eq";
    public static final String QUERY_NEQ = "neq";
    public static final String QUERY_GT = "gt";
    public static final String QUERY_LT = "lt";
    public static final String QUERY_GTE = "gte";
    public static final String QUERY_LTE = "lte";
    public static final String QUERY_LIKE = "like";
    public static final String QUERY_ILIKE = "ilike";
    public static final String QUERY_IS = "is";
    public static final String QUERY_IN = "in";
    public static final String QUERY_CS = "cs";
    public static final String QUERY_CD = "cd";
    public static final String QUERY_OV = "ov";
    public static final String QUERY_SL = "sl";
    public static final String QUERY_SR = "sr";
    public static final String QUERY_NXL = "nxl";
    public static final String QUERY_NXR = "nxr";
    public static final String QUERY_ADJ = "adj";
    
    // ============ MÉTHODES UTILITAIRES ============
    
    public static String getTableUrl(String tableName) {
        return BASE_URL + tableName;
    }
    
    public static String getClientsUrl() {
        return getTableUrl(TABLE_CLIENTS);
    }
    
    public static String getDettesUrl() {
        return getTableUrl(TABLE_DETTES);
    }
    
    public static String getPaiementsUrl() {
        return getTableUrl(TABLE_PAIEMENTS);
    }
    
    public static String getSelectQuery(String fields) {
        return "select=" + fields;
    }
    
    public static String getOrderQuery(String field, String direction) {
        return "order=" + field + "." + direction;
    }
    
    public static String getEqQuery(String field, String value) {
        return field + "=eq." + value;
    }
    
    public static String getLikeQuery(String field, String value) {
        return field + "=like." + value + "%";
    }
    
    public static String getIlikeQuery(String field, String value) {
        return field + "=ilike." + value + "%";
    }
    
    // ============ STATUTS ============
    
    public static class Status {
        public static final String DETTE_IMPAYEE = "impayé";
        public static final String DETTE_PARTIEL = "partiel";
        public static final String DETTE_PAYEE = "payé";
        
        public static final String PAIEMENT_ESPECES = "espèces";
        public static final String PAIEMENT_MOBILE = "mobile money";
        public static final String PAIEMENT_CARTE = "carte";
        public static final String PAIEMENT_VIREMENT = "virement";
    }
}