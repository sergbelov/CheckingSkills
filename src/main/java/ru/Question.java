package ru;

import java.util.*;

/**
 * @author Белов Сергей
 * Вопрос, включает в себя:
 * String theme             тема;
 * String question          вопрос;
 * Integer type             тип (1-RadioButton; 2-CheckBox);
 * List<Answer> answersList варианты ответов
 */
public class Question implements Comparable<Question> {

    private Integer type;               // тип ответа 1-RadioButton; 2-CheckBox
    private String theme;               // тема
    private String question;            // вопрос
    private List<Answer> answersList;   // список вариантов ответа


    public Question(String themeParam, String questionParam, List<Answer> answersListParam) {
        this.theme = themeParam;
        this.question = questionParam;
        this.answersList = new ArrayList<>(answersListParam);

        // если правильных ответов более одного - CheckBox иначе RadioButton
        this.type = (int) answersList.stream().filter((x) -> x.isCorrect()).count() > 1 ? 2 : 1;

        // перемешаем варианты ответов
        this.answersListShuffle();
    }

    public int getType() {
        return type;
    }

    public String getTheme() {
        return theme;
    }

    public String getQuestion() {
        return question;
    }

    public int getCountAnswers() {
        return answersList.size();
    }

    public Answer getAnswer(int i) {
        return answersList.get(i);
    }


    /**
     * Ответ правильный
     * @return
     */
    public boolean isAnswerCorrect(){
        return answersList
                .stream()
                .filter((x) -> x.isCorrect()!=x.isSelected())
                .count() == 0;
    }

    /**
     * Сбрасываем текущий выбор
     */
    public void clearAnswersSelected() {
        answersList
                .stream()
                .filter((x) -> x.isSelected())
                .forEach((x) -> x.setSelected(false));
/*
        for (int i = 0; i < getCountAnswers(); i++) {
            getAnswer(i).setSelected(false);
        }
*/
    }

    /**
     * Перемешаем варианты ответов
    */
    public void answersListShuffle() {
        Collections.shuffle(answersList);
    }


    @Override
    public int compareTo(Question question) {
        return this.getTheme().compareTo(question.getTheme()) |
                this.getQuestion().compareTo(question.getQuestion());
    }
}
