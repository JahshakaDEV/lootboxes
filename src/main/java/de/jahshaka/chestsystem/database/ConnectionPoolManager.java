package de.jahshaka.chestsystem.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.jahshaka.chestsystem.ChestSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionPoolManager {

    private final ChestSystem plugin;
    private String username;
    private String password;
    private String hostname;
    private String database;
    private String port;
    private HikariDataSource hikariDataSource;

    public ConnectionPoolManager(ChestSystem plugin) {
        this.plugin = plugin;
        init();
        setupPool();
    }

    private void init() {
        hostname = plugin.getConfig().getString("database.ip");
        port = String.valueOf(plugin.getConfig().getInt("database.port"));
        database = plugin.getConfig().getString("database.database");
        username = plugin.getConfig().getString("database.username");
        password = plugin.getConfig().getString("database.password");
    }

    private void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://" + hostname + ":" + port + "/" + database);
        config.setDriverClassName(org.postgresql.Driver.class.getName());
        config.setUsername(username);
        config.setPassword(password);
        hikariDataSource = new HikariDataSource(config);
    }

    public Connection getConnection() {
        try {
            return hikariDataSource.getConnection();
        } catch (SQLException e) {
            plugin.getLogger().warning("1687468609643: Couldn't connect to database ");
            throw new RuntimeException(e);
        }
    }

    public void close(Connection conn, PreparedStatement ps, ResultSet res) {
        if (conn != null) try {
            conn.close();
        } catch (SQLException ignored) {
        }
        if (ps != null) try {
            ps.close();
        } catch (SQLException ignored) {
        }
        if (res != null) try {
            res.close();
        } catch (SQLException ignored) {
        }
    }

    public void closePool() {
        if (hikariDataSource != null && !hikariDataSource.isClosed()) {
            hikariDataSource.close();
        }
    }

}

