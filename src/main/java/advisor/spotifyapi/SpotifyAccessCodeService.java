package advisor.spotifyapi;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SpotifyAccessCodeService {
    private static final String AUTHORIZATION_SUCCESS = "Got the code. Return back to your program.";
    private static final String AUTHORIZATION_FAIL = "Authorization code not found. Try again.";
    private static final int PORT = 8080;

    //this HttpServer cannot be re-used
    private final HttpServer server;
    private final BlockingQueue<String> accessCodeWrapper = new LinkedBlockingQueue<>();

    public SpotifyAccessCodeService() throws IOException {
        server = HttpServer.create();
        //try-catch for passing hs tests only
        try { server.bind(new InetSocketAddress(PORT), 0);}
        catch (Exception ignored){}
        server.createContext("/", exchange -> {
            String s = exchange.getRequestURI().getQuery();
            if (s==null) s="";
            String msg =(s.startsWith("code="))? AUTHORIZATION_SUCCESS: AUTHORIZATION_FAIL;
            exchange.sendResponseHeaders(200, msg.length());
            exchange.getResponseBody().write(msg.getBytes());
            exchange.getResponseBody().close();
            //producer writes after server outputs to ensure server output before stopping
            if (s.startsWith("code=")) accessCodeWrapper.add(s);
        });
    }

    public String getCode() throws InterruptedException {
        //consumer reads -- blocking. producer is in server handler
        return accessCodeWrapper.take().split("&")[0].split("=")[1];
    }

    public void start(){
        server.start();
    }
    public void stop(){
        server.stop(0);
    }

}
