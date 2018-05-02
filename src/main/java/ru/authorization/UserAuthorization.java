package ru.authorization;

import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
            String password) {

        connection = HSqlDbConnection.getConnection(
                hSqlPath,
                hSqlDb,
                login,
                password);
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
    public boolean isUserCorrect(String login, String password) {
        return false;
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
                messageError.append("Пользователь ")
                            .append(login)
                            .append(" уже зарегестрирован в системе");
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
