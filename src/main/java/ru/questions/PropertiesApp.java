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
    private int     QUESTION_MAX = 10;                              // максимальное количество задаваемых вопросов
    private String  QUESTION_FILE = "questions\\Questions.json";    // файл с вопросами
    private String  RESULT_PATH = "result\\";                       // путь для сохранения результатов тестирования
    private String  RESULT_FORMAT = "JSON";                         // формат файла с результатами тестирования XML или JSON
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

                this.QUESTION_MAX = Integer.parseInt(pr.getProperty("QUESTION_MAX", "10"));
                this.QUESTION_FILE = pr.getProperty("QUESTION_FILE", "questions\\Questions.json");
                this.RESULT_PATH = pr.getProperty("RESULT_PATH", "Result\\");
                this.RESULT_FORMAT = pr.getProperty("RESULT_FORMAT", "JSON");
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
                        QUESTION_MAX,
                        QUESTION_FILE,
                        RESULT_PATH,
                        RESULT_FORMAT,
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
                    QUESTION_MAX,
                    QUESTION_FILE,
                    RESULT_PATH,
                    RESULT_FORMAT,
                    LOGGER_LEVEL,
                    USER_REGISTRATION,
                    HSQL_PATH,
                    HSQL_DB,
                    HSQL_LOGIN,
                    HSQL_PASSWORD);
        }
    }

    public static Logger getLOG() {
        return LOG;
    }

    public int getQUESTION_MAX() { return QUESTION_MAX; }

    public String getQUESTION_FILE() {
        return QUESTION_FILE;
    }

    public String getRESULT_PATH() {
        return RESULT_PATH;
    }

    public String getRESULT_FORMAT() {
        return RESULT_FORMAT;
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
