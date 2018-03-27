package ru;

/**
 * @author Белов Сергей
 * Вариант ответа на вопрос
 */
public class Answer {
    private String answer;      // ответ
    private boolean correct;    // признак корректности
    private boolean selected;   // выбран в контроле

    public Answer(String answer, boolean correct, boolean selected) {
        this.answer = answer;
        this.correct = correct;
        this.selected = selected;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isCorrect() {
        return correct;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
