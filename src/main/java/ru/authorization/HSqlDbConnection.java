package ru.authorization;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.sql.*;

/**
 * Created by Сергей on 01.05.2018.
 */
public class HSqlDbConnection {

    private static final Logger LOG = LogManager.getLogger();

    private Connection connection = null;

    private boolean loadDriver() {
        String driverName = "org.hsqldb.jdbcDriver";
        try {
            Class.forName(driverName); //JDBCDriver
        } catch (ClassNotFoundException e) {
            LOG.error("Ошибка при работе с драйвером {}: {}",
                    driverName,
                    e);
//            e.printStackTrace();
            return false;
        }
        LOG.debug("{}", driverName);
        return true;
    }

    public boolean isConnect() {
        return connection == null ? false : true;
    }

    public Connection getConnection(
            String hSqlPath,
            String hSqlDb,
            String login,
            String password,
            Level level) {

        Configurator.setLevel(LOG.getName(), level);

        if (!isConnect()) {
            if (hSqlPath == null || hSqlPath.isEmpty()) {
                hSqlPath = "hSQL";
            }
            if (hSqlDb == null || hSqlDb.isEmpty()) {
                hSqlDb = "db";
            }
            if (login == null || login.isEmpty()) {
                login = "admin";
            }
            if (password == null || password.isEmpty()) {
                password = "admin";
            }

            if (loadDriver()) {
                String connectionString = "jdbc:hsqldb:file:" + hSqlPath + hSqlDb;
                try {
                    connection = DriverManager.getConnection(connectionString, login, password);
//                connection.setAutoCommit(false); // для обработки транзакций
//                connection.commit();
//                connection.rollback();
                    LOG.debug("DriverManager.getConnection({}, {}, {})",
                            connectionString,
                            login,
                            password);

                    createTableUsers();

                } catch (SQLException e) {
                    LOG.error("Ошибка при подключении к базе данных: DriverManager.getConnection({}, {}, {})",
                            connectionString,
                            login,
                            password);
//            e.printStackTrace();
                    return null;
                }
            }
        }
        return connection;
    }

    private void createTableUsers() {
        try {
            Statement statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id IDENTITY, " +
                    "login VARCHAR(25), " +
                    "password VARCHAR(50))";
            statement.executeUpdate(sql);
            statement.close();
            LOG.debug("{}", sql);
        } catch (SQLException e) {
            LOG.error(e);
//            e.printStackTrace();
        }
    }

    public void closeConnection() {
//            try {
//                this.connection.rollback();
//                this.connection.setAutoCommit(true);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }

        Statement statement;
        try {
            statement = connection.createStatement();
            String sql = "SHUTDOWN";
            statement.execute(sql);
            connection = null;

        } catch (SQLException e) {
            LOG.error(e);
//            e.printStackTrace();
        }
    }

}
