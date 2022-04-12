/*
 1. Через intent получать message_key со сложностью игры
 2. По сложности игры выбирать операции, которые будут
      2.1. easy - +/- (+-200), medium - * /÷ (+-500), hard - +/-/* /÷ (+-1000)
 3. Генерировать пример со случайными числами и знаком из данных
      3.1. Если операция - умножение, то проверять, не превосходит ли произведение пределов уровня
          3.1.1. Если да, то вернуться к пункту 3
          3.1.2. Если нет, то перейти к пункту 4
      3.2. Если операция - деление, то проверять, целое ли решение
          3.2.1. Если да, то перейти к пункту 4
          3.2.2. Если нет, то вернуться к пункту 3
 4. Записать в переменные answer (ответ на сгенерированную задачу) и answerDigits (количество символов в ответе (включая знак минус))
 5. Первый ли это сгенерированный пример?
      4.1. Если да, то запустить таймер на 60 секунд и перейти к пункту 6
      4.2. Если нет, то перейти к пункту 6
 6. Проверять, сколько символов ввёл пользователь в поле response
      6.1. responseDigits == answerDigits
          6.1.1. Если да, то записать введённое значение в переменную responseAnswer и перейти к пункту 7
          6.1.2. Если нет, то вернуться к пункту 6
 7. Проверять введённый пользователем ответ
      7.1. responseAnswer == answer
          7.1.1. Если да, то очищать поле ввода, прибавлять к переменной score +1 и вернуться к пункту 3
          7.1.2. Если нет, то очищать поле ввода и вернуться к пункту 6
 8. Проверять таймер
      8.1. Таймер не закончился?
          8.1.1. Если да, то вернуться к пункту 3
          8.1.2. Если нет, то перейти к пункту 9
      8.2. Таймер не начинался? (дебаг, запрашивать через каждый пункт 3)
          8.2.1. Если да, то перейти к пункту 11
          8.2.2. Если нет, то вернуться к пункту 3
 9. Выводить окно с результатом (score)
 10. Если пользователь нажмёт на кнопку повтора, то перейти к пункту 11
 11. Вернуться к activity_main и очистить переменную score
 */
package com.example.mathquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.Random;

public class QuizTime extends AppCompatActivity {
    String operation = "non";
    public char SelectedOperation = 'n';
    public TextView equation;
    public TextView timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_time);

        Intent intent = getIntent();
        String difficulty = intent.getStringExtra("message_key");

        operationSelect(difficulty); //Выбор операций по сложности
        selectAChar(operation); //Случайная операция из данных
        equation = findViewById(R.id.equation);
        if (equation.equals("2+2 =")) {
            new CountDownTimer(60000, 1000) {

                public void onTick(long millisUntilFinished) {
                    timer.setText((int)(millisUntilFinished / 1000));
                }
                public void onFinish() {
                    timer.setText("done!");
                }
            }.start();
        }
    }
    public String operationSelect (String difficulty) {
        if (difficulty == "easy") {
            operation = "+-";
        }
        else if (difficulty == "medium") {
            operation = "*/";
        }
        else if (difficulty == "hard") {
            operation = "+-*/";
        }
        return operation;
    }
    public static char selectAChar(String operation){
        Random random = new Random();
        int index = random.nextInt(operation.length());
        selectedOperation = operation.charAt(index);
        return selectedOperation;
    }
}