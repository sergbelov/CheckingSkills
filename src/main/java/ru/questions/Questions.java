package ru.questions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.Level;
//import org.apache.logging.log4j.core.config.Configurator;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Белов Сергей
 * Список всех вопросов
 */
public class Questions {

    private static final Logger LOG = LogManager.getLogger();

    // checkingSkills.properties
    private int MAX_QUESTION_CONST = 10;                            // макимальное количество задаваемых вопросов
    private boolean VISIBLE_ANSWERS = false;                        // отображать подсказки
    private String FILE_QUESTIONS = "questions\\XMLDataTest.xml";   // файл с вопросами
    private String PATH_RESULT = "result\\";                        // путь для сохранения результатов тестирования

    private String theme;                   // текущая тема
    private int maxQuestion;                // максимальное количество задаваемых вопросов с учетом имеющихся по теме
    private int questionNumOnForm;          // номер текущего вопроса (порядковый на форме)
    private int[] randomQuestionsArr;       // случайная последовательность номеров вопросов по теме
    private List<String> themesList;        // список тем
    private List<Question> questionsList;   // список вопросов (все темы)

    private String user = System.getProperty("user.name");      // текущий пользователь
    private long startTesting;                                  // время начала теста

    private ReadQuestions readQuestions = new ReadQuestions();  // читаем вопросы из XML-файла
    private SaveResult saveResult = new SaveResult();           // запись результатов тестирования в XML-файл


    /**
     * Текущий пользователь
     */
    public void setUser(String user) { this.user = user; }

    /**
     * Текущий пользователь
     *
     * @return
     */
    public String getUser() { return user; }

    /**
     * Читаем вопросы из файла FILE_QUESTIONS
     */
    public void readQuestions(String fileProperties) {

        getProperties(fileProperties); // Читаем параметры из файла fileProperties

//        LOG.info( "\r\nПуть к файлу CheckingSkills.properties :\t" + fileProperties +
//                  "\r\nПуть к файлу с вопросами :\t\t\t" + FILE_QUESTIONS);

        // список вопросов (все темы)
        questionsList = new ArrayList<>(readQuestions.read(FILE_QUESTIONS));

        // список тем
        themesList = new ArrayList<>(
                questionsList
                    .stream()
                    .map(Question::getTheme)
                    .sorted()
                    .distinct()
                    .collect(Collectors.toList()));

//        saveQuestionsGroupByThemes("Cp1251"); // сохраним вопросы с правильными вариантами ответов в файлы (по темам)
    }

    /**
     * Задаем текущую тему
     *      Максимально количество задаваемых вопросов
     *      (равно количеству вопросов по теме, но не более MAX_QUESTION_CONST)
     *
     * @param theme
     */
    public void setTheme(String theme) {
        this.theme = theme;
        maxQuestion = Math.min(getCountQuestionsInTheme(theme), MAX_QUESTION_CONST);
    }

    /**
     * Текущая тема
     *
     * @return
     */
    public String getTheme() { return theme; }

    /**
     * Максимальное количество задаваемых вопросов (с учетом имеющихся по теме)
     *
     * @return
     */
    public int getMaxQuestion() { return maxQuestion; }

    /**
     * Количество вопросов по теме
     *
     * @param theme
     * @return
     */
    public int getCountQuestionsInTheme(String theme) {
        return (int) questionsList
                .stream()
                .filter((x) -> x.getTheme().equals(theme))
                .count();
    }

    /**
     * Сисок тем
     *
     * @return
     */
    public List<String> getThemesList() { return themesList; }

    /**
     * Вопрос по номеру
     *
     * @param questionNum
     * @return
     */
    public Question getByNum(int questionNum) {
        return questionsList.get(questionNum);
    }

    /**
     * Вопрос текущий
     *
     * @return
     */
    public Question get() { return questionsList.get(getCurQuestionNum()); }

    /**
     * Текущий номер вопроса на форме
     *
     * @return
     */
    public int getQuestionNumOnForm() {
        return questionNumOnForm;
    }

    /**
     * Текущий номер вопроса в общем списке
     *
     * @return
     */
    public int getCurQuestionNum() { return randomQuestionsArr[questionNumOnForm]; }

    /**
     * Переход к предыдущему вопросу
     */
    public void prevQuestion() {
        if (questionNumOnForm > 0) questionNumOnForm--;
    }

    /**
     * Переход к следующему вопросу
     */
    public void nextQuestion() {
        if (questionNumOnForm < maxQuestion - 1) questionNumOnForm++;
    }

    /**
     * Первый вопрос в списке?
     *
     * @return
     */
    public boolean isFirstQuestion() {
        return questionNumOnForm == 0;
    }

    /**
     * Последний вопрос в списке ?
     *
     * @return
     */
    public boolean isLastQuestion() {
        return questionNumOnForm == (maxQuestion - 1);
    }

    /**
     * Отображать подсказки ?
     */
    public boolean isVisibleAnswers() {
        return VISIBLE_ANSWERS;
    }

    /**
     * Корректность ответов (количество ответов с ошибками)
     *
     * @return
     */
    public int getCountNotCorrectAnswers() {
        return (int) Arrays
                .stream(randomQuestionsArr)
                .filter(n -> !questionsList.get(n).isAnswerCorrect())
                .count();
    }

    /**
     * Читаем параметры из файла
     *
     * @param fileName
     */
    private void getProperties(String fileName) {
        File file = new File(fileName);
        if (file.exists()) { // найден файл с установками
            try (
                InputStream is = new FileInputStream(file)
            ) {
                Properties pr = new Properties();
                pr.load(is);

                this.MAX_QUESTION_CONST = Integer.parseInt(pr.getProperty("MAX_QUESTION_CONST", "10"));
                this.VISIBLE_ANSWERS = Boolean.parseBoolean(pr.getProperty("VISIBLE_ANSWER", "FALSE"));
                this.FILE_QUESTIONS = pr.getProperty("FILE_QUESTIONS", "questions\\XMLDataTest.xml");
                this.PATH_RESULT = pr.getProperty("PATH_RESULT", "Result\\");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Сохраним вопросы с правильными ответами (сгруппировав по темам)
     */
    public void saveQuestionsGroupByThemes(final String charset) {
//        StandardCharsets.US_ASCII.name();
        StringBuilder sbQuestions = new StringBuilder();

        themesList // список тем
                .stream()
                .sorted()
                .forEach(t -> {

                    try (
                            FileOutputStream fileOutPutStream = new FileOutputStream(t.replace("\\", "_") + ".txt", false);
                            BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(fileOutPutStream,
                                    charset != null & charset.length() > 0 ? charset : "Cp1251"));
                    ) {
                        sbQuestions.setLength(0);
                        sbQuestions
                                .append("##################################################\r\n")
                                .append(t)
                                .append("\r\n##################################################\r\n");

                        questionsList // список вопросов по теме
                                .stream()
                                .filter(q -> q.getTheme().equals(t))
                                .forEach(q -> {
                                    sbQuestions
                                            .append("==================================================\r\n")
                                            .append(q.getQuestion())
                                            .append("\r\n");
                                    for (int a = 0; a < q.getCountAnswers(); a++) {
                                        if (q.getAnswer(a).isCorrect()) {
                                            sbQuestions
                                                    .append("..................................................\r\n")
                                                    .append(q.getAnswer(a).getAnswer())
                                                    .append("\r\n");
                                        }
                                    }
                                });
                        bufferWriter.write(sbQuestions.toString());
                        bufferWriter.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
    }

    /**
     * Начинаем тестирование
     */
    public void start() {

        questionNumOnForm = 0;

        // сбрасываем текущий выбор ответов (если есть)
        if (randomQuestionsArr != null) {
            Arrays
                    .stream(randomQuestionsArr)
                    .forEach(n -> {
                        questionsList.get(n).clearAnswersSelected();    // сбросим текущий выбор
                        questionsList.get(n).answersListShuffle();      // перемешаем варианты ответов
                    });
        }

        // выберем maxQuestion случайных вопросов из текущей темы
        Random random = new Random();
        randomQuestionsArr = IntStream
                .generate(() -> random.nextInt(questionsList.size()))
                .distinct()
                .filter(n -> questionsList.get(n).getTheme().equals(theme))
                .limit(maxQuestion)
                .toArray();

        startTesting = System.currentTimeMillis();  // время старта
    }

    /**
     * Заканчиваем тестирование
     * Сохраним результат тестирования
     *
     * @return
     */
    public String stop() {
        String message, resultTXT;
        int countError = getCountNotCorrectAnswers();
        int correctAnswer = getMaxQuestion() - countError;
        int correctAnswerProc = correctAnswer * 100 / getMaxQuestion();

        if (countError == 0) { // ошибок нет
            message = "<html>Примите поздравления!<br/>Отличная работа!<br/><br/>Еще разок?</html>";
            resultTXT = "Отлично!!! (" + correctAnswer + " из " + getMaxQuestion() + ")";

        } else { // ошибки есть

            message = "<html>Имеются ошибки.<br/>" +
                    "Дан верный ответ на " + correctAnswer +
                    " из " + getMaxQuestion() +
                    " ( " + correctAnswerProc + "% )<br/><br/>" +
                    "Анализ ошибок:<br/>" +
                    "<font color=\"#10aa10\">Правильный выбор;</font> <br/>" +
                    "<font color=\"#ffb000\">Нужно было выбрать;</font><br/>" +
                    "<font color=\"#ff10010\">Не правильный выбор.</font><br/></html>";
            resultTXT = "(" + correctAnswer + " из " + getMaxQuestion() + ") " + correctAnswerProc + "%";
        }

        // сохраняем результат тестирования
        saveResult.save(
                PATH_RESULT,
//                System.getProperty("user.name") + ".xml",
                "result.xml",
                user,
                startTesting,
                System.currentTimeMillis(),
                getTheme(),
                resultTXT);

        return message;
    }

}