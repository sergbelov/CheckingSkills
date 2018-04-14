package ru.questions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Белов Сергей
 * Читаем вопросы из Json-файла
 */
public class ReadQuestionsJson implements ReadQuestions{

    private static final Logger LOG = LogManager.getLogger();

    private String author;                                      // автор
    private String theme;                                       // тема
    private String question;                                    // вопрос
    private List<Answer> answersList = new ArrayList<>();       // список вариантов ответа
    private List<Question> questionsList = new ArrayList<>();   // полный список вопросов (все темы)


    public List<Question> read(String fileJson) {

        questionsList.clear();

        File file = new File(fileJson);
        if (!file.exists()) { // файл с вопросами не найден
            LOG.warn("Не найден Json-файл с вопросами " + fileJson);
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

/*
            int bytesRead = -1;
            byte[] buffer = new byte[1024];
            StringBuilder jsonSB = new StringBuilder();

            try (FileInputStream fileInputStream = new FileInputStream(fileJson)) {
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    jsonSB.append(new String(Arrays.copyOf(buffer, bytesRead), "UTF-8"));
                }
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
*/

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            file = new File(fileJson);
            if (file.exists()) {
                List<QuestionJson> questionJsonList = new ArrayList<>();
                try {
                    JsonReader reader = new JsonReader(new FileReader(file.toString()));
                    questionJsonList = gson.fromJson(reader, new TypeToken<List<QuestionJson>>() {}.getType());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                questionJsonList
                        .stream()
                        .forEach(q -> {
                            answersList.clear();
                            for (String a : q.getAnswersTrue()){
                                answersList.add(new Answer(a, true, false));
                            }
                            for (String a : q.getAnswersFalse()){
                                answersList.add(new Answer(a, false, false));
                            }
                            questionsList.add(
                                    new Question(
                                            q.getAuthor(),
                                            q.getTheme(),
                                            q.getQuestion(),
                                            answersList));

                        });
            }


/*

            try {
                JSONObject jsonObject = new JSONObject(jsonSB.toString());
//            System.out.println(jsonObject);
                JSONArray jsonArray = jsonObject.getJSONArray("questions");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                    if (!jsonObject2.isNull("author")) {
                        author = jsonObject2.getString("author");
                    } else {
                        author = "";
                    }
                    theme = jsonObject2.getString("theme");
                    question = jsonObject2.getString("question");

                    JSONArray jsonArray2 = jsonObject2.getJSONArray("answersTrue");
                    for (int j = 0; j < jsonArray2.length(); j ++){
                        answersList.add(new Answer(jsonArray2.getString(j), true, false));
                    }

                    jsonArray2 = jsonObject2.getJSONArray("answersFalse");
                    for (int j = 0; j < jsonArray2.length(); j ++){
                        answersList.add(new Answer(jsonArray2.getString(j), false, false));
                    }

                    if (answersList.size() > 0) { // есть прочитанный блок - добавим в список
                        questionsList.add(
                                new Question(
                                    author,
                                    theme,
                                    question,
                                    answersList));
                    }
                    // очистим переменные
                    author = null;
                    theme = null;
                    question = null;
                    answersList.clear();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
*/

        }
        return questionsList;
    }
}
