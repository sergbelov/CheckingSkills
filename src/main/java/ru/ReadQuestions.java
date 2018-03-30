package ru;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Белов Сергей
 * Читаем вопросы из файла
 */
public class ReadQuestions {

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
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();
            NodeList tags = doc.getChildNodes();  // узел в XML

            // проходим по каждому элементу
            for (int i = 0; i < tags.getLength(); i++) {
                Node tag = tags.item(i);
                getXMLData("", tag);
            }

        } catch (Exception exc) {
            //exc.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    exc.getMessage(),
                    "Ошибка при чтении данных",
                    JOptionPane.ERROR_MESSAGE);

            System.exit(-1);
        }

        if (answersList.size() > 0) { // добавим последний вопрос из файла
            questionsList.add(
                    new Question(
                            theme,
                            question,
                            answersList));
        }

        return questionsList;
    }

    /**
     * Парсим данные
     *
     * @param prefix
     * @param node
     */
    private void getXMLData(String prefix, Node node) {

        String text = node.getNodeValue();

        if (node != null && text != null && !text.isEmpty() && node.getNodeType() == 8) { // комментарий
//            System.out.println(text);
        } else {

            if (text != null &&
                    !text.isEmpty() &&
                    !formatText(text).isEmpty() &&
                    node.getNodeType() != 8 // не комментарий
                    ) {

//                System.out.println(prefix + " value = \"" + text + "\"");

                if (node.getParentNode().getNodeName().equalsIgnoreCase("theme")) { // тема
                    if (answersList.size() > 0) { // в памяти есть ранее прочитанный блок - добавим в список
                        questionsList.add(
                                new Question(
                                        theme,
                                        question,
                                        answersList));

                        // очистим переменные
                        theme = null;
                        question = null;
                        answersList.clear();
                    }
                    theme = formatText(text);

                } else if (node.getParentNode().getNodeName().equalsIgnoreCase("question")) { // вопрос
                    question = formatText(text);

                } else if (node.getParentNode().getNodeName().equalsIgnoreCase("at")) { // верныый ответ
                    answersList.add(new Answer(formatText(text), true, false));

                } else if (node.getParentNode().getNodeName().equalsIgnoreCase("af")) { // ложный ответ
                    answersList.add(new Answer(formatText(text), false, false));
                }
            }

            NamedNodeMap attributes = node.getAttributes();
            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    Node attr = attributes.item(i);
                    System.out.println(prefix + " attr = \"" + attr.getNodeName() + "\"" + " value = \"" + attr.getTextContent().toString() + "\" ");
                    System.out.println(node.getParentNode());
                }
            }

            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                getXMLData(prefix + node.getNodeName() + ":", children.item(i));
            }
        }
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
