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
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Random;

public class QuizTime extends AppCompatActivity {
    String operation = "non";
    int numberOne = 0;
    int numberTwo = 0;
    String difficulty;
    String OperationIsPossible = "no";
    String SelectedOperation;
    TextView equation;
    TextView timer;
    String Equation;
    int answer;
    int answerDigits;
    int responsive;
    int responseDigits;
    EditText response;
    int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_quiz_time);

    Intent intent = getIntent();
    difficulty = intent.getStringExtra("message_key");
    }

    protected void onStart() {
        super.onStart();
        equation = findViewById(R.id.equation);
        timer = findViewById(R.id.timer);
        while (!timer.getText().toString().equals("0")) {
            if (equation.getText().toString().trim().equals("2+2=")) {
                Equation = generator(difficulty);
                equation.setText(Equation);
                new CountDownTimer(60000, 100) {
                    public void onTick(long millisUntilFinished) {
                        timer.setText((int) (millisUntilFinished / 1000));
                        EditText text = (EditText) (findViewById(R.id.response));
                        if (text.getText().toString().length() == responseDigits) {
                            if (text.getText().toString().equals(String.valueOf(answer))) {
                                switch (difficulty) {
                                    case "easy":
                                        score++;
                                    case "medium":
                                        score = score + 2;
                                    case "hard":
                                        score = score + 5;
                                }
                                Equation = generator(difficulty);
                                equation.setText(Equation);
                            } else {
                                response.getText().clear();
                            }
                        }
                    }

                    public void onFinish() {
                        response.setVisibility(View.GONE); //Убрать поле ввода
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(QuizTime.this);
                        alertDialog.setTitle(R.string.gameover + score);
                        alertDialog.setMessage(R.string.restartquestion);
                        alertDialog.setPositiveButton(R.string.restart, (dialog, which) -> startActivity(new Intent(QuizTime.this, MainActivity.class)));
                        alertDialog.setNegativeButton(R.string.exit, (dialog, which) -> {
                            QuizTime.this.finish();
                            System.exit(0);
                        });
                        alertDialog.show();//Вывести окно с результатом и рестартом
                    }
                }.start();
            } else {
                Equation = generator(difficulty);
                equation.setText(Equation);
            }
        }
    }
    public String generator (String difficulty) {
        operation = operationSelect(difficulty); //Выбор операций по сложности
        SelectedOperation = selectAChar(operation, difficulty); //Случайная операция из данных
        numberOne = randomNumberOne(difficulty, SelectedOperation);
        numberTwo = randomNumberTwo(difficulty, SelectedOperation);
        OperationIsPossible = isOperationPossible(SelectedOperation, numberOne, numberTwo, difficulty);

        if (OperationIsPossible.equals("yes")) {
            switch (SelectedOperation) {
                case "+":
                    answer = Math.addExact(numberOne, numberTwo);
                    break;
                case "-":
                    answer = Math.subtractExact(numberOne, numberTwo);
                    break;
                case "*":
                    answer = Math.multiplyExact(numberOne, numberTwo);
                    break;
                case "/":
                    answer = numberOne;
                    break;
            }
        }
        else if (OperationIsPossible.equals("no")) {
            generator(difficulty);
        }
        Equation = mergeEquation(SelectedOperation, numberOne, numberTwo);
        return Equation;
    }
    public String operationSelect (String difficulty) {
        switch (difficulty) {
            case "easy":
                operation = "+-";
                break;
            case "medium":
                operation = "*/";
                break;
            case "hard":
                operation = "+-*/";
                break;
        }
        return operation;
    }
    public String selectAChar(String operation, String difficulty){
        operationSelect(difficulty);
        Random random = new Random();
        int index = random.nextInt(operation.length());
        char krya = operation.charAt(index);
        SelectedOperation = String.valueOf(krya);
        return SelectedOperation;
    }
    public int randomNumberOne (String difficulty, String SelectedOperation) {
        switch (difficulty) {
            case "easy": numberOne = (int) (Math.random() * 200 - 100); break;//От -100 до 100
            case "medium":
                if (SelectedOperation.equals("*") || SelectedOperation.equals("/")) {
                    numberOne = (int) (Math.random()*70-35);
                }
                else {
                    numberOne = (int) (Math.random() * 1000 - 500); //От -500 до 500
                }
                break;
            case "hard":
                if (SelectedOperation.equals("*") || SelectedOperation.equals("/")) {
                    numberOne = (int) (Math.random()*100-50);
                }
            else {
                numberOne = (int) (Math.random() * 2000 - 1000); //От -1000 до 1000
            }
                break;
        }
        return numberOne;
    }
    public int randomNumberTwo (String difficulty, String SelectedOperation) {
        switch(difficulty) {
            case "easy": numberTwo = (int) (Math.random() * 100); break;//От 0 до 100
            case "medium":
                if (SelectedOperation.equals("*") || SelectedOperation.equals("/")) {
                    numberTwo = (int) (Math.random()*35);
                }
                else {
                    numberTwo = (int) (Math.random() * 500); //От 0 до 500
                }
                break;
            case "hard":
                if (SelectedOperation.equals("*") || SelectedOperation.equals("/")) {
                    numberTwo = (int) (Math.random()*50);
                }
                else {
                    numberTwo = (int) (Math.random() * 1000); //От 0 до 1000
                }
                break;
        }
        return numberTwo;
    }
    public String isOperationPossible (String SelectedOperation, int numberOne, int numberTwo, String difficulty) {
        switch (SelectedOperation) {
            case "*":
            case "/":
                if (numberOne == 0 || numberTwo == 0) {
                    OperationIsPossible = "no";
                }
                else {
                    if (difficulty.equals("medium")) {
                        if (((numberOne * numberTwo) < 500) && ((numberOne * numberTwo) > -500)) {
                            OperationIsPossible = "yes"; // numberOne * numberTwo = [-500; 500]
                        } else OperationIsPossible = "no";
                    }
                    if (difficulty.equals("hard")) {
                        if ((numberOne * numberTwo) < 1000 && ((numberOne * numberTwo) > -1000)) {
                            OperationIsPossible = "yes"; // numberOne * numberTwo = [-1000; 1000]
                        } else OperationIsPossible = "no";
                    }
                }
                break;
            case "-":
            case "+":
                OperationIsPossible = "yes";
                break;
        }
        return OperationIsPossible;
    }
    public String mergeEquation (String SelectedOperation, int numberOne, int numberTwo) {
        int numberOnes;
        if (SelectedOperation.equals("/")) {
            numberOnes = (numberOne*numberTwo);
            Equation = numberOnes+" "+SelectedOperation+" "+numberTwo+" =";
        }
        else Equation = numberOne+" "+SelectedOperation+" "+numberTwo+" =";
        return Equation;
    }
}