package nl.teunwillems.server.caching;

import calculate.Edge;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Teun on 6-1-2016.
 */
public class CachedClient implements Serializable {

    private int level;
    private List<Edge> edges;

    public CachedClient(int level, List<Edge> edges) {
        this.level = level;
        this.edges = edges;
    }

    public int getLevel() {
        return level;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
