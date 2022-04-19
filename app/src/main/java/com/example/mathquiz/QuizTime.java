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
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class QuizTime extends AppCompatActivity {
    public String operation = "non";
    public int numberOne = 0;
    public int numberTwo = 0;
    public String difficulty;
    public String OperationIsPossible = "no";
    public String SelectedOperation;
    public TextView equation;
    public TextView timer;
    public String Equation;
    public int answer;
    public int answerDigits;
    public int responsive;
    public int responseDigits;
    public EditText response;
    public int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_time);

        Intent intent = getIntent();
        difficulty = intent.getStringExtra("message_key");

        equation = findViewById(R.id.equation);
        if (equation.equals("2+2 =")) {
            Equation = generator(difficulty);
            equation.setText(Equation);
            new CountDownTimer(60000, 1000) {
                public void onTick(long millisUntilFinished) {
                    timer.setText((int)(millisUntilFinished / 1000));
                }
                public void onFinish() {
                    response.setVisibility(View.GONE); //убрать поле ввода
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(QuizTime.this);
                    alertDialog.setTitle(R.string.gameover+score);
                    alertDialog.setMessage(R.string.restartquestion);
                    alertDialog.setPositiveButton(R.string.restart, (dialog, which) -> startActivity(new Intent(QuizTime.this, MainActivity.class)));
                    alertDialog.setNegativeButton(R.string.exit, (dialog, which) -> {
                        QuizTime.this.finish();
                        System.exit(0);
                    });
                    alertDialog.show();//вывести окно с результатом и рестартом
                }
            }.start();
        }
        else {
            Equation = generator(difficulty);
            equation.setText(Equation);
        }
    }
    public String generator (String difficulty) {
        operationSelect(difficulty); //Выбор операций по сложности
        selectAChar(operation); //Случайная операция из данных
        randomNumbers(difficulty);
        isOperationPossible(SelectedOperation, numberOne, numberTwo, difficulty);

        if (OperationIsPossible == "yes") {
            if (SelectedOperation == "+") {
                answer = Math.addExact(numberOne, numberTwo);
            }
            else if (SelectedOperation == "-") {
                answer = Math.subtractExact(numberOne, numberTwo);
            }
            else if (SelectedOperation == "*") {
                answer = Math.multiplyExact(numberOne, numberTwo);
            }
            else if (SelectedOperation == "/") {
                answer = numberOne/numberTwo;
            }
        }
        else {
            generator(difficulty);
        }
        mergeEquation(SelectedOperation, numberOne, numberTwo);
        return Equation;
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
    public String selectAChar(String operation){
        Random random = new Random();
        int index = random.nextInt(operation.length());
        char krya = operation.charAt(index);
        String SelectedOperation = String.valueOf(krya);
        return SelectedOperation;
    }
    public void randomNumbers (String difficulty){
        if (difficulty == "easy") {
            numberOne = (int)(Math.random()*200-100); //От -100 до 100
            numberTwo = (int)(Math.random()*100); //От 0 до 100
        }
        if (difficulty == "medium") {
            numberOne = (int)(Math.random()*1000-500); //От -500 до 500
            numberTwo = (int)(Math.random()*500); //От 0 до 500
        }
        if (difficulty == "hard") {
            numberOne = (int)(Math.random()*2000-1000); //От -1000 до 1000
            numberTwo = (int)(Math.random()*1000); //От 0 до 1000
        }
    }
    public String isOperationPossible (String SelectedOperation, int numberOne, int numberTwo, String difficulty) {
        if (SelectedOperation == "*") {
            if (difficulty == "medium") {
                if (((numberOne * numberTwo) > 500) && ((numberOne * numberTwo) < -500)) {
                    OperationIsPossible = "no"; // numberOne * numberTwo = [-500; 500]
                }
                else OperationIsPossible = "yes";
            }
            if (difficulty == "hard") {
                if ((numberOne * numberTwo) > 1000 && ((numberOne * numberTwo) < -1000)) {
                    OperationIsPossible = "no"; // numberOne * numberTwo = [-1000; 1000]
                }
                else OperationIsPossible = "yes";
            }
        }
        if (SelectedOperation == "/") {
            try { // Проверка деления на ноль
                if ((numberOne % numberTwo) != 0) {
                    OperationIsPossible = "no";
                } else OperationIsPossible = "yes";
            } catch (ArithmeticException e){
                OperationIsPossible = "no"; // NumberTwo == 0
            }
        }
        return OperationIsPossible;
    }
    public String mergeEquation (String SelectedOperation, int numberOne, int numberTwo) {
        String Equation = numberOne+" "+SelectedOperation+" "+numberTwo+" =";
        return Equation;
    }
}