package ru.questions;

import java.awt.*;
import javax.swing.*;

/**
 * @author Белов Сергей
 * Визуализация (JFrame)
 */
public class CheckingSkills {

    final int maxAnswer = 6; // максимальное количество вариантов ответов на форме
    private long startTesting; // время начала теста
    private boolean visibleAnswers = false; // отображать подсказки
    Questions questions = new Questions("CheckingSkills.properties");; // список вопросов с ответами

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

        questions.readQuestions();

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
    public void begin() {
//        Runtime.getRuntime().gc(); // чистка памяти

        String curTheme = (String) cbTheme.getSelectedItem(); // запоминаем текущую тему
        questions.readQuestions(); // читаем вопросы из файла
        cbTheme.setModel(new DefaultComboBoxModel(questions.getThemesList().toArray()));
        cbTheme.validate();
        if (curTheme != null && !curTheme.isEmpty()) {
            cbTheme.setSelectedItem(curTheme); // указатель на запомненную тему
        }
        questions.setThemeNum(cbTheme.getSelectedIndex());

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
        visibleAnswers = questions.isVisibleAnswers();
        questions.generateRandomQuestionsArr(); // случайная последовательность вопросов
        refreshQuestion();                          // отображаем вопрос
        startTesting = System.currentTimeMillis();  // время старта
/*
        long usedBytes = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        System.out.println(usedBytes);
*/
    }

    /**
     * Отобразим текущий вопрос на форме
     */
    public void refreshQuestion() {

        boolean answered = false;

        lQuestion.setText("<html>" + questions.getCur().getQuestion() + "</html>");// вопрос
        p2.removeAll(); // удадяем контролы с p2
        bGRB.clearSelection(); // сброс RadioButton

        // варианты ответов
        if (questions.getCur().getType() == 1) {      // RadioButton
            for (int i = 0; i < questions.getCur().getCountAnswers(); i++) {
                arrRB[i].setText("<html>" + questions.getCur().getAnswer(i).getAnswer() + "</html>");
                arrRB[i].setSelected(questions.getCur().getAnswer(i).isSelected());
                p2.add(arrRB[i]);
                if (questions.getCur().getAnswer(i).isSelected()) {
                    answered = true;
                }

                if (visibleAnswers) { // подсказки
                    if (questions.getCur().getAnswer(i).isCorrect() & questions.getCur().getAnswer(i).isSelected()) {
                        arrRB[i].setBackground(greenColor);
                    } // отмечен правильный ответ
                    else if (questions.getCur().getAnswer(i).isCorrect()) {
                        arrRB[i].setBackground(yellowColor);
                    } // не отмечен правильный вариант
                    else if (!questions.getCur().getAnswer(i).isCorrect() & questions.getCur().getAnswer(i).isSelected()) {
                        arrRB[i].setBackground(redColor);
                    } // отмечен не правильный вариант
                    else arrRB[i].setBackground(defaultBackground);
                }
            }

        } else if (questions.getCur().getType() == 2) { // CheckBox
            for (int i = 0; i < questions.getCur().getCountAnswers(); i++) {
                arrCB[i].setText("<html>" + questions.getCur().getAnswer(i).getAnswer() + "</html>");
                arrCB[i].setSelected(questions.getCur().getAnswer(i).isSelected());
                p2.add(arrCB[i]);
                if (questions.getCur().getAnswer(i).isSelected()) {
                    answered = true;
                }

                if (visibleAnswers) {
                    if (questions.getCur().getAnswer(i).isCorrect() & questions.getCur().getAnswer(i).isSelected()) {
                        arrCB[i].setBackground(greenColor);
                    } // отмечен правильный ответ
                    else if (questions.getCur().getAnswer(i).isCorrect()) {
                        arrCB[i].setBackground(yellowColor);
                    } // не отмечен правильный вариант
                    else if (!questions.getCur().getAnswer(i).isCorrect() & questions.getCur().getAnswer(i).isSelected()) {
                        arrCB[i].setBackground(redColor);
                    } // отмечен не правильный вариант
                    else arrCB[i].setBackground(defaultBackground);
                }
            }
        }

        if (answered) { // есть вариант ответа
            lQuestion.setForeground(blueColor);
        } else {
            lQuestion.setForeground(blackColor);
        }

        bPrevQuestion.setEnabled(!questions.isFirstQuestion()); // первый вопрос ?
        bNextQuestion.setEnabled(!questions.isLastQuestion()); // последний вопрос ?

        mainFrame.setTitle("Проверка знаний (" + (questions.getQuestionNumOnForm() + 1) + " из " + questions.getMaxQuestion() + ")");

        p2.repaint();
    }

    /**
     * Запомним текущий выбор
     */
    public void rememberStatus(boolean isClear) {

        for (int i = 0; i < questions.getCur().getCountAnswers(); i++) {
            questions
                    .getCur()
                    .getAnswer(i)
                    .setSelected(arrRB[i].isSelected() | arrCB[i].isSelected());

            if (isClear) {
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
    public int checkAnswers() {

        String title, message, resultTXT;
        int countError = questions.getCountNotCorrectAnswers();

        if (countError == 0) { // ошибок нет
            title = "Тестирование завершено";
            message = "<html>Примите поздравления!<br/>Отличная работа!<br/><br/>Еще разок?</html>";
            resultTXT = "Отлично!!! (" + (questions.getMaxQuestion() - countError) +
                    " из " + questions.getMaxQuestion() + ")";

        } else { // ошибки есть
            int correctAnswerProc = (questions.getMaxQuestion() - countError) * 100 / questions.getMaxQuestion();

            title = "Тестирование завершено с ошибками";
            message = "<html>Имеются ошибки.<br/>" +
                    "Дан верный ответ на " + (questions.getMaxQuestion() - countError) +
                    " из " + questions.getMaxQuestion() +
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
            resultTXT = "(" + (questions.getMaxQuestion() - countError) +
                    " из " + questions.getMaxQuestion() + ") " + correctAnswerProc + "%";

        }

        questions.saveResultTest(startTesting, resultTXT); // сохраняем результат тестирования

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