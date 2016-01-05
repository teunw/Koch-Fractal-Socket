package nl.teunwillems.server.generators;

import calculate.Edge;
import calculate.KochFractal;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Teun on 5-1-2016.
 */
public class KochFractalDynamicGenerator implements Observer {

    private KochFractal kochFractal;
    private EdgeGeneratedCallback edgeGeneratedCallback;

    public KochFractalDynamicGenerator(int level, EdgeGeneratedCallback callback) {
        this.edgeGeneratedCallback = callback;

        kochFractal = new KochFractal();
        kochFractal.setLevel(level);
        kochFractal.addObserver(this);

        kochFractal.generateBottomEdge();
        kochFractal.generateRightEdge();
        kochFractal.generateLeftEdge();

        edgeGeneratedCallback.onEdgesGenerated();
    }

    @Override
    public void update(Observable o, Object arg) {
        edgeGeneratedCallback.onEdgeGenerated((Edge) arg);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
