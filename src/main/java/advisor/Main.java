package advisor;

import advisor.controller.CliController;
import advisor.spotifyapi.SpotifyConnection;


public class Main {
    public static void main(String[] args) {
        SpotifyConnection spotifyConnection = new SpotifyConnection(
                "f3f49f01499240a6bc05989e01ebc5cd",
                "Basic ZjNmNDlmMDE0OTkyNDBhNmJjMDU5ODllMDFlYmM1Y2Q6OWYyZmNiMGQ1ODRlNGJhMzk3OGNjOWQ0YTBhM2ViM2E="
        );
        CliController controller = new CliController(spotifyConnection);
        //args should be in form: -arg1 arg2 -arg3 arg4. Not valid example: -arg1 arg2 arg3 -arg4
        for (int i = 0; i < args.length; i+=2) {
            switch (args[i]){
                case "-access":  controller.getSpotifyConnection().setAccessServerPoint(args[i+1]);
                    break;
                case "-resource": controller.getSpotifyConnection().setApiServerPoint(args[i+1]);
                    break;
                case "-page": CliController.Paginator.setItemsNumPerPage(Integer.parseInt(args[i+1]));
                    break;
                default:
                    break;
            }
        }

        controller.run();
    }

}
