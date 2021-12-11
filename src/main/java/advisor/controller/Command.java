package advisor.controller;

import advisor.spotifyapi.SpotifyCommand;

public class Command {
    public enum Type {
        NEXT,
        PREV,
        EXEC,
        AUTH,
        EXIT
    }
    public final SpotifyCommand spotifyCommand;
    public final Type type;

    public Command(Type type, SpotifyCommand spotifyCommand) {
        this.type = type;
        this.spotifyCommand = spotifyCommand;
    }

    @Override
    public String toString() {
        return "Controller." + type.toString() +
                "(" +
                    ((spotifyCommand == null)? "": spotifyCommand.toString()) +
                ")";
    }
}