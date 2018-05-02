package ru.questions;

import java.util.List;
import org.apache.logging.log4j.Level;

/**
 * @author Белов Сергей
 * Читаем вопросы из файла
 */
public interface ReadQuestions {
    List<QuestionJson> read(String fileName, Level level);
}
