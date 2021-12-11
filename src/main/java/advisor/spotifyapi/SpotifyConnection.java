package advisor.spotifyapi;


public class SpotifyConnection {
    public static final String REDIRECT_URI = "http://localhost:8080";
    public static final String DEFAULT_ACCESS_SERVER = "https://accounts.spotify.com";
    public static final String DEFAULT_API_SERVER = "https://api.spotify.com";


    private  final String clientId;
    public final String authSecretBase64;

    public SpotifyConnection(String clientId, String authSecretBase64) {
        this.clientId = clientId;
        this.authSecretBase64 = authSecretBase64;
    }
    private String accessServerPoint = DEFAULT_ACCESS_SERVER;
    private String apiServerPoint = DEFAULT_API_SERVER;

    private boolean authorized = false;

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    private String accessCode="";
    private String token="";

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getAccessServerPoint() {
        return accessServerPoint;
    }

    public void setAccessServerPoint(String accessServerPoint) {
        this.accessServerPoint = accessServerPoint;
    }

    public String getApiServerPoint() {
        return apiServerPoint;
    }

    public void setApiServerPoint(String apiServerPoint) {
        this.apiServerPoint = apiServerPoint;
    }


    public String getAuthLink() {
        String authLink = accessServerPoint+
                "/authorize?" +
                "client_id=%s" +
                "&redirect_uri=%s" +
                "&response_type=code";
        return String.format(authLink, clientId, REDIRECT_URI);
    }

    public  String getTokenLink() {
        return accessServerPoint + "/api/token";
    }


}
