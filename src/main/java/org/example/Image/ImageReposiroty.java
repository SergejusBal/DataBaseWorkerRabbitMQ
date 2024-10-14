package org.example.Image;

import org.example.DataBaseInfo;

import java.sql.*;

public class ImageReposiroty {

    public String registerImage(String URL){

        String sql = "INSERT INTO image (URL) VALUES (?);";
        try {
            Connection connection = DriverManager.getConnection(DataBaseInfo.URL, DataBaseInfo.USERNAME, DataBaseInfo.PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, URL);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

        }catch (SQLException e) {

            System.out.println(e.getMessage());

            return "Database connection failed";
        }

        return "URL  was successfully registered";
    }


}
