package ru;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.swing.*;

/**
 * @author Белов Сергей
 * Визуализация (JFrame)
 */
public class CheckingSkills {

    final int maxAnswer = 6; // максимальное количество вариантов ответов на форме

    int MAX_QUESTION_CONST = 10; // макимальное количество задаваемых вопросов по теме
    boolean VISIBLE_ANSWERS = false; // отображать подсказки
    String FILE_QUESTIONS = "questions\\XMLDataTest.xml";
    String PATH_RESULT = "result\\";

    long startTesting; // время начала теста
    boolean visibleAnswers = VISIBLE_ANSWERS; // отображать подсказки

    QuestionsList questionsList;
    SaveResultTest saveResultTest;

    JComboBox cbTheme;
    JLabel lQuestion;
    ButtonGroup bGRB;
    JRadioButton[] arrRB = new JRadioButton[maxAnswer];
    JCheckBox[] arrCB = new JCheckBox[maxAnswer];
    JButton bPrevQuestion, bNextQuestion, bEnd, bNew, bExit;
    JPanel windowContent, p1, p2, p4;
    JFrame mainFrame;
    Object[] options = {"Да", "Нет"};

    Color redColor = new Color(250, 100, 100);
    Color greenColor = new Color(100, 200, 100);
    Color blueColor = new Color(100, 100, 200);
    Color yellowColor = new Color(247, 250, 144);
    Color blackColor = new Color(0, 0, 0);
    Color defaultBackground = null;

    public CheckingSkills() {
//        System.out.println(System.getProperty("user.name"));
//        currPath = new File("").getAbsolutePath()+"\\";
//        currPath = System.getProperty("user.dir")+"\\";
//        JOptionPane.showMessageDialog(null, currPath);

        getParameters("CheckingSkills.parameters"); // читаем параметры из файла

        // вопросы (читаем из файла FILE_QUESTIONS)
        questionsList = new QuestionsList();
        questionsList.setMaxQuestionConst(MAX_QUESTION_CONST);
        questionsList.readQuestionsFromFile(FILE_QUESTIONS);
        questionsList.saveQuestionsGroupByThemes("Cp1251"); // сохраним вопросы с правильными вариантами ответов в файлы (по темам)

        // для записи результатов тестирования
        saveResultTest = new SaveResultTest();
//        saveResultTest.save(PATH_RESULT, startTesting, stopTesting, theme);

        CheckingSkillsEngine etEngine = new CheckingSkillsEngine(this); // слушатель

        // основная панель
        windowContent = new JPanel();
        // Задаём схему для этой панели
        BorderLayout bl = new BorderLayout();
        windowContent.setLayout(bl);

        // тема тестирования
        cbTheme = new JComboBox();
        cbTheme.setModel(new DefaultComboBoxModel(questionsList.getThemesList().toArray()));
        cbTheme.addActionListener(etEngine);

        // группа RadioButton
        bGRB = new ButtonGroup();

        // Компонент для вопроса
        lQuestion = new JLabel(
                "<html>Тестовый вопрос:<br/> " +
                        "Скажи-ка дядя, ведь не даром, Москва спаленная пожаром французам отдана?<br/> " +
                        "Ведь были-ж схватки боевые, да говорят еще какие, " +
                        "не даром помнит вся Россия про день Бородина?</html>");
//        lQuestion.setFont( new Font("Arial",Font.BOLD, 14));
//        lQuestion.setForeground(new Color(100, 150, 100));

        for (int i = 0; i < maxAnswer; i++) {
            // создаем компоненты JRadioButton + слушатель
            arrRB[i] = new JRadioButton();
            arrRB[i].setName("RB" + i);
            arrRB[i].addActionListener(etEngine);
            bGRB.add(arrRB[i]);

            // создаем компоненты JCheckBox + слушатель
            arrCB[i] = new JCheckBox();
            arrCB[i].setName("CB" + i);
            arrCB[i].addActionListener(etEngine);
        }

        defaultBackground = arrRB[0].getBackground();

        // Создаем кнопки + слушатель
        bPrevQuestion = new JButton("Предыдущий вопрос");
        bPrevQuestion.addActionListener(etEngine);
        bNextQuestion = new JButton("Следующий вопрос");
        bNextQuestion.addActionListener(etEngine);
        bEnd = new JButton("Завершить");
        bEnd.addActionListener(etEngine);
        bNew = new JButton("Начать заново");
        bNew.addActionListener(etEngine);
        bExit = new JButton("Выход");
        bExit.addActionListener(etEngine);

        // Создаём панель p1 с GridLayout (для темы и вопроса)
        p1 = new JPanel();
        GridLayout gl1 = new GridLayout(2, 1, 1, 1);
        p1.setLayout(gl1);
        // Помещаем панель p1 в северную область окна
        windowContent.add("North", p1);
        // Добавляем компоненты на панель p1
        p1.add(cbTheme);
        p1.add(lQuestion);

        // Создаём панель p2 с GridLayout (для вариантов ответа - 5 строк, 1 столбец)
        p2 = new JPanel();
        GridLayout gl2 = new GridLayout(maxAnswer, 1, 1, 2);
        p2.setLayout(gl2);
        // Помещаем панель p2 в центральную область окна
        windowContent.add("Center", p2);

        // Создаём панель p4 (кнопки) с GridLayout (1 строчкa, 5 столбцов)
        p4 = new JPanel();
        GridLayout gl4 = new GridLayout(1, 5, 1, 2);
        p4.setLayout(gl4);
        // Помещаем панель p4 в южную область окна
        windowContent.add("South", p4);
        // Добавим кнопки на панель p4
        p4.add(bPrevQuestion);
        p4.add(bNextQuestion);
        p4.add(bEnd);
        p4.add(bNew);
        p4.add(bExit);

        //Создаём фрейм и задаём его основную панель
        mainFrame = new JFrame("Проверка знаний");
        mainFrame.setContentPane(windowContent);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //иконка для приложения
//        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/test3.jpg"));
//        ImageIcon icon = new ImageIcon("/resource/test3.jpg");
        ImageIcon icon = new ImageIcon("test3.jpg");
        Image image = icon.getImage();
        mainFrame.setIconImage(image);

        // делаем размер окна достаточным для того, чтобы вместить все компоненты
//		mainFrame.pack();
        // Задаем размер окна
        mainFrame.setSize(800, 600);
        mainFrame.setVisible(true);

//        begin();
    }

    /**
     * Начинаем новое тестирование
     */
    void begin() {

//        Runtime.getRuntime().gc(); // чистка памяти

        // внешний вид - по умолчанию
        for (int i = 0; i < maxAnswer; i++) {
            arrRB[i].setEnabled(true);
            arrRB[i].setBackground(defaultBackground);
            arrCB[i].setEnabled(true);
            arrCB[i].setBackground(defaultBackground);
        }
        bPrevQuestion.setEnabled(false);
        bNextQuestion.setEnabled(true);
        bEnd.setEnabled(true);

        visibleAnswers = VISIBLE_ANSWERS;
        questionsList.getQuestionsListNum();        // случайная последовательность вопросов
        refreshQuestion();                          // отображаем вопрос
        startTesting = System.currentTimeMillis();  // время старта
    }

    /**
     * Отобразим текущий вопрос на форме
     */
    void refreshQuestion() {

        boolean reply = false;
        int questNum = questionsList.getCurQuestionNum();

        lQuestion.setText("<html>" + questionsList.get(questNum).getQuestion() + "</html>");// вопрос
        p2.removeAll(); // удадяем контролы с p2
        bGRB.clearSelection(); // сброс RadioButton

//        questionsList.get(questNum).answersListShuffle(); // перемешаем ответы

        // варианты ответов
        if (questionsList.get(questNum).getType() == 1) {      // RadioButton
            for (int i = 0; i < questionsList.get(questNum).getCountAnswers(); i++) {
                arrRB[i].setText("<html>" + questionsList.get(questNum).getAnswer(i).getAnswer() + "</html>");
                arrRB[i].setSelected(questionsList.get(questNum).getAnswer(i).isSelected());
                p2.add(arrRB[i]);
                if (questionsList.get(questNum).getAnswer(i).isSelected()) {
                    reply = true;
                }

                if (visibleAnswers) { // подсказки
                    if (questionsList.get(questNum).getAnswer(i).isCorrect() & questionsList.get(questNum).getAnswer(i).isSelected()) {
                        arrRB[i].setBackground(greenColor);
                    } // отмечен правильный ответ
                    else if (questionsList.get(questNum).getAnswer(i).isCorrect()) {
                        arrRB[i].setBackground(yellowColor);
                    } // не отмечен правильный вариант
                    else if (!questionsList.get(questNum).getAnswer(i).isCorrect() & questionsList.get(questNum).getAnswer(i).isSelected()) {
                        arrRB[i].setBackground(redColor);
                    } // отмечен не правильный вариант
                    else arrRB[i].setBackground(defaultBackground);
                }
            }

        } else if (questionsList.get(questNum).getType() == 2) { // CheckBox
            for (int i = 0; i < questionsList.get(questNum).getCountAnswers(); i++) {
                arrCB[i].setText("<html>" + questionsList.get(questNum).getAnswer(i).getAnswer() + "</html>");
                arrCB[i].setSelected(questionsList.get(questNum).getAnswer(i).isSelected());
                p2.add(arrCB[i]);
                if (questionsList.get(questNum).getAnswer(i).isSelected()) {
                    reply = true;
                }

                if (visibleAnswers) {
                    if (questionsList.get(questNum).getAnswer(i).isCorrect() & questionsList.get(questNum).getAnswer(i).isSelected()) {
                        arrCB[i].setBackground(greenColor);
                    } // отмечен правильный ответ
                    else if (questionsList.get(questNum).getAnswer(i).isCorrect()) {
                        arrCB[i].setBackground(yellowColor);
                    } // не отмечен правильный вариант
                    else if (!questionsList.get(questNum).getAnswer(i).isCorrect() & questionsList.get(questNum).getAnswer(i).isSelected()) {
                        arrCB[i].setBackground(redColor);
                    } // отмечен не правильный вариант
                    else arrCB[i].setBackground(defaultBackground);
                }
            }
        }

        if (reply) { // есть вариант ответа
            lQuestion.setForeground(blueColor);
        } else {
            lQuestion.setForeground(blackColor);
        }

        mainFrame.setTitle("Проверка знаний (" + (questionsList.getCurQuestion() + 1) + " из " + questionsList.getMaxQuestion() + ")");

        p2.repaint();
    }


    /**
     * Запомним текущий выбор
     */
    void rememberStatus(boolean clear) {

        for (int i = 0; i < questionsList.get(questionsList.getCurQuestionNum()).getCountAnswers(); i++) {
            questionsList.get(questionsList.getCurQuestionNum()).getAnswer(i).setSelected(
                    arrRB[i].isSelected() | arrCB[i].isSelected());
        }

        if (clear) {
            for (int i = 0; i < maxAnswer; i++) {
                arrRB[i].setSelected(false);
                arrCB[i].setSelected(false);
            }
        }
    }


    /**
     * Проверка корректности ответов
     *
     * @return
     */
    int checkAnswers() {

        int countError = 0;
        int correctAnswerProc = 0;
        String title, message, resultTXT;

        mainFrame.setTitle("Проверка знаний (" + (questionsList.getCurQuestion() + 1) + " из " + questionsList.getMaxQuestion() + ")");

        countError = questionsList.getCountNotCorrectAnswers();

        if (countError == 0) { // ошибок нет
            title = "Тестирование завершено";
            message = "<html>Примите поздравления!<br/>Отличная работа!<br/><br/>Еще разок?</html>";
            resultTXT = "Отлично!!! (" + (questionsList.getMaxQuestion() - countError) +
                    " из " + questionsList.getMaxQuestion() + ")";

        } else { // ошибки есть
            correctAnswerProc = (questionsList.getMaxQuestion() - countError) * 100 / questionsList.getMaxQuestion();

            title = "Тестирование завершено с ошибками";
            message = "<html>Имеются ошибки.<br/>" +
                    "Дан верный ответ на " + (questionsList.getMaxQuestion() - countError) +
                    " из " + questionsList.getMaxQuestion() +
                    ", что составляет " + correctAnswerProc + "%<br/><br/>" +
                    "Анализ ошибок:<br/>" +
                    "<font color=\"#10aa10\">Правильный выбор;</font> <br/>" +
                    "<font color=\"#ffb000\">Нужно было выбрать;</font><br/>" +
                    "<font color=\"#ff10010\">Не правильный выбор.</font><br/></html>";
/*
                    "<font color=\"#10aa10\">Зеленый</font> - правильный выбор<br/>" +
                    "<font color=\"#ffb000\">Желтый</font> - нужно было выбрать<br/>" +
                    "<font color=\"#ff10010\">Красный</font> - не правильный выбор<br/></html>";
*/
            resultTXT = "(" + (questionsList.getMaxQuestion() - countError) +
                    " из " + questionsList.getMaxQuestion() + ") " + correctAnswerProc + "%";

        }

        saveResultTest.save(PATH_RESULT,
                startTesting,
                System.currentTimeMillis(),
                questionsList.getCurTheme(),
                resultTXT);

        if (countError == 0) {
            if (JOptionPane.showOptionDialog(
                    mainFrame,
                    message,
                    title,
                    JOptionPane.OK_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]) == 0) {
                begin();
            } else {
                System.exit(0);
            }
            return 1;

        } else {

            visibleAnswers = true; // отображать подсказки

            // для анализа, отключаем активность контролов
            for (int i = 0; i < maxAnswer; i++) {
                arrRB[i].setEnabled(false);
                arrCB[i].setEnabled(false);
            }
            bEnd.setEnabled(false);

            refreshQuestion();

            JOptionPane.showMessageDialog(
                    mainFrame,
                    message,
                    title,
                    JOptionPane.WARNING_MESSAGE);

            return -1;
        }
    }


    /**
     * читаем параметры из файла
     */
    private void getParameters(String fileName) {
        File file = new File(fileName);
        if (file.exists()) { // найден файл с установками
            try (
                    InputStream is = new FileInputStream(file)
            ) {
                if (is != null) {
                    Properties pr = new Properties();
                    pr.load(is);

                    this.MAX_QUESTION_CONST = (int) Integer.parseInt(pr.getProperty("MAX_QUESTION_CONST", "10"));
                    this.VISIBLE_ANSWERS = (boolean) Boolean.parseBoolean(pr.getProperty("VISIBLE_ANSWER", "FALSE"));
                    this.FILE_QUESTIONS = pr.getProperty("FILE_QUESTIONS", "questions\\XMLDataTest.xml");
                    this.PATH_RESULT = pr.getProperty("PATH_RESULT", "Result\\");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * точка входа
     *
     * @param args
     */
/*
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CheckingSkills tp = new CheckingSkills();
                tp.begin();
            }
        });
    }
*/

}