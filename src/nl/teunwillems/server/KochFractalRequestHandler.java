package nl.teunwillems.server;

import calculate.Edge;
import com.google.gson.Gson;
import nl.teunwillems.server.generators.EdgeGeneratedCallback;
import nl.teunwillems.server.generators.KochFractalDynamicGenerator;
import nl.teunwillems.server.generators.KochFractalGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Teun on 5-1-2016.
 */
public class KochFractalRequestHandler implements Runnable {

    public static final String DONE_STRING = "{DONESTRING}";

    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    public KochFractalRequestHandler(Socket socket) {
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

            if (request.getRequestType() == Request.REQUEST_TYPE.WHOLE) {
                new KochFractalGenerator(level, edges -> {
                    String json = new Gson().toJson(edges);
                    out.println(json);
                    flushAndCloseConnection();
                });
            }else{
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
        } catch (IOException e) {}

    }
}
