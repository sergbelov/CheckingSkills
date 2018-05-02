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
    private StringBuilder messageError = new StringBuilder();

    public UserAuthorization(
            String hSqlPath,
            String hSqlDb,
            String login,
            String password,
            Level level) {

        Configurator.setLevel(LOG.getName(), level);

        connection = HSqlDbConnection.getConnection(
                hSqlPath,
                hSqlDb,
                login,
                password,
                level);
    }

    public void closeConnection() {
        HSqlDbConnection.closeConnection();
    }

    public static String encryptMD5(String data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(data.getBytes(Charset.forName("UTF8")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] digest = md.digest();
        return new String(Hex.encodeHex(digest));
    }

    public String getMessageError() {
        return messageError.toString();
    }

    @Override
    public boolean isCorrectUser(String login, String password) {
        boolean res = false;
        messageError.setLength(0);
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("select password from users where LOWER(login) = ?");
            preparedStatement.setString(1, login.toLowerCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                if (encryptMD5(password).equals(resultSet.getString(1))){
                    LOG.info("Успешная авторизация пользователя {}", login);

                    res = true;
                } else {
                    LOG.warn("Не верный пароль для пользователя {}", login);
                    messageError.append("Не верный пароль для пользователя ")
                                .append(login);
                }
            } else {
                LOG.warn("Пользователь {} не зарегестрирован", login);
                messageError.append("Пользователь ")
                            .append(login)
                            .append(" не зарегестрирован");
            }
            preparedStatement.close();

        } catch (SQLException e) {
            LOG.error(e);
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public boolean userAdd(String login, String password) {
        boolean res = false;
        messageError.setLength(0);
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("select id from users where LOWER(login) = ?");
            preparedStatement.setString(1, login.toLowerCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                LOG.warn("Пользователь {} уже зарегестрирован", login);
                messageError.append("Пользователь ")
                            .append(login)
                            .append(" уже зарегестрирован");
            } else {
                LOG.debug("Регистрация пользователя {}, {}", login, encryptMD5(password));
                preparedStatement.close();
                preparedStatement = connection.prepareStatement("INSERT INTO users (login, password) VALUES(?, ?)");
                preparedStatement.setString(1, login);
                preparedStatement.setString(2, encryptMD5(password));
                preparedStatement.executeUpdate();
                res = true;
            }
            preparedStatement.close();

        } catch (SQLException e) {
            LOG.error(e);
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public boolean userUpdate(String login, String password) {
        boolean r = false;
        return r;
    }
}
