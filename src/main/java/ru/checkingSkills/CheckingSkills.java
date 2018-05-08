package ru.checkingSkills;

import ru.questions.PropertiesApp;
import ru.questions.Question;
import ru.questions.Questions;

import java.awt.*;
import javax.swing.*;

/**
 * @author Белов Сергей
 * Визуализация (JFrame)
 */
public class CheckingSkills {

    final int maxAnswer = 6; // максимальное количество вариантов ответов на форме
    final String FILE_PROPERTIES = "CheckingSkills.properties";

    PropertiesApp propertiesApp = new PropertiesApp();
    Questions questions = new Questions();; // список вопросов с ответами

    JComboBox cbTheme;
    JLabel lQuestion;
    ButtonGroup bGRB;
    JRadioButton[] arrRB = new JRadioButton[maxAnswer];
    JCheckBox[] arrCB = new JCheckBox[maxAnswer];
    JButton bPrevQuestion, bNextQuestion, bEnd, bNew, bExit;
    JPanel windowContent, p1, p2, p4;
    JFrame mainFrame;
    Object[] options = {"Да", "Нет"};
    Object[] options2 = {"Еще раз", "Выход"};

    Color blueColor = new Color(100, 100, 200);
    Color blackColor = new Color(0, 0, 0);
    Color defaultBackground = null;

    public CheckingSkills() {
//        System.out.println(System.getProperty("user.name"));
//        currPath = new File("").getAbsolutePath()+"\\";
//        currPath = System.getProperty("user.dir")+"\\";
//        JOptionPane.showMessageDialog(null, currPath);

//        questions.readQuestions(FILE_PROPERTIES);

        questions.setUser(System.getProperty("user.name"));
        CheckingSkillsListener listener = new CheckingSkillsListener(this); // слушатель

        // основная панель
        windowContent = new JPanel();
        // Задаём схему для этой панели
        BorderLayout bl = new BorderLayout();
        windowContent.setLayout(bl);

        // тема для тестирования
        cbTheme = new JComboBox();
//        cbTheme.setModel(new DefaultComboBoxModel(questions.getThemesList().toArray()));
//        cbTheme.setSelectedIndex(0);
        cbTheme.addActionListener(listener);

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
            arrRB[i].addActionListener(listener);
            bGRB.add(arrRB[i]);

            // создаем компоненты JCheckBox + слушатель
            arrCB[i] = new JCheckBox();
            arrCB[i].setName("CB" + i);
            arrCB[i].addActionListener(listener);
        }

        defaultBackground = arrRB[0].getBackground();

        // Создаем кнопки + слушатель
        bPrevQuestion = new JButton("Предыдущий вопрос");
        bPrevQuestion.addActionListener(listener);
        bNextQuestion = new JButton("Следующий вопрос");
        bNextQuestion.addActionListener(listener);
        bEnd = new JButton("Завершить");
        bEnd.addActionListener(listener);
        bNew = new JButton("Начать заново");
        bNew.addActionListener(listener);
        bExit = new JButton("Выход");
        bExit.addActionListener(listener);

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
    }

    /**
     * Начинаем новое тестирование
     */
    public void start() {
//        Runtime.getRuntime().gc(); // чистка памяти

//        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
//        System.out.println(stackTraceElements[2].getMethodName());

        propertiesApp.readProperties(FILE_PROPERTIES); // параметры из файла

        questions.readQuestions(
                propertiesApp.getMAX_QUESTION(),
                propertiesApp.getFILE_QUESTIONS(),
                propertiesApp.getPATH_RESULT(),
                propertiesApp.getFORMAT_RESULT(),
                propertiesApp.getLOGGER_LEVEL()); // читаем вопросы из файла

        if (questions.getThemesList().size() > 0) {
            String curTheme = (String) cbTheme.getSelectedItem(); // запоминаем текущую тему
            cbTheme.setModel(new DefaultComboBoxModel(questions.getThemesList().toArray())); // список тем
//            cbTheme.validate();
            if (curTheme != null && !curTheme.isEmpty()) {
                cbTheme.setSelectedItem(curTheme); // указатель на запомненную тему
            }
            curTheme = (String) cbTheme.getSelectedItem(); // текущая тема

            questions.setTheme(curTheme);

            // внешний вид - по умолчанию
            for (int i = 0; i < maxAnswer; i++) {
                arrRB[i].setEnabled(true);
                arrRB[i].setBackground(defaultBackground);
                arrRB[i].setSelected(false);

                arrCB[i].setEnabled(true);
                arrCB[i].setBackground(defaultBackground);
                arrCB[i].setSelected(false);
            }

            bEnd.setEnabled(true);
            questions.start();  // начинаем тестирование
            refreshQuestion();  // отображаем вопрос с вариантами ответов

        } else{
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "Ошибка при чтении вопросов из файла",
                    "Ошибка при чтении вопросов из файла",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(0);
        }
//        long usedBytes = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
//        System.out.println(usedBytes);
    }

    /**
     * Отобразим текущий вопрос на форме
     */
    public void refreshQuestion() {

        boolean answered = false;

//        lQuestion.setText("<html>" + questions.get().getQuestion() + "</html>");// вопрос
        lQuestion.setText(
                "<html>" +
                        ((questions.get().getAuthor() == null) ?
                                "" :
                                "<h6>"+questions.get().getAuthor() + "</h6>") +
                "<h3>"+questions.get().getQuestion() +
                "</h3></html>");// вопрос

        p2.removeAll(); // удадяем контролы с p2
        bGRB.clearSelection(); // сброс RadioButton

        // варианты ответов
        if (questions.get().getType().equals(Question.TypeOfAnswer.radio)) { // RadioButton
            for (int i = 0; i < questions.get().getCountAnswers(); i++) {
                arrRB[i].setText("<html>" + questions.get().getAnswer(i).getAnswer() + "</html>");
                arrRB[i].setSelected(questions.get().getAnswer(i).isSelected());
                p2.add(arrRB[i]);
                if (questions.get().getAnswer(i).isSelected()) {
                    answered = true;
                }

                if (!questions.isStarted()) { // подсказки
                    arrRB[i].setBackground(questions.get().getAnswer(i).getColor());
                }
            }

        } else if (questions.get().getType().equals(Question.TypeOfAnswer.checkbox)) { // CheckBox
            for (int i = 0; i < questions.get().getCountAnswers(); i++) {
                arrCB[i].setText("<html>" + questions.get().getAnswer(i).getAnswer() + "</html>");
                arrCB[i].setSelected(questions.get().getAnswer(i).isSelected());
                p2.add(arrCB[i]);
                if (questions.get().getAnswer(i).isSelected()) {
                    answered = true;
                }

                if (!questions.isStarted()) { // подсказки
                    arrCB[i].setBackground(questions.get().getAnswer(i).getColor());
                }
            }
        }

        if (answered) { // есть вариант ответа
            lQuestion.setForeground(blueColor);
        } else {
            lQuestion.setForeground(blackColor);
        }

        bPrevQuestion.setEnabled(!questions.isFirstQuestion()); // первый вопрос ?
        bNextQuestion.setEnabled(!questions.isLastQuestion());  // последний вопрос ?

        mainFrame.setTitle("Проверка знаний (" + (questions.getQuestionNum() + 1) + " из " + questions.getMaxQuestion() + ")");

        p2.repaint();
    }

    /**
     * Запомним текущий выбор
     */
    public void rememberStatus(boolean isClear) {

        for (int i = 0; i < questions.get().getCountAnswers(); i++) {
            questions
                    .get()
                    .getAnswer(i)
                    .setSelected(arrRB[i].isSelected() | arrCB[i].isSelected());

            if (isClear) {
                arrRB[i].setSelected(false);
                arrCB[i].setSelected(false);
            }
        }
    }

    /**
     * Завершаем тестирование
     * Проверка корректности ответов
     *
     * @return
     */
    public int stop() {

        String message = questions.stop();

        if (message.contains("Примите поздравления!")) {
            if (JOptionPane.showOptionDialog(
                    mainFrame,
                    message,
                    "Тестирование завершено без ошибок",
                    JOptionPane.OK_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options2,
                    options2[0]) == 0) {
                start();
            } else {
                System.exit(0);
            }
            return 1;

        } else {

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
                    "Тестрование завершено с ошибками",
                    JOptionPane.WARNING_MESSAGE);

            return -1;
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
                checkingSkills tp = new checkingSkills();
                tp.start();
            }
        });
    }
*/

}