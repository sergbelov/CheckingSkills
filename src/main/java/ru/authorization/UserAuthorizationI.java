package ru.authorization;

/**
 * Created by Сергей on 01.05.2018.
 */
public interface UserAuthorizationI {
    boolean isCorrectUser(String login, String password);
    boolean userAdd(String login, String password, String password2);
    boolean userUpdate(String login, String password);
}
