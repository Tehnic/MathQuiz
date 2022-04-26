package com.example.mathquiz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
    int score = 0;

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

            if (equation.getText().toString().trim().equals("2+2 =")) {
                Equation = generator(difficulty);
                equation.setText(Equation);
                new CountDownTimer(60000, 100) {
                    public void onTick(long millisUntilFinished) {
                        timer.setText(String.valueOf(millisUntilFinished / 1000));
                        EditText text = (EditText) (findViewById(R.id.response));
                        if (text.getText().toString().length() == String.valueOf(answer).length()) {
                            if (text.getText().toString().equals(String.valueOf(answer))) {
                                text.getText().clear();
                                switch (difficulty) {
                                    case "easy":
                                        score = score + 1;
                                        break;
                                    case "medium":
                                        score = score + 3;
                                        break;
                                    case "hard":
                                        score = score + 5;
                                        break;
                                }
                                Equation = generator(difficulty);
                                equation.setText(Equation);
                            }
                            else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(100, 255));
                                }
                                else {
                                    ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(150);
                                }
                                text.getText().clear();
                            }
                        }
                    }

                    public void onFinish() {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(QuizTime.this);
                        String sb = getResources().getString(R.string.gameover) + " " + score + "\n" + getResources().getString(R.string.restartquestion);
                        alertDialog.setMessage(sb);
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
            case "easy": numberOne = (int) (Math.random() * 60 - 30); break;//От -30 до 30
            case "medium":
                if (SelectedOperation.equals("*") || SelectedOperation.equals("/")) {
                    numberOne = (int) (Math.random()*30-15);
                }
                else {
                    numberOne = (int) (Math.random() * 200 - 100); //От -100 до 100
                }
                break;
            case "hard":
                if (SelectedOperation.equals("*") || SelectedOperation.equals("/")) {
                    numberOne = (int) (Math.random()*50-25);
                }
            else {
                numberOne = (int) (Math.random() * 600 - 300); //От -300 до 300
            }
                break;
        }
        return numberOne;
    }
    public int randomNumberTwo (String difficulty, String SelectedOperation) {
        switch(difficulty) {
            case "easy": numberTwo = (int) (Math.random() * 30); break;//От 0 до 30
            case "medium":
                if (SelectedOperation.equals("*") || SelectedOperation.equals("/")) {
                    numberTwo = (int) (Math.random()*15);
                }
                else {
                    numberTwo = (int) (Math.random() * 100); //От 0 до 100
                }
                break;
            case "hard":
                if (SelectedOperation.equals("*") || SelectedOperation.equals("/")) {
                    numberTwo = (int) (Math.random()*25);
                }
                else {
                    numberTwo = (int) (Math.random() * 300); //От 0 до 300
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