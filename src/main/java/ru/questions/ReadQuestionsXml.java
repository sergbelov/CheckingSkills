package ru.questions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Белов Сергей
 * Читаем вопросы из XML-файла
 */
public class ReadQuestionsXml implements ReadQuestions{

    private static final Logger LOG = LogManager.getLogger();

    private String author;                                              // автор
    private String theme;                                               // тема
    private String question;                                            // вопрос
    private List<QuestionJson> questionsJsonList = new ArrayList<>();   // полный список вопросов (все темы)

    // для преобразования XML в Json
    private List<String> answersTrue = new ArrayList<>();
    private List<String> answersFalse = new ArrayList<>();

    /**
     * Читаем вопросы из файла
     *
     * @param fileXML
     */
//    public List<Question> read(String fileXML) {
    public List<QuestionJson> read(String fileXML) {

        questionsJsonList.clear();

        File file = new File(fileXML);
        if (!file.exists()) { // файл с вопросами не найден
            LOG.warn("Не найден XML-файл с вопросами " + fileXML);
/*
            JOptionPane.showMessageDialog(
                    null,
                    "<html>Не найден XML-файл с вопросами<br/>" +
                            "Укажите данный файл...</html>");

            JFileChooser dialog = new JFileChooser();
            dialog.showOpenDialog(null);
            file = dialog.getSelectedFile();
            if (file != null) {
                file = new File(file.getAbsolutePath());
            }
*/
        }

        try {
            // Строим объектную модель исходного XML файла
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);

            // Выполнять нормализацию не обязательно, но рекомендуется
            doc.getDocumentElement().normalize();

            // Получаем все узлы с именем "QuestionBlock"
            NodeList nodesList = doc.getElementsByTagName("QuestionBlock");
            NodeList nodesList2;

            for (int i = 0; i < nodesList.getLength(); i++) {
                Node node = nodesList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element element = (Element) node;
                    theme = formatText(element.getElementsByTagName("theme").item(0).getTextContent());
                    question = formatText(element.getElementsByTagName("question").item(0).getTextContent());
                    if (element.getElementsByTagName("author").item(0) != null) {
                        author = formatText(element.getElementsByTagName("author").item(0).getTextContent());
                    } else {
                        author = "";
                    }
                    nodesList2 = element.getElementsByTagName("at");
                    for (int x = 0; x < nodesList2.getLength(); x++) {
                        answersTrue.add(formatText(nodesList2.item(x).getTextContent()));
                    }

                    nodesList2 = element.getElementsByTagName("af");
                    for (int x = 0; x < nodesList2.getLength(); x++) {
                        answersFalse.add(formatText(nodesList2.item(x).getTextContent()));
                    }

                    questionsJsonList.add(
                            new QuestionJson(
                                    author,
                                    theme,
                                    question,
                                    answersTrue,
                                    answersFalse));

                    answersTrue.clear();
                    answersFalse.clear();
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            LOG.error(ex);
        }


        // место нахождение файла с вопросами
        File parentFolder = new File(file.getAbsolutePath()
                                .substring(0, file.getAbsolutePath().lastIndexOf(
                                File.separator)));

        // запишем массив с вопросами в Json-файл
//        Gson gson = new Gson();
        Gson gson = new GsonBuilder() // с форматированием
                            .setPrettyPrinting()
                            .create();

        String json = gson.toJson(questionsJsonList);
        try(
//                FileWriter fw = new FileWriter("JSONDataTest.json", false);
                BufferedWriter fw = new BufferedWriter(
                                        new OutputStreamWriter(
                                            new FileOutputStream(parentFolder.getAbsolutePath() + "/JSONDataTest.json", false),
                                            "UTF-8"));
            ){
//            fw.write("{\"questions\":"+ json +"}");
            fw.write( json );
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        };

//        return questionsList;
        return questionsJsonList;
    }


    /**
     * Уберем из строки: переводы строк, табуляции, двойные пробелы
     *
     * @return
     */
    private String formatText(String text) {
        if (text != null) {
            return text
                    .replaceAll("\t", " ")
                    .replaceAll("\r", "")
                    .replaceAll("\n", " ")
                    .replaceAll("[\\s]{2,}", " ")
                    .trim();
        } else {
            return text;
        }
    }

}