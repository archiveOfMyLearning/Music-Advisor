package advisor.model;


import advisor.spotifyapi.SpotifyApiException;
import advisor.spotifyapi.SpotifyApiService;
import advisor.spotifyapi.SpotifyCommand;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CommandProcessor {
    private final SpotifyApiService spotifyApiService;

    public CommandProcessor(SpotifyApiService spotifyApiService) {
        this.spotifyApiService = spotifyApiService;
    }
    private static final String UNKNOWN_CAT_ERR = "Unknown category name.";

    public static class Response {

        public final List<List<String>> items = new ArrayList<>();

        public Response() {
        }
        public Response(String s) {
            items.add(new ArrayList<>());
            items.get(0).add(s);
        }

        public Response addItem(List<String> item) {
            items.add(item);
            return this;
        }

        @Override
        public String toString() {
            return items.toString();
        }

    }

    private static Response parseCategories(String json) {
        Response response = new Response();
        JsonParser
                .parseString(json)
                .getAsJsonObject()
                .get("categories")
                .getAsJsonObject()
                .get("items")
                .getAsJsonArray()
                .forEach(item -> {
                    List<String> responseItem = new ArrayList<>();
                    responseItem.add(item.getAsJsonObject().get("name").getAsString());
                    response.addItem(responseItem);
                });
        return response;
    }

    private static Response parsePlaylists(String json) {
        Response response = new Response();
        JsonParser
                .parseString(json)
                .getAsJsonObject()
                .get("playlists")
                .getAsJsonObject()
                .get("items")
                .getAsJsonArray()
                .forEach(item -> {
                    List<String> responseItem = new ArrayList<>();
                    responseItem.add(item.getAsJsonObject().get("name").getAsString());
                    responseItem.add(item.getAsJsonObject()
                            .get("external_urls")
                            .getAsJsonObject()
                            .get("spotify")
                            .getAsString()
                    );
                    responseItem.add("");
                    response.addItem(responseItem);
                });
        return response;
    }

    private static Response parseAlbums(String json) {
        Response response = new Response();
        JsonParser
                .parseString(json)
                .getAsJsonObject()
                .get("albums")
                .getAsJsonObject()
                .get("items")
                .getAsJsonArray()
                .forEach(item -> {
                    List<String> responseItem = new ArrayList<>();
                    responseItem.add(item.getAsJsonObject().get("name").getAsString());
                    responseItem.add(
                            StreamSupport.stream(
                                    item.getAsJsonObject()
                                            .get("artists")
                                            .getAsJsonArray()
                                            .spliterator(),false
                            ).map(el -> el
                                    .getAsJsonObject()
                                    .get("name")
                                    .getAsString()
                            ).collect(Collectors.toList()).toString()
                    );
                    responseItem.add(item.getAsJsonObject()
                            .get("external_urls")
                            .getAsJsonObject()
                            .get("spotify")
                            .getAsString()
                    );
                    responseItem.add("");
                    response.addItem(responseItem);
                });
        return response;
    }

    private static final Map<SpotifyCommand.Type, Function<String,Response>> PARSER = new HashMap<>();
    static {
        PARSER.put(SpotifyCommand.Type.NEW,CommandProcessor::parseAlbums);
        PARSER.put(SpotifyCommand.Type.FEATURED,CommandProcessor::parsePlaylists);
        PARSER.put(SpotifyCommand.Type.PLAYLISTS,CommandProcessor::parsePlaylists);
        PARSER.put(SpotifyCommand.Type.CATEGORIES,CommandProcessor::parseCategories);
    }

    public Response process(SpotifyCommand spotifyCommand) throws IOException, InterruptedException, SpotifyApiException {
        SpotifyCommand command = spotifyCommand;
        //for "playlists" command substitute category name with its id before execution
        if (command.type == SpotifyCommand.Type.PLAYLISTS) {
            String jsonForCategories = spotifyApiService.getFromSpotify(new SpotifyCommand(SpotifyCommand.Type.CATEGORIES,null));
            //this mapping catName:catId could be a service for not downloading categories every time
            Map<String,String> catIdFromName = new HashMap<>();
            JsonParser
                    .parseString(jsonForCategories)
                    .getAsJsonObject()
                    .get("categories")
                    .getAsJsonObject()
                    .get("items")
                    .getAsJsonArray()
                    .forEach(item -> catIdFromName.put(
                                    item.getAsJsonObject().get("name").getAsString(),
                                    item.getAsJsonObject().get("id").getAsString()
                            )
                    );
            if (!catIdFromName.containsKey(command.param)) {
                throw new CommandProcessorException(UNKNOWN_CAT_ERR);
            } else command = new SpotifyCommand(SpotifyCommand.Type.PLAYLISTS, catIdFromName.get(command.param));
        }
        String json = spotifyApiService.getFromSpotify(command);

        return PARSER.get(command.type).apply(json);
    }
}
