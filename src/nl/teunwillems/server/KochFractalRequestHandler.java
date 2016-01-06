package nl.teunwillems.server;

import calculate.Edge;
import com.google.gson.Gson;
import nl.teunwillems.server.caching.CachedClient;
import nl.teunwillems.server.generators.EdgeGeneratedCallback;
import nl.teunwillems.server.generators.EdgesGeneratedCallback;
import nl.teunwillems.server.generators.KochFractalDynamicGenerator;
import nl.teunwillems.server.generators.KochFractalGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Teun on 5-1-2016.
 */
public class KochFractalRequestHandler implements Runnable {

    private PrintWriter out;
    private BufferedReader in;

    private Socket socket;
    private CachedClient cachedClient;
    private KochFractalServer kochFractalServer;

    public KochFractalRequestHandler(KochFractalServer kochFractalServer, Socket socket) {
        this.kochFractalServer = kochFractalServer;
        this.cachedClient = this.kochFractalServer.getCachingHandler().getCachedClients().get(socket.getInetAddress().getHostAddress());
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String input = in.readLine();
            System.out.println("Input received: " + input);
            Request request = new Gson().fromJson(input, Request.class);
            int level = request.getLevel();

            sendEdges(request, level);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void flushAndCloseConnection() {
        out.flush();
        out.close();
        try {
            in.close();
            socket.close();
        } catch (IOException e) {
        }
    }

    private void sendEdges(Request request, int level) {
        if (request.getRequestType() == Request.REQUEST_TYPE.WHOLE) {
            final List<Edge> edges = new ArrayList<>();
            if (!useCash(level)) {
                new KochFractalGenerator(level, edges::addAll);
            }else{
                edges.addAll(cachedClient.getEdges());
            }
            String json = new Gson().toJson(edges);
            out.println(json);
            flushAndCloseConnection();
            kochFractalServer.getCachingHandler().addCachedClient(socket.getInetAddress().getHostAddress(), new CachedClient(level, edges));
        } else {
            new KochFractalDynamicGenerator(level, new EdgeGeneratedCallback() {
                @Override
                public void onEdgeGenerated(Edge e) {
                    String json = new Gson().toJson(e);
                    out.println(json);
                }

                @Override
                public void onEdgesGenerated() {
                    flushAndCloseConnection();
                }
            });
        }
    }

    private boolean useCash(int level) {
        if (cachedClient == null)
            return false;

        return cachedClient.getLevel() == level;
    }
}
