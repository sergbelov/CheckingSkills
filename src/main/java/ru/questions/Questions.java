package ru.questions;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

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

    // CheckingSkills.properties
    private int     MAX_QUESTION = 10;                          // максимальное количество задаваемых вопросов
    private String  FILE_QUESTIONS = "questions\\Questions.json";// файл с вопросами
    private String  PATH_RESULT = "result\\";                   // путь для сохранения результатов тестирования
    private String  FORMAT_RESULT = "JSON";                     // формат файла с результатами тестирования XML или JSON
    private Level   LOGGER_LEVEL = Level.WARN;                  // уровень логирования

    private String theme;                                       // текущая тема
    private int maxQuestion;                                    // максимальное количество задаваемых вопросов с учетом имеющихся по теме
    private int questionNum;                                    // номер текущего вопроса
    private List<String> themesList = new ArrayList<>();        // список тем
    private List<QuestionJson> questionsJsonList;               // полный список вопросов (все темы)
    private List<Question> questionsList = new ArrayList<>();   // список текущих вопросов

    private String user = System.getProperty("user.name");      // текущий пользователь
    private long startingTime = 0;                              // время начала теста

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
     * Устанавливаем параметры, читаем вопросы из файла FILE_QUESTIONS
     *
     * @param MAX_QUESTION          // максимальное количество задаваемых вопросов
     * @param FILE_QUESTIONS        // файл с вопросами
     * @param PATH_RESULT           // путь для сохранения результатов тестирования
     * @param FORMAT_RESULT         // формат файла с результатами тестирования XML или JSON
     * @param LOGGER_LEVEL          // уровень логирования
     */
    public void readQuestions(
            int     MAX_QUESTION,
            String  FILE_QUESTIONS,
            String  PATH_RESULT,
            String  FORMAT_RESULT,
            Level   LOGGER_LEVEL) {

        this.MAX_QUESTION = MAX_QUESTION;
        this.FILE_QUESTIONS = FILE_QUESTIONS;
        this.PATH_RESULT = PATH_RESULT;
        this.FORMAT_RESULT = FORMAT_RESULT;
        this.LOGGER_LEVEL = LOGGER_LEVEL;

        Configurator.setLevel(LOG.getName(), LOGGER_LEVEL);

        if (questionsJsonList != null) questionsJsonList.clear();
        if (themesList != null) themesList.clear();

        // тип файла с вопросами
        if (FILE_QUESTIONS.toUpperCase().endsWith(".XML")) {
            readQuestions = new ReadQuestionsXml();
        } else if (FILE_QUESTIONS.toUpperCase().endsWith(".JSON")) {
            readQuestions = new ReadQuestionsJson();
        } else{
            readQuestions = null;
        }

        if (readQuestions != null) {
            // список вопросов (все темы)
            questionsJsonList = new ArrayList<>(readQuestions.read(FILE_QUESTIONS, LOGGER_LEVEL));

            // список тем
            if (questionsJsonList != null && !questionsJsonList.isEmpty()) {
                themesList = new ArrayList<>(
                        questionsJsonList
                                .stream()
                                .map(QuestionJson::getTheme)
                                .sorted()
                                .distinct()
                                .collect(Collectors.toList()));

                StringBuilder themes = new StringBuilder();
                themesList
                        .stream()
                        .forEach(x -> themes.append("\r\n").append(x));
                LOG.debug("Имеются вопросы по следующим темам:{}", themes.toString());

//                saveQuestionsGroupByThemes(PATH_RESULT, "Cp1251"); // сохраним вопросы с правильными вариантами ответов в файлы (по темам)
            } else {
                LOG.error("Ошибка при чтении вопросов из файла {}", FILE_QUESTIONS);
            }
        } else{
            LOG.error("Файл с вопросами имеет недопустимый формат (возможны JSON или XML)");
        }
    }

    /**
     * Задаем текущую тему
     *      Максимально количество задаваемых вопросов
     *      (равно количеству вопросов по теме, но не более MAX_QUESTION)
     *
     * @param theme
     */
    public void setTheme(String theme) {
        this.theme = theme;
        maxQuestion = Math.min(getCountQuestionsInTheme(theme), MAX_QUESTION);
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
     * Список текущих вопросов
     *
     * @return
     */
    public List<Question> getQuestionsList() { return questionsList; }


    /**
     * Вопрос текущий
     *
     * @return
     */
    public Question get() { return questionsList.get(questionNum); }

    /**
     * Вопрос по номеру
     *
     * @param q
     * @return
     */
    public Question get(int q) { return questionsList.get(q); }

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
     * Сохраним вопросы с правильными ответами (сгруппировав по темам)
     */
    public void saveQuestionsGroupByThemes(final String path, final String charset) {
//        StandardCharsets.US_ASCII.name();
        StringBuilder sbQuestions = new StringBuilder();

        themesList // список тем
                .stream()
                .sorted()
                .forEach(t -> {

                    try (
                        BufferedWriter bw = new BufferedWriter(
                                                new OutputStreamWriter(
                                                    new FileOutputStream(path +  File.separator + t.replace("\\", "_") + ".txt", false),
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
                        LOG.error("IOException", e);
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

        startingTime = System.currentTimeMillis();  // время старта

        LOG.info("Пользователь {} начал тестирование по теме {}",
                user,
                theme);
    }

    /**
     * Заканчиваем тестирование
     * Сохраним результат тестирования
     *
     * @return
     */
    public String stop() {
        if (startingTime > 0) {
            String message, resultTXT;
            int countError = getCountNotCorrectAnswers();
            int correctAnswer = getMaxQuestion() - countError;
            int correctAnswerProc = correctAnswer * 100 / getMaxQuestion();

            if (countError == 0) { // ошибок нет
                message = "<html>Отличная работа!<br/>Примите поздравления!</html>";
                resultTXT = "Отлично!!! (" + correctAnswer + " из " + getMaxQuestion() + ")";

            } else { // ошибки есть
                message = "<html>Имеются ошибки.<br/>" +
                        "Дан верный ответ на " + correctAnswer +
                        " из " + getMaxQuestion() +
                        " ( " + correctAnswerProc + "% )<br/><br/>" +
                        "Анализ ошибок:<br/>" +
                        "<font color=\"#10aa10\">Правильный выбор;</font> <br/>" +
                        "<font color=\"#ffb000\">Нужно было выбрать;</font><br/>" +
                        "<font color=\"#ff10010\">Неправильный выбор.</font><br/></html>";
                resultTXT = "(" + correctAnswer + " из " + getMaxQuestion() + ") " + correctAnswerProc + "%";
            }

            // результат тестирования в лог-файл
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            StringBuilder wrongAnswers = new StringBuilder();
            List<String> wrongAnswersList = new ArrayList<>();
            questionsList
                    .stream()
                    .filter(q -> !q.isAnswerCorrect())
                    .map(q -> q.getQuestion())
                    .forEach(q -> { wrongAnswersList.add(q);
                                    wrongAnswers.append("\r\n").append(q);});

            LOG.info("Пользователь {} завершил тестирование по теме {}; Результат: {}{}",
                    user,
                    theme,
                    resultTXT,
                    wrongAnswers);

            // сохраняем результат тестирования
            String fileResultName = "";
            switch (FORMAT_RESULT.toUpperCase()) {
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
                        startingTime,
                        System.currentTimeMillis(),
                        getTheme(),
                        resultTXT,
                        wrongAnswersList);
            } else {
                LOG.warn("Указан недопустимый формат для сохранения результатов тестирования (возможны JSON или XML); Результат тестирования не сохранен!");
            }

            startingTime = 0;
            return message;
        } else {
            return "";
        }
    }

    public boolean isStarted() { return startingTime > 0 ? true : false; }
}