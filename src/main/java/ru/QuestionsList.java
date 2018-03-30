package ru;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Белов Сергей
 * Список всех вопросов
 */
public class QuestionsList {

    private List<String> themesList;      // полный список тем
    private List<Question> questionsList; // полный список вопросов (все темы)

    // CheckingSkills.parameters
    private int MAX_QUESTION_CONST = 10; // макимальное количество задаваемых вопросов
    private boolean VISIBLE_ANSWERS = false; // отображать подсказки
    private String FILE_QUESTIONS = "questions\\XMLDataTest.xml";
    private String PATH_RESULT = "result\\";

    private int maxQuestion;            // максимальное количество задаваемых вопросов с учетом имеющихся по теме
    private int curThemeNum;            // номер текущей темы
    private int curQuestion;            // номер текущего вопроса (порядковый на форме)
    private int[] curQuestionsNumList;  // случайная последовательность номеров по теме

    private ReadQuestions readQuestions = new ReadQuestions(); // читаем вопросы из XML-файла
    private SaveResult saveResult = new SaveResult(); // запись результатов тестирования в XML-файл


    public QuestionsList() {
        getParameters("CheckingSkills.parameters"); // читаем параметры из файла
    }

    /**
     * Читаем вопросы из файла
     */
    public void readQuestions() {

        questionsList = new ArrayList<>(
                readQuestions.read(FILE_QUESTIONS));

        themesList = new ArrayList<>(
                questionsList
                        .stream()
                        .map(Question::getTheme)
                        .sorted()
                        .distinct()
                        .collect(Collectors.toList()));

        saveQuestionsGroupByThemes("Cp1251"); // сохраним вопросы с правильными вариантами ответов в файлы (по темам)

//        Collections.sort(themesList);
    }

    /**
     * Максимальное количество задаваемых вопросов с учетом имеющихся по теме
     *
     * @return
     */
    public int getMaxQuestion() {
        return maxQuestion;
    }

    /**
     * Размер полного списка (все темы)
     *
     * @return
     */
    public int size() {
        return questionsList.size();
    }

    /**
     * Тема по номеру
     *
     * @param themeNum
     * @return
     */
    public String getTheme(int themeNum) {
        return themesList.get(themeNum);
    }

    /**
     * Текущая тема
     *
     * @return
     */
    public String getCurTheme() {
        return themesList.get(curThemeNum);
    }

    /**
     * Задаем текущую тему
     *
     * @param themeNum
     */
    public void setCurTheme(int themeNum) {
        this.curThemeNum = themeNum;
    }

    /**
     * Количество вопросов по теме
     *
     * @param themeNum
     * @return
     */
    public int getCountThemeQuestions(int themeNum) {
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
    public int getCountThemeQuestions(String theme) {
        return (int) questionsList
                .stream()
                .filter((x) -> x.getTheme().equalsIgnoreCase(theme))
                .count();
    }

    /**
     * Сисок тем
     *
     * @return
     */
    public List<String> getThemesList() {
        return themesList;
    }

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
     * Генерим случайную последовательность номеров по теме
     *
     * @return
     */
    public void getQuestionsListNum() {

        curQuestion = 0;

        // сбрасываем текущий выбор
        if (curQuestionsNumList != null) {
/*
            Arrays
                .stream(curQuestionsNumList)
                .forEach(n -> {
                    questionsList.get(n).clearAnswersSelect();
                    questionsList.get(n).answersListShuffle(); // перемешаем варианты ответов
                });
*/
            for (int i = 0; i < curQuestionsNumList.length; i++) {
                questionsList.get(curQuestionsNumList[i]).clearAnswersSelect();
                questionsList.get(curQuestionsNumList[i]).answersListShuffle(); // перемешаем варианты ответов
            }
        }

        // максимально количество задаваемых вопросов (равно количеству вопросов по теме, но не более maxQuestionConst)
        maxQuestion = Math.min(getCountThemeQuestions(curThemeNum), MAX_QUESTION_CONST);

        // выберем maxQuestion случайных вопросов из текущей темы
        Random random = new Random();
        curQuestionsNumList = IntStream
                .generate(() -> random.nextInt(questionsList.size()))
                .distinct()
                .filter(n -> questionsList.get(n).getTheme().equals(getTheme(curThemeNum)))
                .limit(maxQuestion)
                .toArray();
    }

    /**
     * Текущий номер вопроса на форме
     *
     * @return
     */
    public int getCurQuestion() {
        return curQuestion;
    }

    /**
     * Текущий номер вопроса в общем списке
     *
     * @return
     */
    public int getCurQuestionNum() {
        return curQuestionsNumList[curQuestion];
    }

    /**
     * Переход к предыдущему вопросу
     */
    public void PrevQuestion() {
        if (curQuestion > 0) curQuestion--;
    }

    /**
     * Переход к следующему вопросу
     */
    public void NextQuestion() {
        if (curQuestion < maxQuestion - 1) curQuestion++;
    }

    /**
     * Первый вопрос в списке?
     *
     * @return
     */
    public boolean isFirstQuestion() {
        return curQuestion == 0;
    }

    /**
     * Последний вопрос в списке?
     *
     * @return
     */
    public boolean isLastQuestion() {
        return curQuestion == (maxQuestion - 1);
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

        int countError = 0;

        for (int i = 0; i < curQuestionsNumList.length; i++) {
            if (!questionsList.get(curQuestionsNumList[i]).isAnswerCorrect()) {
                countError++;
            }
        }
        return countError;
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
                Properties pr = new Properties();
                pr.load(is);

                this.MAX_QUESTION_CONST = (int) Integer.parseInt(pr.getProperty("MAX_QUESTION_CONST", "10"));
                this.VISIBLE_ANSWERS = (boolean) Boolean.parseBoolean(pr.getProperty("VISIBLE_ANSWER", "FALSE"));
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

    public void saveResultTest(long startTesting, String resultTXT) {
        saveResult.save(
                PATH_RESULT,
//                System.getProperty("user.name") + ".xml", // "result.xml"
                "result.xml",
                startTesting,
                System.currentTimeMillis(),
                getCurTheme(),
                resultTXT);
    }

}