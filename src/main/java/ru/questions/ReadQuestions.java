package ru.questions;

import java.util.List;

/**
 * @author Белов Сергей
 * Читаем вопросы из файла
 */
public interface ReadQuestions {
    List<Question> read(String fileName);
}
