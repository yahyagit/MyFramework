package framework.tools;

import framework.data.DBDriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBService {
    private Connection connection;

    public DBService(DBDriver dbDriver) {
        try {
            Class.forName(dbDriver.getClassPath()).newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public void initConnection(String url, String username, String password) {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public ResultSet executeQuery(String query) {
        try {
            return connection.createStatement().executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
