package ru.questions;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.*;
import java.util.Properties;

/**
 * @author Белов Сергей
 * Читаем вопросы из XML-файла
 */
public class PropertiesApp {

    private static final Logger LOG = LogManager.getLogger();

    // CheckingSkills.properties
    private int     MAX_QUESTION = 10;                              // максимальное количество задаваемых вопросов
    private String  FILE_QUESTIONS = "questions\\Questions.json";   // файл с вопросами
    private String  PATH_RESULT = "result\\";                       // путь для сохранения результатов тестирования
    private String  FORMAT_RESULT = "JSON";                         // формат файла с результатами тестирования XML или JSON
    private Level   LOGGER_LEVEL = Level.WARN;                      // уровень логирования
    private String  HSQL_PATH = "C:\\TEMP\\questions\\HSQL\\";      // HSQL путь к базе
    private String  HSQL_DB = "DB_CheckingSkills";                  // HSQL имя базы
    private String  HSQL_LOGIN = "admin";                           // HSQL логин
    private String  HSQL_PASSWORD = "admin";                        // HSQL пароль
    private boolean USER_REGISTRATION = false;                      // Самостоятельная регистрация пользователей

    /**
     * Читаем параметры из файла
     *
     * @param fileName
     */
    public void readProperties(String fileName) {
        File file = new File(fileName);
        if (file.exists()) { // найден файл с установками
            try (
                    InputStream is = new FileInputStream(file)
            ) {
                Properties pr = new Properties();
                pr.load(is);

                this.MAX_QUESTION = Integer.parseInt(pr.getProperty("MAX_QUESTION", "10"));
                this.FILE_QUESTIONS = pr.getProperty("FILE_QUESTIONS", "questions\\Questions.json");
                this.PATH_RESULT = pr.getProperty("PATH_RESULT", "Result\\");
                this.FORMAT_RESULT = pr.getProperty("FORMAT_RESULT", "JSON");
                this.LOGGER_LEVEL = Level.getLevel(pr.getProperty("LOGGER_LEVEL", "WARN"));
                this.USER_REGISTRATION = Boolean.parseBoolean(pr.getProperty("USER_REGISTRATION", "false"));

                this.HSQL_PATH = pr.getProperty("HSQL_PATH", "C:\\TEMP\\questions\\HSQL\\");
                this.HSQL_DB = pr.getProperty("HSQL_DB", "DB_CheckingSkills");
                this.HSQL_LOGIN = pr.getProperty("HSQL_LOGIN", "admin");
                this.HSQL_PASSWORD = pr.getProperty("HSQL_PASSWORD", "admin");


                Configurator.setLevel(LOG.getName(), LOGGER_LEVEL);

                LOG.info("Параметры из файла {}\r\n"+
                            "Максимальное количество задаваемых вопросов: {}\r\n" +
                            "Файл с вопросами: {}\r\n" +
                            "Путь для сохранения результатов тестирования: {}\r\n" +
                            "Формат файла с результатами тестирования XML или JSON: {}\r\n" +
                            "Уровень логирования: {}\r\n" +
                            "Самостоятельная регистрация пользователей: {}\r\n" +
                            "HSQL путь к базе: {}\r\n" +
                            "HSQL имя базы: {}\r\n" +
                            "HSQL логин: {}\r\n" +
                            "HSQL пароль: {}",
                        fileName,
                        MAX_QUESTION,
                        FILE_QUESTIONS,
                        PATH_RESULT,
                        FORMAT_RESULT,
                        LOGGER_LEVEL,
                        USER_REGISTRATION,
                        HSQL_PATH,
                        HSQL_DB,
                        HSQL_LOGIN,
                        HSQL_PASSWORD);

            } catch (IOException e) {
                LOG.error(e);
            }
        } else{
            Configurator.setLevel(LOG.getName(), LOGGER_LEVEL);

            LOG.warn("Не найден файл с параметрами {}\r\n" +
                        "Параметры по умолчанию:\r\n" +
                        "Максимальное количество задаваемых вопросов: {}\r\n" +
                        "Файл с вопросами: {}\r\n" +
                        "Путь для сохранения результатов тестирования: {}\r\n" +
                        "Формат файла с результатами тестирования XML или JSON: {}\r\n" +
                        "Уровень логирования: {}\r\n" +
                        "Самостоятельная регистрация пользователей: {}\r\n" +
                        "HSQL путь к базе: {}\r\n" +
                        "HSQL имя базы: {}\r\n" +
                        "HSQL логин: {}\r\n" +
                        "HSQL пароль: {}",
                    fileName,
                    MAX_QUESTION,
                    FILE_QUESTIONS,
                    PATH_RESULT,
                    FORMAT_RESULT,
                    LOGGER_LEVEL,
                    USER_REGISTRATION,
                    HSQL_PATH,
                    HSQL_DB,
                    HSQL_LOGIN,
                    HSQL_PASSWORD);
        }
    }

    public int getMAX_QUESTION() {
        return MAX_QUESTION;
    }

    public String getFILE_QUESTIONS() {
        return FILE_QUESTIONS;
    }

    public String getPATH_RESULT() {
        return PATH_RESULT;
    }

    public String getFORMAT_RESULT() {
        return FORMAT_RESULT;
    }

    public Level getLOGGER_LEVEL() {
        return LOGGER_LEVEL;
    }

    public boolean isUSER_REGISTRATION() { return USER_REGISTRATION; }

    public String getHSQL_PATH() {
        return HSQL_PATH;
    }

    public String getHSQL_DB() { return HSQL_DB; }

    public String getHSQL_LOGIN() {
        return HSQL_LOGIN;
    }

    public String getHSQL_PASSWORD() {
        return HSQL_PASSWORD;
    }
}
