package xyz.starly.astralshop.database;

import java.sql.*;

public class SQLInjector {

    private String url;

    private String user;

    private String password;

    private String dbName;

    private Connection con = null;

    private Class driver;

    private Statement statement;
    private ResultSet result;

    public SQLInjector(String url, String user, String password, String dbName) throws ClassNotFoundException, SQLException {
        this.url = url;
        this.user = user;
        this.password = password;
        this.dbName = dbName;

        this.driver = Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(url + dbName, user, password);
    }

    public int createTable(String sql) throws SQLException {
        Statement s = con.createStatement();
        return s.executeUpdate(sql);
    }
}