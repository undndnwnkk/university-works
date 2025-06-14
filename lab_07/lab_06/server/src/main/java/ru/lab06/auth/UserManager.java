package ru.lab06.auth;

import ru.lab06.db.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {
    public static boolean register(String login, String password) {
        String hash = PasswordHasher.hash(password);
        try (Connection connection = DatabaseConnector.connect()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, password_hash) VALUES (?, ?)");
            statement.setString(1, login);
            statement.setString(2, hash);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean login(String login, String password) {
        String hash = PasswordHasher.hash(password);
        try (Connection connection = DatabaseConnector.connect()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password_hash = ?");
            statement.setString(1, login);
            statement.setString(2, hash);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public static Integer getUserId(String login) {
        try (Connection connection = DatabaseConnector.connect()) {
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM users WHERE username = ?");
            statement.setString(1, login);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) return rs.getInt("id");
            else return null;
        } catch (SQLException e) {
            return null;
        }
    }

}
