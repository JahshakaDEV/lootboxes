package de.jahshaka.chestsystem.database;

import de.jahshaka.chestsystem.ChestSystem;

import java.sql.*;
import java.util.UUID;

public class SqlManager {

    private final ChestSystem plugin;
    private final ConnectionPoolManager cpl;

    public SqlManager(ChestSystem plugin) {
        this.plugin = plugin;
        cpl = new ConnectionPoolManager(plugin);
        createTables();
    }

    public ConnectionPoolManager getCpl() {
        return cpl;
    }

    private void createTables() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = cpl.getConnection();
            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS economy (id SERIAL PRIMARY KEY, player UUID UNIQUE NOT NULL, balance INTEGER DEFAULT 1000)");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            cpl.close(connection, preparedStatement, null);
        } catch (SQLException e) {
            plugin.getLogger().warning("economy table couldn't be created!");
        } finally {
            cpl.close(connection, preparedStatement, null);
        }
        try {
            connection = cpl.getConnection();
            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS lootboxes (id SERIAL PRIMARY KEY, player UUID NOT NULL, box VARCHAR(100) NOT NULL, amount INTEGER)");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            cpl.close(connection, preparedStatement, null);
        } catch (SQLException e) {
            plugin.getLogger().warning("lootboxes table couldn't be created!");
        } finally {
            cpl.close(connection, preparedStatement, null);
        }
        try {
            connection = cpl.getConnection();
            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS win_history (id SERIAL PRIMARY KEY, player UUID NOT NULL, box VARCHAR(100) NOT NULL, prize VARCHAR(100), timestamp TIMESTAMP);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            cpl.close(connection, preparedStatement, null);
        } catch (SQLException e) {
            plugin.getLogger().warning("winhistory table couldn't be created!");
        } finally {
            cpl.close(connection, preparedStatement, null);
        }
    }

    public void closePool() {
        cpl.closePool();
    }

    public void addLootboxToHistory(UUID playerUUID, String lootboxID, String itemID) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = cpl.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO win_history(player, box, prize, timestamp) VALUES (?, ?, ?, ?);");
            preparedStatement.setObject(1, playerUUID);
            preparedStatement.setString(2, lootboxID);
            preparedStatement.setString(3, itemID);
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            preparedStatement.setTimestamp(4, currentTimestamp);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            cpl.close(connection, preparedStatement, null);
        } catch (SQLException e) {
            plugin.getLogger().warning("1687722911434 SQL Exception!");
        } finally {
            cpl.close(connection, preparedStatement, null);
        }
    }

    public boolean isPlayerInEconomyTable(UUID playerUUID) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean exists = false;
        try {
            connection = cpl.getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM economy WHERE player=?");
            preparedStatement.setObject(1, playerUUID);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                exists = true;
            }
            rs.close();
            preparedStatement.close();
            connection.close();
            cpl.close(connection, preparedStatement, rs);
            return exists;
        } catch (SQLException e) {
            plugin.getLogger().warning("1687471064464: SQL Exception!");
            e.printStackTrace();
            return exists;
        } finally {
            cpl.close(connection, preparedStatement, null);
        }
    }

    public void addPlayerToEconomyTable(UUID playerUUID) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = cpl.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO economy(player) VALUES (?) ON CONFLICT (player) DO NOTHING;");
            preparedStatement.setObject(1, playerUUID);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            cpl.close(connection, preparedStatement, null);
        } catch (SQLException e) {
            plugin.getLogger().warning("1687472113197: Couldn't add player to economy table!");
            e.printStackTrace();
        } finally {
            cpl.close(connection, preparedStatement, null);
        }
    }

}
