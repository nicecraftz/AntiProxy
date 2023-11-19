package tech.nicecraftz.antiproxy.storage.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

@RequiredArgsConstructor
public class H2DataSource implements DataSource {
    private final File file;
    private HikariDataSource hikariDataSource;

    @Override
    public void connect() {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.h2.Driver");
        hikariConfig.setJdbcUrl("jdbc:h2:" + file.getAbsolutePath());
        hikariDataSource = new HikariDataSource(hikariConfig);
        createTables();
    }

    private void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS antiproxy_ip (ip VARCHAR(16) NOT NULL PRIMARY KEY, proxy BOOLEAN)";
        update(sql);
    }

    @Override
    public void disconnect() {
        if (hikariDataSource != null) {
            hikariDataSource.close();
        }
    }

    @Override
    public <T> T query(String query, Function<ResultSet, T> mapper, Object... objects) {
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (int i = 0; i < objects.length; i++) {
                preparedStatement.setObject(i + 1, objects[i]);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            return mapper.apply(resultSet);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void update(String query, Object... objects) {
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (int i = 0; i < objects.length; i++) {
                preparedStatement.setObject(i + 1, objects[i]);
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
