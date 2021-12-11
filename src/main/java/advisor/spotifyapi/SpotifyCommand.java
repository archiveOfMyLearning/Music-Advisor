package advisor.spotifyapi;

public class SpotifyCommand {
    public enum Type {
        AUTH(false,false),
        FEATURED(false,true),
        NEW(false,true),
        CATEGORIES(false,true),
        PLAYLISTS(true,true);

        public final boolean requiresParam;
        public final boolean requiresToken;

        Type(boolean requiresParam, boolean requiresToken) {
            this.requiresParam = requiresParam;
            this.requiresToken = requiresToken;
        }
    }

    public final Type type;
    public final String param;

    public SpotifyCommand(Type type, String param) {
        this.type = type;
        this.param = (param == null)? "": param;
    }

    @Override
    public String toString() {
        return "Spotify." + type.toString() + "(" + param + ")";
    }
}
