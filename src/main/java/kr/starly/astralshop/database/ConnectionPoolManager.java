package kr.starly.astralshop.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPoolManager {

    @Getter
    private static ConnectionPoolManager internalPool;
    private HikariDataSource dataSource;

    @Setter private String host;
    @Setter private String port;
    @Setter @Getter private String database;
    @Setter private String username;
    @Setter private String password;
    private final int minimumConnections;
    private final int maximumConnections;
    private long connectionTimeout;
    private String testQuery;

    public static void initializePoolManager(FileConfiguration config) {
        if (internalPool == null)
            internalPool = new ConnectionPoolManager(
                    config.getString("mysql.host"),
                    config.getString("mysql.port"),
                    config.getString("mysql.user"),
                    config.getString("mysql.password"),
                    config.getString("mysql.database"),
                    10,
                    20
            );
    }

    public ConnectionPoolManager(String address, String port, String username, String password, String mysql, int minimumConnections, int maximumConnections) {
        this.host = address;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = mysql;
        this.minimumConnections = minimumConnections;
        this.maximumConnections = maximumConnections;
        initialize();
        openPool();
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private void initialize() {
        connectionTimeout = 300000L;
        testQuery = "select 1";
    }

    private void openPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(username);
        config.setPassword(password);
        config.setMinimumIdle(minimumConnections);
        config.setMaximumPoolSize(maximumConnections);
        config.setConnectionTimeout(connectionTimeout);
        config.setConnectionTestQuery(testQuery);
        dataSource = new HikariDataSource(config);
    }

    public void closePool() {
        if (dataSource != null)
            dataSource.close();
        internalPool = null;
    }
}