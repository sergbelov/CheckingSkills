package ru;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Белов Сергей
 * Сохранение результата тестирования в XML-файл
 */
public class SaveResultTest {

    /**
     * Сохранение результата тестирования в XML-файл
     * @param path          - путь для сохранения
     * @param startTesting  - время начала теста
     * @param stopTesting   - время окончания теста
     * @param theme         - тема
     * @param resultTXT     - результат тестирования
     */
    void save(String path,
              String fileName,
              long startTesting,
              long stopTesting,
              String theme,
              String resultTXT) {

        File file = new File(path);
        file.mkdirs();
//        file = new File(path + System.getProperty("user.name") + ".xml"); //
        file = new File(path + fileName); //

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            Element rootElement = null;

            if (file.exists()) { // файл для данного пользователя уже существует
                doc = builder.parse(file.toString()); // читаем документ
                doc.getDocumentElement().normalize();
                // Получаем корневой элемент из Document
                Node root = doc.getDocumentElement();
                rootElement = (Element) root.getChildNodes();
            } else { // файла нет
                doc = builder.newDocument(); // создаем документ
                // создаем корневой элемент
                rootElement = doc.createElementNS("", "TestResults");
                // добавляем корневой элемент в объект Document
                doc.appendChild(rootElement);
            }

            // добавляем новый узел
            rootElement.appendChild(getTestResult(doc, startTesting, stopTesting, theme, resultTXT));
            doc.getDocumentElement().normalize();

            //создаем объект TransformerFactory для печати в консоль
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            // для красивого вывода в консоль
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);

            //вывод в консоль или файл
//            StreamResult console = new StreamResult(System.out);
            StreamResult streamResult = new StreamResult(file);
            //записываем данные
//            transformer.transform(source, console);
            transformer.transform(source, streamResult);

        } catch (Exception ew) {
//            ew.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    ew.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
//            System.exit(-1);
        } finally {
            if (doc != null) doc = null;
//            Runtime.getRuntime().gc(); // чистка памяти
        }

    }

    // метод для создания нового узла XML-файла
    private static Node getTestResult(Document doc, long startTesting, long stopTesting, String theme, String resultTXT) {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Element result = doc.createElement("TestResult");

        // устанавливаем атрибут id
//        result.setAttribute("id", id);

        // создаем элемент Theme
        result.appendChild(getLanguageElements(doc, result, "Theme", theme));
        // создаем элемент StartTesting
        result.appendChild(getLanguageElements(doc, result, "StartTesting", dateFormat.format(startTesting)));
        // создаем элемент StopTesting
        result.appendChild(getLanguageElements(doc, result, "StopTesting", dateFormat.format(stopTesting)));
        // создаем элемент ResultTXT
        result.appendChild(getLanguageElements(doc, result, "ResultTXT", resultTXT));

        return result;
    }

    // утилитный метод для создание нового узла XML-файла
    private static Node getLanguageElements(Document doc, Element element, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

}
