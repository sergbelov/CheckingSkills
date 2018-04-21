package ru.questions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Белов Сергей
 * Сохранение результата тестирования в XML-файл
 */
public class SaveResultJson implements SaveResult {

    private static final Logger LOG = LogManager.getLogger();

    /**
     * Сохранение результата тестирования в XML-файл
     *
     * @param path         - путь для сохранения
     * @param fileName     - имя файла
     * @param user         - пользователь
     * @param startingTime - время начала теста
     * @param stoppingTime  - время окончания теста
     * @param theme        - тема
     * @param resultTXT    - результат тестирования
     */
    public void save(String path,
                     String fileName,
                     String user,
                     long startingTime,
                     long stoppingTime,
                     String theme,
                     String resultTXT) {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        ResultTest resultTest = new ResultTest(
                user,
                dateFormat.format(startingTime),
                dateFormat.format(stoppingTime),
                theme,
                resultTXT);

        List<ResultTest> resultTestList = new ArrayList<>();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        File file = new File(path);
        file.mkdirs();
        file = new File(path + fileName);
        if (file.exists()) {
            try(
//                JsonReader reader = new JsonReader(new FileReader(file.toString()));
                JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
            ) {
                resultTestList = gson.fromJson(reader, new TypeToken<List<ResultTest>>() {}.getType());
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
        resultTestList.add(resultTest);
        String json = gson.toJson(resultTestList);
        try(
            BufferedWriter fw = new BufferedWriter(
                                    new OutputStreamWriter(
                                        new FileOutputStream(file.toString(), false),
                                        "UTF-8"));
        ) {
            fw.write(json );
            fw.flush();
        } catch (IOException e) {
            LOG.error("IOException", e);
            e.printStackTrace();
        };

    }

    // класс с результатами тестирования
    class ResultTest {
        private String user;
        private String startingTime;
        private String stoppingTime;
        private String theme;
        private String result;

        public ResultTest(String user,
                          String startingTime,
                          String stoppingTime,
                          String theme,
                          String result) {

            this.user = user;
            this.startingTime = startingTime;
            this.stoppingTime = stoppingTime;
            this.theme = theme;
            this.result = result;
        }

        public String getUser() {
            return user;
        }

        public String getStartingTime() {
            return startingTime;
        }

        public String getStoppingTime() {
            return stoppingTime;
        }

        public String getTheme() {
            return theme;
        }

        public String getResult() {
            return result;
        }
    }
}

