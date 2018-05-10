package ru.authorization;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;

/**
 * Created by Сергей on 01.05.2018.
 */
public class HSqlDbConnection {

    private static final Logger LOG = LogManager.getLogger();

    private final String driverName = "org.hsqldb.jdbcDriver";
    private Connection connection = null;

    private boolean loadDriver() {
        try {
            Class.forName(driverName); //JDBCDriver
        } catch (ClassNotFoundException e) {
            LOG.error("Ошибка при работе с драйвером: {} ", driverName, e);
            return false;
        }
        LOG.debug("SQL Driver: {}", driverName);
        return true;
    }

    public boolean isConnection() {
        return connection == null ? false : true;
    }

    public Connection getConnection(
            String hSqlPath,
            String hSqlDb,
            String login,
            String password,
            Level loggerLevel) {

        Configurator.setLevel(LOG.getName(), loggerLevel);

        if (!isConnection()) {
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
                    LOG.debug("Подключение к базе данных: DriverManager.getConnection({}, {}, {})",
                            connectionString,
                            login,
                            password);

                    createTableUsers();

                } catch (SQLException e) {
                    LOG.error("Ошибка при подключении к базе данных: DriverManager.getConnection({}, {}, {})",
                            connectionString,
                            login,
                            password,
                            e);
                    return null;
                }
            }
        }
        return connection;
    }

    private void createTableUsers() {
        execUpdate("CREATE TABLE IF NOT EXISTS users (" +
                "id IDENTITY, " +
                "login VARCHAR(25), " +
                "password VARCHAR(50))");
    }

    public boolean closeConnection() {
        boolean res = false;
//            try {
//                connection.rollback();
//                connection.setAutoCommit(true);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
        if ((res = execUpdate("SHUTDOWN"))){
            connection = null;
        }
        return res;
    }

    private boolean execUpdate(String sql) {
        boolean res = false;
        if (isConnection()) {
            try {
                Statement statement = connection.createStatement();
                statement.execute(sql);
                statement.close();
                LOG.debug("{}", sql);
                res = true;

            } catch (SQLException e) {
                LOG.error("{}", sql, e);
            }
        } else {
            LOG.error("Отсутствует подключение к базе данных");
        }
        return res;
    }

    // StackTrace to String
    private String stackTraceToString(Exception e) {
        StringWriter error = new StringWriter();
        e.printStackTrace(new PrintWriter(error));
        return error.toString();
    }

}
