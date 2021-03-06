package ru.checkingSkills;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 * @author Белов Сергей
 * обработчик событий на форме
 */
public class CheckingSkillsListener implements ActionListener {

    Object[] options = { "Да", "Нет" };
    CheckingSkills parent; // ссылка на checkingSkills

    // Конструктор сохраняет ссылку на окно checkingSkills в переменной класса “parent”
    CheckingSkillsListener(CheckingSkills parent){
        this.parent = parent;
    }

    public void actionPerformed(ActionEvent evt) {

/*
        JRadioButton selRB = null;
        JCheckBox selCB = null;
        JButton clButton = null
*/

        // Получаем источник события
        Object eventSource = evt.getSource();

/*
        if (eventSource instanceof JButton) {
            clButton = (JButton) eventSource;
        }
        if (eventSource instanceof JRadioButton) {
            selRB = (JRadioButton) eventSource;
        }
        if (eventSource instanceof JCheckBox) {
            selCB = (JCheckBox) eventSource;
        }
*/

        if (eventSource == parent.bExit) { // выход из программы
            if (JOptionPane.showOptionDialog(parent.mainFrame,
                    "Уверены, что хотите выйти?",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]) == 0) {

                System.exit(0);
            }

        } else if (eventSource == parent.bNew) { // начать заново
            if (JOptionPane.showOptionDialog(parent.mainFrame,
                    "Начинаем заново?",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]) == 0){

                parent.start();
            }

        } else if (eventSource == parent.bEnd) { // завершить тестирование
            parent.rememberStatus(false); // запомним выбор

            if (JOptionPane.showOptionDialog(parent.mainFrame,
                    "<html>Внимание!<br/>Результаты будут сохранены.<br/>Завершаем тестирование?</html>",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]) == 0){

                parent.stop();
            }

        } else if (eventSource == parent.bPrevQuestion || eventSource == parent.bNextQuestion) { // следующий/предыдущий вопрос
            parent.rememberStatus(true); // запомним текущий выбор

            if (eventSource == parent.bPrevQuestion) {
                parent.questions.prevQuestion();
            } else if (eventSource == parent.bNextQuestion) {
                parent.questions.nextQuestion();
            }
            parent.refreshQuestion();

        } else if (eventSource == parent.cbTheme){ // сменили тему
//            parent.questions.setTheme((String) parent.cbTheme.getSelectedItem());
            parent.questions.stop(false);
            parent.start();
        }
    }
}

