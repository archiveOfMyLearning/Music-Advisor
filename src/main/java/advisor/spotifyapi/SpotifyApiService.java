package advisor.spotifyapi;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;



public class SpotifyApiService {
    private final SpotifyConnection spotifyConnection;

    public SpotifyApiService(SpotifyConnection spotifyConnection) {
        this.spotifyConnection = spotifyConnection;
    }

    private final HttpClient client = HttpClient.newBuilder().build();
    private static final String NOT_AUTHORIZED = "Please, provide access for application.";
    private static final Map<SpotifyCommand.Type,String> API_FOR_COMMAND_WO_PARAM = new HashMap<>();
    static {
        API_FOR_COMMAND_WO_PARAM.put(SpotifyCommand.Type.NEW,"/v1/browse/new-releases");
        API_FOR_COMMAND_WO_PARAM.put(SpotifyCommand.Type.FEATURED,"/v1/browse/featured-playlists");
        API_FOR_COMMAND_WO_PARAM.put(SpotifyCommand.Type.CATEGORIES,"/v1/browse/categories");
    }
    private static final Map<SpotifyCommand.Type,String> API_FOR_COMMAND_WITH_PARAM = new HashMap<>();
    static {
        API_FOR_COMMAND_WITH_PARAM.put(SpotifyCommand.Type.PLAYLISTS,"/v1/browse/categories/%s/playlists");
    }


    private static String apiForCommand(SpotifyCommand command) {
        if(!command.type.requiresParam) return API_FOR_COMMAND_WO_PARAM.get(command.type);
        else return String.format(API_FOR_COMMAND_WITH_PARAM.get(command.type), command.param);
    }
    public String getFromSpotify(SpotifyCommand command) throws IOException, InterruptedException, SpotifyApiException {
        if(!spotifyConnection.isAuthorized()) throw new SpotifyApiException(NOT_AUTHORIZED);
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + spotifyConnection.getToken())
                .header("Content-Type","application/json")
                .uri(URI.create(spotifyConnection.getApiServerPoint()+apiForCommand(command)))
                .GET()
                .build();
        HttpResponse<String> response;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        if (json.startsWith("{\"error\":")) {
            //extract error message from spotify json response
            throw new SpotifyApiException(json.split("\"")[7]);
        }
        return json;
    }
}
