package ru.authorization;

import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

/**
 * Created by Сергей on 01.05.2018.
 */
public class UserAuthorization implements UserAuthorizationI {

    private static final Logger LOG = LogManager.getLogger();

    private Connection connection = null;
    private HSqlDbConnection HSqlDbConnection = new HSqlDbConnection();
    private StringBuilder errorMessage = new StringBuilder();

    public boolean getConnection(
            String hSqlPath,
            String hSqlDb,
            String login,
            String password,
            Level  loggerLevel) {

        Configurator.setLevel(LOG.getName(), loggerLevel);

        connection = HSqlDbConnection.getConnection(
                hSqlPath,
                hSqlDb,
                login,
                password,
                loggerLevel);

        return connection == null ? false : true;
    }

    public void closeConnection() {
        if (HSqlDbConnection.closeConnection()) {
            connection = null;
        }
    }

    public static String encryptMD5(String data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(data.getBytes(Charset.forName("UTF8")));
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e);
        }
        byte[] digest = md.digest();
        return new String(Hex.encodeHex(digest));
    }

    public String getErrorMessage() {
        return errorMessage.toString();
    }

    @Override
    public boolean isCorrectUser(String login, String password) {
        boolean res = false;
        errorMessage.setLength(0);
        if (connection != null) {
            if (login != null && !login.isEmpty()) {
                if (password != null && !password.isEmpty()) {
                    PreparedStatement preparedStatement = null;
                    try {
                        preparedStatement = connection.prepareStatement("select password from users where LOWER(login) = ?");
                        preparedStatement.setString(1, login.toLowerCase());
                        ResultSet resultSet = preparedStatement.executeQuery();
                        if (resultSet.next()) {
                            if (encryptMD5(password).equals(resultSet.getString(1))) {
                                LOG.info("Успешная авторизация пользователя {}", login);
                                res = true;
                            } else {
                                LOG.warn("Неверный пароль для пользователя {}", login);
                                errorMessage.append("Неверный пароль для пользователя ")
                                        .append(login);
                            }
                        } else {
                            LOG.warn("Пользователь {} не зарегистрирован", login);
                            errorMessage.append("Пользователь ")
                                    .append(login)
                                    .append(" не зарегистрирован");
                        }
                        resultSet.close();
                        preparedStatement.close();

                    } catch (SQLException e) {
                        LOG.error(e);
                    }
                } else {
                    errorMessage.append("Необходимо указать пароль");
                }
            } else {
                errorMessage.append("Необходимо указать пользователя");
            }
        } else {
            errorMessage.append("Отсутствует подключение к базе данных");
        }
        return res;
    }

    @Override
    public boolean userAdd(String login, String password, String password2) {
        boolean res = false;
        errorMessage.setLength(0);
        if (connection != null) {
            if (!password.isEmpty() & password.equals(password2)) {
                PreparedStatement preparedStatement = null;
                try {
                    preparedStatement = connection.prepareStatement("select id from users where LOWER(login) = ?");
                    preparedStatement.setString(1, login.toLowerCase());
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        LOG.warn("Пользователь {} уже зарегистрирован", login);
                        errorMessage.append("Пользователь ")
                                .append(login)
                                .append(" уже зарегистрирован");
                    } else {
                        LOG.info("Регистрация пользователя {}, {}", login, encryptMD5(password));
                        preparedStatement.close();
                        preparedStatement = connection.prepareStatement("INSERT INTO users (login, password) VALUES(?, ?)");
                        preparedStatement.setString(1, login);
                        preparedStatement.setString(2, encryptMD5(password));
                        preparedStatement.executeUpdate();
                        res = true;
                    }
                    resultSet.close();
                    preparedStatement.close();

                } catch (SQLException e) {
                    LOG.error(e);
                }
            } else {
                if (password.isEmpty()) {
                    errorMessage.append("Ошибка регистрации пользователя ")
                            .append(login)
                            .append(" - пароль не может быть пустым");
                } else {
                    errorMessage.append("Ошибка регистрации пользователя ")
                            .append(login)
                            .append(" - пароль и подтверждение не совпадают");
                }
            }
        } else {
            errorMessage.append("Отсутствует подключение к базе данных");
        }
        return res;
    }

    @Override
    public boolean userUpdate(String login, String password) {
        boolean r = false;
        return r;
    }
}
