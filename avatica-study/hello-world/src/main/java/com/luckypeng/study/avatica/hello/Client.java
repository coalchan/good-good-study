package com.luckypeng.study.avatica.hello;

import java.sql.*;
import java.util.Properties;

/**
 * @author coalchan
 */
public class Client {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:avatica:remote:url=http://localhost:8282/";

        Properties connectionProperties = new Properties();

        String query = "select id from dual";

        try (Connection connection = DriverManager.getConnection(url, connectionProperties)) {
            try (
                    final Statement statement = connection.createStatement();
                    final ResultSet resultSet = statement.executeQuery(query);
            ) {
                while (resultSet.next()) {
                    System.out.println("id: " + resultSet.getString(1));
                }
            }
        }
    }
}
