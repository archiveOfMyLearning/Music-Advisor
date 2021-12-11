package advisor.spotifyapi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

public class SpotifyAuthService {

    private final SpotifyConnection spotifyConnection;

    public SpotifyAuthService(SpotifyConnection spotifyConnection) {
        this.spotifyConnection = spotifyConnection;
    }

    private static final String DID_NOT_GET_ACCESS_CODE = "Failed to receive access code";
    private static final String DID_NOT_GET_ACCESS_TOKEN = "Failed to receive access token";

    private String receiveAccessCode() {
        String code = "";
        try {
            SpotifyAccessCodeService accessCodeService = new SpotifyAccessCodeService();
            accessCodeService.start();
            code = accessCodeService.getCode();
            accessCodeService.stop();
        } catch (IOException e) {
            throw new SpotifyAuthException(DID_NOT_GET_ACCESS_CODE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return code;
    }
    private String receiveToken(String accessCode) {
        String tokenJson = "";
        try {
            tokenJson = new SpotifyTokenService(spotifyConnection).receiveTokenJson(accessCode);
        } catch (IOException e) {
            throw new SpotifyAuthException(DID_NOT_GET_ACCESS_TOKEN);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return tokenJson;
    }

    public void authorize() throws SpotifyAuthException {
        //blocking op
        String accessCode = receiveAccessCode();
        spotifyConnection.setAccessCode(accessCode);
        String tokenJson = receiveToken(accessCode);
        JsonObject tokenJsonObject = JsonParser.parseString(tokenJson).getAsJsonObject();
        JsonElement accessTokenElement = tokenJsonObject.get("access_token");
        if (accessTokenElement == null) throw new SpotifyAuthException(DID_NOT_GET_ACCESS_TOKEN);
        spotifyConnection.setToken(accessTokenElement.getAsString());
    }
}
