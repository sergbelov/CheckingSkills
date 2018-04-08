package ru.questions;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Белов Сергей
 * Список всех вопросов
 */
public class Questions {

    private List<String> themesList;      // полный список тем
    private List<Question> questionsList; // полный список вопросов (все темы)

    // CheckingSkills.properties
    private int MAX_QUESTION_CONST = 10; // макимальное количество задаваемых вопросов
    private boolean VISIBLE_ANSWERS = false; // отображать подсказки
    private String FILE_QUESTIONS = "questions\\XMLDataTest.xml";
    private String PATH_RESULT = "result\\";

    private String theme;               // текущая тема
    private int themeNum;               // номер текущей темы
    private int maxQuestion;            // максимальное количество задаваемых вопросов с учетом имеющихся по теме
    private int questionNumOnForm;      // номер текущего вопроса (порядковый на форме)
    private int[] randomQuestionsArr;   // случайная последовательность номеров вопросов по теме
    private long startTesting;          // время начала теста

    private ReadQuestions readQuestions = new ReadQuestions(); // читаем вопросы из XML-файла
    private SaveResult saveResult = new SaveResult(); // запись результатов тестирования в XML-файл


    /**
     * Инициализация (читаем параметры из файла fileProperties)
     *
     * @param fileProperties
     */
    public Questions(String fileProperties) {
        getProperties(fileProperties);
    }

    /**
     * Читаем вопросы из файла FILE_QUESTIONS
     */
    public void readQuestions() {

        questionsList = new ArrayList<>(readQuestions.read(FILE_QUESTIONS));

        // темы
        themesList = new ArrayList<>(
                questionsList
                        .stream()
                        .map(Question::getTheme)
                        .sorted()
                        .distinct()
                        .collect(Collectors.toList()));

        // сохраним вопросы с правильными вариантами ответов в файлы (по темам)
//        saveQuestionsGroupByThemes("Cp1251");

//        Collections.sort(themesList);
    }

    /**
     * Задаем текущую тему
     *
     * @param theme
     */
    public void setTheme(String theme) {
        this.theme = theme;
        this.themeNum = themesList.indexOf(theme);
        setMaxQuestion();
    }

    /**
     * Задаем текущую тему (номер)
     *
     * @param themeNum
     */
    public void setThemeByNum(int themeNum) {
        this.theme = getTheme(themeNum);
        this.themeNum = themeNum;
        setMaxQuestion();
    }

    /**
     * Тема по номеру
     *
     * @param themeNum
     * @return
     */
    public String getTheme(int themeNum) { return themesList.get(themeNum); }

    /**
     * Текущая тема
     *
     * @return
     */
    public String getCurTheme() {
//        return themesList.get(themeNum);
        return theme;
    }

    /**
     * Количество вопросов по теме
     *
     * @param themeNum
     * @return
     */
    public int getCountQuestionsInTheme(int themeNum) {
        return (int) questionsList
                .stream()
                .filter((x) -> x.getTheme().equals(themesList.get(themeNum)))
                .count();
    }

    /**
     * Количество вопросов по теме
     *
     * @param theme
     * @return
     */
    public int getCountQuestionsInTheme(String theme) {
        return (int) questionsList
                .stream()
                .filter((x) -> x.getTheme().equalsIgnoreCase(theme))
                .count();
    }

    /**
     * Максимально количество задаваемых вопросов
     * (равно количеству вопросов по теме, но не более MAX_QUESTION_CONST)
     */
    private void setMaxQuestion(){
        maxQuestion = Math.min(getCountQuestionsInTheme(themeNum), MAX_QUESTION_CONST);
    }

    /**
     * Максимальное количество задаваемых вопросов с учетом имеющихся по теме
     *
     * @return
     */
    public int getMaxQuestion() { return maxQuestion; }

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
    public Question get(int questionNum) {
        return questionsList.get(questionNum);
    }
    /**
     * Вопрос текущий
     * @return
     */
    public Question getCur() {
        return questionsList.get(getCurQuestionNum());
    }

    /**
     * Генерим случайную последовательность номеров по текущей теме
     *
     * @return
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
    public void PrevQuestion() {
        if (questionNumOnForm > 0) questionNumOnForm--;
    }

    /**
     * Переход к следующему вопросу
     */
    public void NextQuestion() {
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
     * Последний вопрос в списке?
     *
     * @return
     */
    public boolean isLastQuestion() {
        return questionNumOnForm == (maxQuestion - 1);
    }

    /**
     * Отображать подсказки
     */
    public boolean isVisibleAnswers(){
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
                                .filter(q -> q.getTheme().equalsIgnoreCase(t))
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
     * Сохраним результат тестирования
     *
     * @param resultTXT
     */
    public void saveResult(String resultTXT) {
        saveResult.save(
                PATH_RESULT,
//                System.getProperty("user.name") + ".xml",
                "result.xml",
                startTesting,
                System.currentTimeMillis(),
                getCurTheme(),
                resultTXT);
    }

}