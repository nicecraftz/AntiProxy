package tech.nicecraftz.antiproxy.storage.database;

import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface DataSource {

    void connect();

    void disconnect();

    <T> T query(String query, Function<ResultSet, T> mapper, Object... objects);

    void update(String query, Object... objects);

    default <T> CompletableFuture<T> queryAsync(String query, Function<ResultSet, T> mapper, Object... objects) {
        return CompletableFuture.supplyAsync(() -> query(query, mapper, objects));
    }

    default CompletableFuture<Void> updateAsync(String query, Object... objects) {
        return CompletableFuture.runAsync(() -> update(query, objects));
    }
}
