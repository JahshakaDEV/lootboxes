package de.jahshaka.chestsystem.economy;

import de.jahshaka.chestsystem.ChestSystem;
import de.jahshaka.chestsystem.database.ConnectionPoolManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Economy {

    public static int balance(UUID playerUUID) {
        ConnectionPoolManager cpl = ChestSystem.getPlugin().getSqlManager().getCpl();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        int balance = 0;
        try {
            connection = cpl.getConnection();
            preparedStatement = connection.prepareStatement("SELECT balance from economy WHERE player=?::uuid");
            preparedStatement.setString(1, String.valueOf(playerUUID));
            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                balance = rs.getInt(1);
            }
            rs.close();
            preparedStatement.close();
            connection.close();
            cpl.close(connection, preparedStatement, rs);
            return balance;
        } catch (SQLException e) {
            ChestSystem.getPlugin().getLogger().warning("1687552367463: SQL Exception!");
            e.printStackTrace();
        } finally {
            cpl.close(connection, preparedStatement, rs);
        }
        return balance;
    }

    public static void setBalance(UUID playerUUID, int balance) {
        ConnectionPoolManager cpl = ChestSystem.getPlugin().getSqlManager().getCpl();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = cpl.getConnection();
            preparedStatement = connection.prepareStatement("Update economy SET balance=? WHERE player=?::uuid");
            preparedStatement.setInt(1, balance);
            preparedStatement.setString(2, String.valueOf(playerUUID));
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            cpl.close(connection, preparedStatement, null);
        } catch (SQLException e) {
            ChestSystem.getPlugin().getLogger().warning("1687552773535: SQL Exception!");
            e.printStackTrace();
        } finally {
            cpl.close(connection, preparedStatement, null);
        }
    }
}
