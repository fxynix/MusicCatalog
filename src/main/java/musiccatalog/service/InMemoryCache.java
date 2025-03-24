package musiccatalog.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

@Component
public class InMemoryCache {
    private final Map<String, Object> cache = new LinkedHashMap<>();
    private static final int MAX_SIZE = 100;

    public void put(String key, Object value) {
        if (cache.size() >= MAX_SIZE) {
            String oldestKey = cache.keySet().iterator().next();
            cache.remove(oldestKey);
        }
        cache.put(key, value);
        Logger logger = Logger.getLogger(InMemoryCache.class.getName());
        String msg = String.format("Cached size: %s", cache.size());
        logger.info(msg);
    }

    public Object get(String key) {
        return cache.get(key);
    }

    public boolean containsKey(String key) {
        return cache.containsKey(key);
    }

    public void remove(String key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }
}

