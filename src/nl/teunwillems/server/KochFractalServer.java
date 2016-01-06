package nl.teunwillems.server;

import com.sun.deploy.security.CachedCertificatesHelper;
import nl.teunwillems.server.caching.CachingHandler;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Teun on 5-1-2016.
 */
public class KochFractalServer {

    public static final int DEFAULT_PORT = 8008;
    public static int PORT = DEFAULT_PORT;

    private Executor executor;
    private CachingHandler cachingHandler;

    public KochFractalServer() throws Exception {
        executor = Executors.newCachedThreadPool();
        cachingHandler = new CachingHandler();
        startListening();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Starting nl.teunwillems.server");
        System.out.println("Local network ip: " + Inet4Address.getLocalHost());
        System.out.println("Connect via port: " + PORT);
        new KochFractalServer();
    }

    private void startListening() throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        for (; ; ) {
            Socket connectedSocket = serverSocket.accept();
            System.out.println("Client connected: " + connectedSocket.getInetAddress().getHostAddress());
            executor.execute(new KochFractalRequestHandler(this, connectedSocket));
        }
    }

    public CachingHandler getCachingHandler() {
        return cachingHandler;
    }
}
