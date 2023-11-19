package tech.nicecraftz.antiproxy.database;

import lombok.RequiredArgsConstructor;
import tech.nicecraftz.antiproxy.address.NetAddress;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class NetAddressDao implements Dao<String, NetAddress> {
    private final DataSource dataSource;

    @Override
    public void create(String key, NetAddress value) {
        String sql = "MERGE INTO antiproxy_ip (ip, proxy) KEY(ip) VALUES (?, ?);\n";
        dataSource.updateAsync(sql, key, value.proxy());
    }

    @Override
    public CompletableFuture<NetAddress> read(String key) {
        String sql = "SELECT * FROM antiproxy_ip WHERE ip = ?";
        return dataSource.queryAsync(sql, resultSet -> {
            try (resultSet) {
                if (resultSet != null && resultSet.next()) {
                    String ip = resultSet.getString("ip");
                    boolean proxy = resultSet.getBoolean("proxy");
                    return new NetAddress(ip, proxy);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }, key);
    }

    @Override
    public void update(String key, NetAddress value) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void delete(String key) {
        throw new UnsupportedOperationException();
    }
}
