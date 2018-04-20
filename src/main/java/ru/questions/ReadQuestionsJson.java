package ru.questions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Белов Сергей
 * Читаем вопросы из Json-файла
 */
public class ReadQuestionsJson implements ReadQuestions{

    private static final Logger LOG = LogManager.getLogger();

    private List<QuestionJson> questionsJsonList = new ArrayList<>(); // полный список вопросов (все темы)

    @Override
    public List<QuestionJson> read(String fileJson) {

        Configurator.setLevel(LOG.getName(), Questions.LOGGER_LEVEL);

        questionsJsonList.clear();

        File file = new File(fileJson);
        if (!file.exists()) { // файл с вопросами не найден
            LOG.warn("Не найден файл с вопросами " + fileJson);
/*
            JOptionPane.showMessageDialog(
                    null,
                    "<html>Не найден Json-файл с вопросами<br/>" +
                            "Укажите данный файл...</html>");

            JFileChooser dialog = new JFileChooser();
            dialog.showOpenDialog(null);
            file = dialog.getSelectedFile();
            if (file != null) {
                file = new File(file.getAbsolutePath());
            }
*/
        } else {

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            file = new File(fileJson);
            if (file.exists()) {
                try(
//                    JsonReader reader = new JsonReader(new FileReader(file.toString()));
                    JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
                ) {
                    questionsJsonList = gson.fromJson(reader, new TypeToken<List<QuestionJson>>() {}.getType());
                } catch (FileNotFoundException e) {
                    LOG.error("FileNotFoundException", e);
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    LOG.error("UnsupportedEncodingException", e);
                    e.printStackTrace();
                } catch (IOException e) {
                    LOG.error("IOException", e);
                    e.printStackTrace();
                }
            }
        }
        return questionsJsonList;
    }
}
