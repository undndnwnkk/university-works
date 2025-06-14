package ru.lab06.db;

import ru.lab06.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class FlatDaoImpl {
    public Vector<Flat> getAllFlats() {
        Vector<Flat> flats = new Vector<>();

        String query = """
    SELECT flats.*, users.username AS owner
    FROM flats
    JOIN users ON flats.owner_id = users.id
""";

        try (Connection connection = DatabaseConnector.connect();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Flat flat = new Flat();

                flat.setOwner(resultSet.getString("owner"));
                flat.setId(resultSet.getInt("id"));
                flat.setName(resultSet.getString("name"));
                flat.setCoordinates(new Coordinates(
                        resultSet.getFloat("x"),
                        resultSet.getFloat("y")
                ));
                flat.setCreationDate(new Date(resultSet.getTimestamp("creation_date").getTime()));
                flat.setArea(resultSet.getInt("area"));
                flat.setNumberOfRooms(resultSet.getLong("number_of_rooms"));
                flat.setFurnish(Furnish.valueOf(resultSet.getString("furnish")));
                flat.setView(View.valueOf(resultSet.getString("view")));
                flat.setTransport(Transport.valueOf(resultSet.getString("transport")));
                flat.setHouse(new House(
                        resultSet.getString("house_name"),
                        resultSet.getInt("house_year"),
                        resultSet.getLong("flats_on_floor")
                ));

                flats.add(flat);
            }
        } catch (SQLException e) {
            System.out.println("Failed loading of flats, " + e.getMessage());
        }

        return flats;
    }

    public boolean insertFlat(Flat flat, int ownerId) {
        String query = "INSERT INTO flats (name, x, y, creation_date, area, number_of_rooms, " +
                "furnish, view, transport, house_name, house_year, flats_on_floor, owner_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, flat.getName());
            stmt.setFloat(2, flat.getCoordinates().getX());
            stmt.setFloat(3, flat.getCoordinates().getY());
            stmt.setTimestamp(4, new Timestamp(flat.getCreationDate().getTime()));
            stmt.setInt(5, flat.getArea());
            stmt.setLong(6, flat.getNumberOfRooms());
            stmt.setString(7, flat.getFurnish().name());
            stmt.setString(8, flat.getView().name());
            stmt.setString(9, flat.getTransport().name());

            House house = flat.getHouse();
            if (house != null) {
                stmt.setString(10, house.getName());
                stmt.setInt(11, house.getYear());
                stmt.setLong(12, house.getNumberOfFloors());
            } else {
                stmt.setNull(10, Types.VARCHAR);
                stmt.setNull(11, Types.INTEGER);
                stmt.setNull(12, Types.BIGINT);
            }

            stmt.setInt(13, ownerId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) return false;

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    flat.setId(generatedKeys.getInt(1));
                    return true;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error while inserting flat: " + e.getMessage());
        }

        return false;
    }


    public boolean updateFlatById(int id, Flat flat, int ownerId) {
        String query = "UPDATE flats SET name = ?, x = ?, y = ?, area = ?, number_of_rooms = ?, " +
                "furnish = ?, view = ?, transport = ?, house_name = ?, house_year = ?, " +
                "flats_on_floor = ? WHERE id = ? AND owner_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, flat.getName());
            stmt.setFloat(2, flat.getCoordinates().getX());
            stmt.setFloat(3, flat.getCoordinates().getY());
            stmt.setInt(4, flat.getArea());
            stmt.setLong(5, flat.getNumberOfRooms());
            stmt.setString(6, flat.getFurnish().name());
            stmt.setString(7, flat.getView().name());
            stmt.setString(8, flat.getTransport().name());

            House house = flat.getHouse();
            if (house != null) {
                stmt.setString(9, house.getName());
                stmt.setInt(10, house.getYear());
                stmt.setLong(11, house.getNumberOfFloors());
            } else {
                stmt.setNull(9, Types.VARCHAR);
                stmt.setNull(10, Types.INTEGER);
                stmt.setNull(11, Types.BIGINT);
            }

            stmt.setInt(12, id);
            stmt.setInt(13, ownerId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error while updating flat: " + e.getMessage());
        }

        return false;
    }

    public boolean removeById(int id, int ownerId) {
        String query = "DELETE FROM flats WHERE id = ? AND owner_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.setInt(2, ownerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error while deleting flat: " + e.getMessage());
            return false;
        }
    }




}
