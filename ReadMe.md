Программа для тестирования знаний

Вопросы с ответами хранятся в xml-файле, формат:
<Questions>
	<QuestionBlock>
		<author>Иванов Иван Иванович 29/06/2016</author>
		<theme> Тема теста </theme>
		<question> Вопрос</question>
		<answer_true>
			<at>Верный вариант 1 </at>
			<at>Верный вариант 2 </at>
		</answer_true>
		<answers_false>
			<af>Ложный вариант 1</af>
			<af>Ложный вариант 2 </af>
		</answers_false>
	</QuestionBlock>
</Questions>

Если верный вариант один, в опроснике появляется компонент с единственным вариантом ответа (RadioButton), если верных вариантов несколько, отобразятся CheckBox.

По окончании тестирования имеется возможность проанализировать ошибочные ответы, если таковые были.
Результаты тестирования сохраняются в xml-файл, формат:
<TestResults>
    <TestResult>
        <Theme>Тема 1</Theme>
        <StartTesting>06/06/2017 13:01:51</StartTesting>
        <StopTesting>06/06/2017 13:02:40</StopTesting>
        <ResultTXT>Отлично!!! (5 из 5) 100%</ResultTXT>
    </TestResult>
    <TestResult>
        <Theme>Тема 2</Theme>
        <StartTesting>16/06/2017 13:55:26</StartTesting>
        <StopTesting>16/06/2017 13:59:02</StopTesting>
        <ResultTXT> Отлично!!!  (10 из 10) 100%</ResultTXT>
    </TestResult>
</TestResults>
