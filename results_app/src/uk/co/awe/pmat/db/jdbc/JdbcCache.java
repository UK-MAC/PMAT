package uk.co.awe.pmat.db.jdbc;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author AWE Plc copyright 2013
 */
class JdbcCache {
    
    private final class CacheKey {
        private final String queryString;
        private final long paramsHash;

        public CacheKey(String queryString, long paramsHash) {
            this.queryString = queryString;
            this.paramsHash = paramsHash;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof CacheKey)) {
                return false;
            }
            final CacheKey other = (CacheKey) obj;
            if (!Objects.equals(this.queryString, other.queryString)) {
                return false;
            }
            if (this.paramsHash != other.paramsHash) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            int hash = 5;
            hash = 89 * hash + Objects.hashCode(this.queryString);
            hash = 89 * hash + (int) (this.paramsHash ^ (this.paramsHash >>> 32));
            return hash;
        }

    }
    
    final Map<CacheKey, SoftReference<?>> cache = new HashMap<>();
        
    boolean contains(String query, List<? extends Object> params) {
        final CacheKey key = new CacheKey(query, params.hashCode());
        // Can't just test for inclusion as the reference may have been
        // released.
        return cache.containsKey(key) && (cache.get(key).get() != null);
    }

    Object get(String query, List<? extends Object> params) {
        final CacheKey key = new CacheKey(query, params.hashCode());
        return cache.containsKey(key) ? cache.get(key).get() : null;
    }
    
    void put(String query, List<? extends Object> params, Object value) {
        final CacheKey key = new CacheKey(query, params.hashCode());
        cache.put(key, new SoftReference<>(value));
    }

    void dirty() {
        cache.clear();
    }
    
}
