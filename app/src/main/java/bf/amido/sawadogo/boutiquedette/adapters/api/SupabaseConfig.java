package bf.amido.sawadogo.boutiquedette.adapters.api;

public class SupabaseConfig {
    
    // ============ NOUVELLES CLÉS API SUPRABASE ============
    
    // VOTRE URL DE PROJET
    public static final String PROJECT_URL = "https://rcsqmtihjrdpaxwzejle.supabase.co";
    
    // NOUVELLE CLÉ PUBLISHABLE
    public static final String PUBLISHABLE_KEY = "sb_publishable_Ljtlgo608Ij4NKJaGPpJwg_WaKPrKdJ";
    
    // Alias pour compatibilité
    public static final String API_KEY = PUBLISHABLE_KEY;
    
    // URLs de base
    public static final String BASE_URL = PROJECT_URL + "/rest/v1/";
    public static final String AUTH_URL = PROJECT_URL + "/auth/v1/";
    
    // AJOUTÉ: Constantes pour ApiHelper
    public static final String SUPABASE_URL = BASE_URL;
    public static final String SUPABASE_ANON_KEY = PUBLISHABLE_KEY;
    
    // ============ CONFIGURATION AUTH ============
    
    // Pour l'authentification
    public static final String AUTH_HEADER = "Bearer " + PUBLISHABLE_KEY;
    
    // ============ TABLES ============
    
    public static final String TABLE_USERS = "users";
    public static final String TABLE_CLIENTS = "clients";
    public static final String TABLE_DETTES = "dettes";
    public static final String TABLE_PAIEMENTS = "paiements";
    
    // ============ HEADERS ============
    
    public static final String HEADER_API_KEY = "apikey";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_PREFER = "Prefer";
    
    // ============ VALEURS PAR DÉFAUT ============
    
    public static final String DEFAULT_SELECT = "*";
    public static final String QUERY_SELECT = "select";
    public static final String QUERY_ORDER = "order";
    
    public static final String DEFAULT_ORDER = "created_at.desc";
    
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String PREFER_REPRESENTATION = "return=representation";
    public static final String PREFER_MINIMAL = "return=minimal";
    
    // ============ MÉTHODES UTILITAIRES ============
    
    public static String getAuthUrl(String endpoint) {
        return AUTH_URL + endpoint;
    }
    
    public static String getTableUrl(String tableName) {
        return BASE_URL + tableName;
    }
    
    // AJOUTÉ: Méthodes utilitaires pour la construction d'URL
    public static String getClientsUrl() {
        return BASE_URL + "clients";
    }
    
    public static String getDettesUrl() {
        return BASE_URL + "dettes";
    }
    
    public static String getPaiementsUrl() {
        return BASE_URL + "paiements";
    }
}