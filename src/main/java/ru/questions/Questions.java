package ru.questions;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
//import org.apache.logging.log4j.Level;
//import org.apache.logging.log4j.core.config.Configurator;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private int MAX_QUESTION_CONST = 10;                        // макимальное количество задаваемых вопросов
    private boolean VISIBLE_ANSWERS = false;                    // отображать подсказки
    private String FILE_QUESTIONS = "questions\\Questions.json";// файл с вопросами
    private String PATH_RESULT = "result\\";                    // путь для сохранения результатов тестирования
    private String FORMAT_RESULT = "JSON";                      // формат файла с результатами тестирования XML или JSON
    public static Level LOGGER_LEVEL = Level.WARN;              // уровень логирования

    private String theme;                                       // текущая тема
    private int maxQuestion;                                    // максимальное количество задаваемых вопросов с учетом имеющихся по теме
    private int questionNum;                                    // номер текущего вопроса
    private List<String> themesList = new ArrayList<>();        // список тем
    private List<QuestionJson> questionsJsonList;               // полный список вопросов (все темы)
    private List<Question> questionsList = new ArrayList<>();   // список текущих вопросов

    private String user = System.getProperty("user.name");      // текущий пользователь
    private long startTesting;                                  // время начала теста

    private ReadQuestions readQuestions; // читаем вопросы из файла (XML/JSON задается в properties (FILE_QUESTIONS расширение))
    private SaveResult saveResult;       // запись результатов тестирования в файл (XML/JSON задается в properties (FORMAT_RESULT))


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

        if (questionsJsonList != null) questionsJsonList.clear();
        if (themesList != null) themesList.clear();

        getProperties(fileProperties); // Читаем параметры из файла fileProperties

        // тип файла с вопросами
        if (FILE_QUESTIONS.toUpperCase().endsWith(".XML")) {
            readQuestions = new ReadQuestionsXml();
        } else if (FILE_QUESTIONS.toUpperCase().endsWith(".JSON")) {
            readQuestions = new ReadQuestionsJson();
        } else{
            readQuestions = null;
        }

        if (readQuestions != null) {

//        LOG.info( "\r\nПуть к файлу CheckingSkills.properties :\t" + fileProperties +
//                  "\r\nПуть к файлу с вопросами :\t\t\t" + FILE_QUESTIONS);

            // список вопросов (все темы)
            questionsJsonList = new ArrayList<>(readQuestions.read(FILE_QUESTIONS));

            // список тем
            if (questionsJsonList != null && !questionsJsonList.isEmpty()) {
                themesList = new ArrayList<>(
                        questionsJsonList
                                .stream()
                                .map(QuestionJson::getTheme)
                                .sorted()
                                .distinct()
                                .collect(Collectors.toList()));

//                saveQuestionsGroupByThemes("Cp1251"); // сохраним вопросы с правильными вариантами ответов в файлы (по темам)
            } else {
                LOG.error("Ошибка при чтении вопросов из файла");
            }
        } else{
            LOG.error("В файле с параметрами " + fileProperties + " - указан не верный формат файла с вопросами (допустимы форматы JSON или XML)");
        }
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
        return (int) questionsJsonList
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
     * Вопрос текущий
     *
     * @return
     */
    public Question get() { return questionsList.get(questionNum); }

    /**
     * Текущий номер вопроса
     *
     * @return
     */
    public int getQuestionNum() {
        return questionNum;
    }

    /**
     * Переход к предыдущему вопросу
     */
    public void prevQuestion() {
        if (questionNum > 0) questionNum--;
    }

    /**
     * Переход к следующему вопросу
     */
    public void nextQuestion() {
        if (questionNum < maxQuestion - 1) questionNum++;
    }

    /**
     * Первый вопрос в списке?
     *
     * @return
     */
    public boolean isFirstQuestion() {
        return questionNum == 0;
    }

    /**
     * Последний вопрос в списке ?
     *
     * @return
     */
    public boolean isLastQuestion() {
        return questionNum == (maxQuestion - 1);
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
        return (int) questionsList
                    .stream()
                    .filter(q -> !q.isAnswerCorrect())
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
                this.FILE_QUESTIONS = pr.getProperty("FILE_QUESTIONS", "questions\\Questions.json");
                this.PATH_RESULT = pr.getProperty("PATH_RESULT", "Result\\");
                this.FORMAT_RESULT = pr.getProperty("FORMAT_RESULT", "XML");
                this.LOGGER_LEVEL = Level.getLevel(pr.getProperty("LOGGER_LEVEL", "WARN"));
//                this.LOGGER_LEVEL = pr.getProperty("LOGGER_LEVEL", "WARN");

                Configurator.setLevel(LOG.getName(), LOGGER_LEVEL);

                LOG.info("\r\nФайл с параметрами " + fileName +
                        "\r\nПараметры:"+
                        "\r\nМакимальное количество задаваемых вопросов : " + MAX_QUESTION_CONST +
                        "\r\nОтображать подсказки : " + VISIBLE_ANSWERS +
                        "\r\nФайл с вопросами : " + FILE_QUESTIONS +
                        "\r\nПуть для сохранения результатов тестирования : " + PATH_RESULT +
                        "\r\nФормат файла с результатами тестирования XML или JSON : " + FORMAT_RESULT +
                        "\r\nУровень логирования : " + LOGGER_LEVEL
                );

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else{
            Configurator.setLevel(LOG.getName(), LOGGER_LEVEL);

            LOG.warn("\r\nНе найден файл с параметрами " + fileName +
            "\r\nПараметры по умолчанию:"+
            "\r\nМакимальное количество задаваемых вопросов : " + MAX_QUESTION_CONST +
            "\r\nОтображать подсказки : " + VISIBLE_ANSWERS +
            "\r\nФайл с вопросами : " + FILE_QUESTIONS +
            "\r\nПуть для сохранения результатов тестирования : " + PATH_RESULT +
            "\r\nФормат файла с результатами тестирования XML или JSON : " + FORMAT_RESULT +
            "\r\nУровень логирования : " + LOGGER_LEVEL
            );
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
                        BufferedWriter bw = new BufferedWriter(
                                                new OutputStreamWriter(
                                                    new FileOutputStream(t.replace("\\", "_") + ".txt", false),
                                                    charset != null & charset.length() > 0 ? charset : "Cp1251"));
                    ) {
                        sbQuestions.setLength(0);
                        sbQuestions
                                .append("##################################################\r\n")
                                .append(t)
                                .append("\r\n##################################################\r\n");

                        questionsJsonList // список вопросов по теме
                                .stream()
                                .filter(q -> q.getTheme().equals(t))
                                .forEach(q -> {
                                    sbQuestions
                                            .append("==================================================\r\n")
                                            .append(q.getQuestion())
                                            .append("\r\n");
                                    for (String a : q.getAnswersTrue()) {
                                        sbQuestions
                                                .append("..................................................\r\n")
                                                .append(a)
                                                .append("\r\n");
                                    }
                                });
                        bw.write(sbQuestions.toString());
                        bw.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
    }

    /**
     * Начинаем тестирование
     */
    public void start() {

        questionNum = 0;
        questionsList.clear();
        List<Answer> answersList = new ArrayList<>();

        // выберем maxQuestion случайных вопросов по текущей теме
        Random random = new Random();
        IntStream
            .generate(() -> random.nextInt(questionsJsonList.size()))
            .distinct()
            .filter(n -> questionsJsonList.get(n).getTheme().equals(theme))
            .limit(maxQuestion)
            .forEach(q -> {
                for (String a : questionsJsonList.get(q).getAnswersTrue()){
                    answersList.add(new Answer(a, true, false));
                }
                for (String a : questionsJsonList.get(q).getAnswersFalse()){
                    answersList.add(new Answer(a, false, false));
                }
                questionsList.add(
                        new Question(
                                questionsJsonList.get(q).getAuthor(),
                                questionsJsonList.get(q).getTheme(),
                                questionsJsonList.get(q).getQuestion(),
                                answersList));

                answersList.clear();
            });

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

        // результат тестирования в лог-файл
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        final String[] questionsError = {""};
        questionsList
                .stream()
                .filter(q -> !q.isAnswerCorrect())
                .map(q -> q.getQuestion())
                .forEach(q -> questionsError[0] = questionsError[0] + q +"\r\n");

        LOG.info("\r\nТестирование завершено:\r\n" +
                user + "\r\n" +
                dateFormat.format(startTesting) + "\r\n" +
                dateFormat.format(System.currentTimeMillis()) + "\r\n" +
                resultTXT + "\r\n" +
                questionsError[0]
        );

        // сохраняем результат тестирования
        String fileResultName = "";
        switch (FORMAT_RESULT.toUpperCase()){
            case "XML":
                fileResultName = "result.xml";
                saveResult = new SaveResultXml();
                break;

            case "JSON":
                fileResultName = "result.json";
                saveResult = new SaveResultJson();
                break;

            default:
                saveResult = null;
        }

        if (saveResult != null) {
            saveResult.save(
                    PATH_RESULT,
                    fileResultName,
                    user,
                    startTesting,
                    System.currentTimeMillis(),
                    getTheme(),
                    resultTXT);
        } else {
            LOG.warn("Результат тестирования не сохранен !");
        }
        return message;
    }

}