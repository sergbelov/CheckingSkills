package ru.questions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Белов Сергей
 * Читаем вопросы из файла
 */
public class ReadQuestions {

    private static final Logger LOG = LogManager.getLogger();

    private String author;                                      // автор
    private String theme;                                       // тема
    private String question;                                    // вопрос
    private List<Answer> answersList = new ArrayList<>();       // список вариантов ответа
    private List<Question> questionsList = new ArrayList<>();   // полный список вопросов (все темы)

    /**
     * Читаем вопросы из файла
     *
     * @param fileXML
     */
    public List<Question> read(String fileXML) {

        questionsList.clear();

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
                    }

                    nodesList2 = element.getElementsByTagName("at");
                    for (int x = 0; x < nodesList2.getLength(); x++) {
                        answersList.add(new Answer(formatText(nodesList2.item(x).getTextContent()), true, false));
                    }

                    nodesList2 = element.getElementsByTagName("af");
                    for (int x = 0; x < nodesList2.getLength(); x++) {
                        answersList.add(new Answer(formatText(nodesList2.item(x).getTextContent()), false, false));
                    }

                    if (answersList.size() > 0) { // есть прочитанный блок - добавим в список
                        questionsList.add(
                                new Question(
                                        author,
                                        theme,
                                        question,
                                        answersList));

                        // очистим переменные
                        author = null;
                        theme = null;
                        question = null;
                        answersList.clear();
                    }
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            LOG.error(ex);
        }

        return questionsList;
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
