package tech.nicecraftz.antiproxy.storage.database;

import java.util.concurrent.CompletableFuture;

public interface Dao<K, V> {

    void create(K key, V value);

    CompletableFuture<V> read(K key);

    void update(K key, V value);

    void delete(K key);
}
