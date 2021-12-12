package advisor;


import advisor.spotifyapi.SpotifyApiException;
import advisor.spotifyapi.SpotifyApiService;
import advisor.spotifyapi.SpotifyAuthException;
import advisor.spotifyapi.SpotifyAuthService;
import advisor.spotifyapi.SpotifyConnection;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class CliController implements Runnable {

    private boolean commandLoopTerminated = false;
    private final Scanner scanner = new Scanner(System.in);
    private final SpotifyConnection spotifyConnection;
    private final CommandProcessor processor;

    public CliController(SpotifyConnection spotifyConnection) {
        this.spotifyConnection = spotifyConnection;
        this.processor = new CommandProcessor(new SpotifyApiService(spotifyConnection));
    }

    public SpotifyConnection getSpotifyConnection() {
        return spotifyConnection;
    }

    public static class Paginator {
        private static final String NO_MORE_PAGES = "No more pages.";
        private static int perPage = 5;

        public static void setItemsNumPerPage(int perPage) {
            Paginator.perPage = perPage;
        }

        public static int getItemsNumPerPage() {
            return perPage;
        }

        private int currentPageNumber = 1;
        private final CommandProcessor.Response response;


        public String getFooter() {
            return String.format("---PAGE %d OF %d---", currentPageNumber,getPageCount());
        }

        private int getPageCount() {
            return (response.items.size()+perPage-1)/perPage;
        }



        public Paginator(CommandProcessor.Response response) {
            this.response = response;
        }

        public String getCurrentPage() {
            StringBuilder sb = new StringBuilder();
            //stream.skip.limit
            var arr = response.items.subList((currentPageNumber -1)*perPage, Math.min(response.items.size(), currentPageNumber *perPage));
            for (List<String> strings : arr) {
                for (String s : strings) {
                    sb.append(s).append("\n");
                }
            }
            return sb.toString()+getFooter();
        }
        public String getNextPage() {
            if ((currentPageNumber+1)*perPage > response.items.size()) return NO_MORE_PAGES;
            currentPageNumber++;
            return getCurrentPage();
        }

        public String getPrevPage() {
            if (currentPageNumber==1) return NO_MORE_PAGES;
            currentPageNumber--;
            return getCurrentPage();
        }

    }

    @Override
    //controller manages the View (which is terminal) only from this function
    public void run() {
        Paginator paginator = new Paginator(new CommandProcessor.Response());
        while (!commandLoopTerminated){

            String input = scanner.nextLine();
            Command command = null;
            boolean validCommand = false;
            try{
                command = CommandParser.parseUserInput(input);
                validCommand = true;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage() + " Try again");
            }
            if (!validCommand) continue;

            Command.Type controllerCommandType = command.type;
            switch (controllerCommandType) {
                case AUTH:
                    System.out.println("use this link to request the access code:");
                    System.out.println(spotifyConnection.getAuthLink());
                    System.out.println("waiting for code...");
                    try {
                        new SpotifyAuthService(spotifyConnection).authorize();
                    } catch (SpotifyAuthException e) {
                        System.out.println(e.getMessage());
                    }
                    spotifyConnection.setAuthorized(true);
                    System.out.println("Success!");

                    break;
                case PREV:
                    System.out.println(paginator.getPrevPage());
                    break;
                case NEXT:
                    System.out.println(paginator.getNextPage());
                    break;
                case EXIT:
                    commandLoopTerminated = true;
                    System.out.println("---GOODBYE!---");
                    break;
                case EXEC:
                    CommandProcessor.Response response = null;
                    try {
                        response = processor.process(command.spotifyCommand);
                    } catch (SpotifyApiException | CommandProcessorException e) {
                        System.out.println(e.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                    if (response == null) continue;
                    paginator = new Paginator(response);
                    System.out.println(paginator.getCurrentPage());
                    break;
            }
        }
    }

}
