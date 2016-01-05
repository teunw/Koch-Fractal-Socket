package nl.teunwillems.server.generators;

import calculate.Edge;

/**
 * Created by Teun on 5-1-2016.
 */
public interface EdgeGeneratedCallback {

    void onEdgeGenerated(Edge e);


    void onEdgesGenerated();

}
