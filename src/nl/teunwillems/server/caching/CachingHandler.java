package nl.teunwillems.server.caching;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Teun on 6-1-2016.
 */
public class CachingHandler implements Serializable {

    private HashMap<String, CachedClient> cachedClients;

    public CachingHandler() {
        cachedClients = new HashMap<>();
    }

    public Map<String, CachedClient> getCachedClients() {
        return Collections.unmodifiableMap(cachedClients);
    }

    public void addCachedClient(String ip, CachedClient cachedClient) {
        cachedClients.put(ip, cachedClient);
    }

    public void removeCachedClient(String ip) {
        cachedClients.remove(ip);
    }
}
