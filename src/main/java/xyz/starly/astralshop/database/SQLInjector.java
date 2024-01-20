package xyz.starly.astralshop.database;

import lombok.Getter;

import java.sql.*;

@Getter
public class SQLInjector {

    private String url;

    private String user;

    private String password;

    private String dbName;

    private Connection con;

    private Class driver;

    public SQLInjector(String url, String user, String password, String dbName) throws ClassNotFoundException, SQLException {
        this.url = url;
        this.user = user;
        this.password = password;
        this.dbName = dbName;

        this.driver = Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(url + dbName, user, password);
    }

    public int executeUpdate(String sql) throws SQLException {
        Statement s = con.createStatement();

        return s.executeUpdate(sql);
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        return preparedStatement.executeQuery();
    }

    public PreparedStatement preparedStatement(String sql) throws SQLException {
        return con.prepareStatement(sql);
    }

    public boolean execute(String sql) throws SQLException {
        Statement statement = con.createStatement();
        return statement.execute(sql);
    }
}