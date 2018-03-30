package util;

/**
 * A helper class representing an immutable pair.
 * @param <K> The class of the key
 * @param <V> The class of the value
 */
public class Pair<K, V> {
    private K key;
    private V value;

    /**
     * Constructs a new immutable pair.
     * @param key The key of the pair
     * @param value The value of the pair
     */
    public  Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     *
     * @return
     */
    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
