package advisor;

import advisor.spotifyapi.SpotifyCommand;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CommandParser {

    private static final Map<String, Command.Type> CONTROLLER_COMMAND_CLI_NAME = new HashMap<>();
    static {
        CONTROLLER_COMMAND_CLI_NAME.put("next", Command.Type.NEXT);
        CONTROLLER_COMMAND_CLI_NAME.put("prev", Command.Type.PREV);
        CONTROLLER_COMMAND_CLI_NAME.put("exit", Command.Type.EXIT);
    }
    private static final Map<String, SpotifyCommand.Type> SPOTIFY_COMMAND_CLI_NAME = new HashMap<>();
    static {
        SPOTIFY_COMMAND_CLI_NAME.put("auth", SpotifyCommand.Type.AUTH);
        SPOTIFY_COMMAND_CLI_NAME.put("featured", SpotifyCommand.Type.FEATURED);
        SPOTIFY_COMMAND_CLI_NAME.put("categories", SpotifyCommand.Type.CATEGORIES);
        SPOTIFY_COMMAND_CLI_NAME.put("new", SpotifyCommand.Type.NEW);
        SPOTIFY_COMMAND_CLI_NAME.put("playlists", SpotifyCommand.Type.PLAYLISTS);
        assert (Collections.disjoint(CONTROLLER_COMMAND_CLI_NAME.keySet(),SPOTIFY_COMMAND_CLI_NAME.keySet()));
    }

    private static final String NO_SUCH_COMMAND = "Illegal command \"%s\".";
    private static final String COMMAND_REQUIRES_PARAMETER = "Command \"%s\" requires parameter.";

    private CommandParser() {
    }


    public static Command parseUserInput(String input) throws IllegalArgumentException{
        String[] inputArr = input.split(" ");
        String name = inputArr[0];
        if(CONTROLLER_COMMAND_CLI_NAME.containsKey(name)) {
            return new Command(CONTROLLER_COMMAND_CLI_NAME.get(name),null);
        } else if (SPOTIFY_COMMAND_CLI_NAME.containsKey(name)) {
            SpotifyCommand.Type spotifyCommandType = SPOTIFY_COMMAND_CLI_NAME.get(name);
            String spotifyCommandParam = "";
            if (spotifyCommandType == SpotifyCommand.Type.AUTH) {
                return new Command(Command.Type.AUTH, new SpotifyCommand(spotifyCommandType,spotifyCommandParam));
            }
            if (spotifyCommandType.requiresParam) {
                if (inputArr.length < 2) {
                    throw new IllegalArgumentException(String.format(COMMAND_REQUIRES_PARAMETER,name));
                }
                spotifyCommandParam = input.substring(name.length()).trim();
            }
            return new Command(
                    Command.Type.EXEC,
                    new SpotifyCommand(spotifyCommandType,spotifyCommandParam)
            );
        } else {
            throw new IllegalArgumentException(String.format(NO_SUCH_COMMAND, name));
        }
    }

}
