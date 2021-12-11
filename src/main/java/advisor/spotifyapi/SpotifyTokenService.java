package advisor.spotifyapi;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SpotifyTokenService {
    private static final String TOKEN_REQUEST = "grant_type=authorization_code" +
            "&code=%s" +
            "&redirect_uri=%s";

    private final HttpClient client = HttpClient.newBuilder().build();
    private final SpotifyConnection spotifyConnection;

    public SpotifyTokenService(SpotifyConnection spotifyConnection) {
        this.spotifyConnection = spotifyConnection;

    }

    public String receiveTokenJson(String accessCode) throws IOException, InterruptedException {
        String tokenRequestBody = String.format(TOKEN_REQUEST, accessCode, SpotifyConnection.REDIRECT_URI);
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization",spotifyConnection.authSecretBase64)
                .uri(URI.create(spotifyConnection.getTokenLink()))
                .POST(HttpRequest.BodyPublishers.ofString(tokenRequestBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }


}
