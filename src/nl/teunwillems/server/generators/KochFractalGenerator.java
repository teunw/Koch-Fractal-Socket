package nl.teunwillems.server.generators;

import calculate.Edge;
import calculate.KochFractal;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Teun
 */
public class KochFractalGenerator implements Observer {

    private KochFractal kochFractal;
    private ArrayList<Edge> edges;

    public KochFractalGenerator(int level, EdgesGeneratedCallback callback) {
        this.edges = new ArrayList<>();

        kochFractal = new KochFractal();
        kochFractal.setLevel(level);
        kochFractal.addObserver(this);

        kochFractal.generateLeftEdge();
        kochFractal.generateRightEdge();
        kochFractal.generateBottomEdge();

        callback.OnEdgesCallback(edges);
    }

    @Override
    public void update(Observable o, Object arg) {
        edges.add((Edge) arg);
    }
}
