package nl.teunwillems.client;

import calculate.Edge;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import jsf31kochfractalfx.JSF31KochFractalFX;
import nl.teunwillems.server.KochFractalServer;
import nl.teunwillems.server.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.List;

/**
 * Created by Teun on 5-1-2016.
 */
public class KochFractalSocketClient {

    private Request request;
    private JSF31KochFractalFX fx;

    public KochFractalSocketClient(JSF31KochFractalFX fx, Request request) {
        this.request = request;
        this.fx = fx;
    }

    public void requestEdges() {
        new Thread(() -> {
            try {
                exec();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void exec() throws IOException {
        Socket socket = new Socket(fx.getIp(), KochFractalServer.PORT);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String json = new Gson().toJson(request);

        out.println(json);
        out.flush();

        Platform.runLater(() -> fx.clearKochPanel());
        if (request.getRequestType() == Request.REQUEST_TYPE.WHOLE) {
            String line = in.readLine();
            System.out.println("Received input: " + line);
            List<Edge> edges = new Gson().fromJson(line, getEdgeListType());
            for (Edge edge : edges) {
                Platform.runLater(() -> fx.drawEdge(edge));
            }
        }else if (request.getRequestType() == Request.REQUEST_TYPE.DYNAMIC){
            String line;
            while ((line = in.readLine()) != null) {
                Edge edge = new Gson().fromJson(line, new TypeToken<Edge>(){}.getType());
                System.out.println("Received input: " + line);
                Platform.runLater(() -> fx.drawEdge(edge));
            }
        }

        out.close();
        in.close();
        socket.close();
    }

    private Type getEdgeListType() {
        return new TypeToken<List<Edge>>() {

        }.getType();
    }
}
