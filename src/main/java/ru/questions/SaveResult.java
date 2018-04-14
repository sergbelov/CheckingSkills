package ru.questions;

/**
 * @author Белов Сергей
 * Сохранение результата тестирования в файл
 */

public interface SaveResult {

    /**
     * Сохранение результата тестирования в файл
     * @param path          - путь для сохранения
     * @param fileName      - имя файла
     * @param user          - пользователь
     * @param startTesting  - время начала теста
     * @param stopTesting   - время окончания теста
     * @param theme         - тема
     * @param resultTXT     - результат тестирования
     */
    void save(  String path,
                String fileName,
                String user,
                long startTesting,
                long stopTesting,
                String theme,
                String resultTXT);
}
