package com.example.mathquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String difficulty = "non";
    Button starter, easybutton, mediumbutton, hardbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        starter = (Button)findViewById(R.id.starter);
        easybutton = (Button)findViewById(R.id.easybutton);
        mediumbutton = (Button)findViewById(R.id.mediumbutton);
        hardbutton = (Button)findViewById(R.id.hardbutton);

        starter.setOnClickListener(this);
        easybutton.setOnClickListener(this);
        mediumbutton.setOnClickListener(this);
        hardbutton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.easybutton) {
            easybutton.setTextColor(Color.parseColor("#fff200"));
            mediumbutton.setTextColor(Color.parseColor("#880e4f"));
            hardbutton.setTextColor(Color.parseColor("#880e4f"));
            difficulty = "easy";
        }
        else if (view.getId() == R.id.mediumbutton) {
            mediumbutton.setTextColor(Color.parseColor("#fff200"));
            easybutton.setTextColor(Color.parseColor("#880e4f"));
            hardbutton.setTextColor(Color.parseColor("#880e4f"));
            difficulty = "medium";
        }
        else if (view.getId() == R.id.hardbutton) {
            hardbutton.setTextColor(Color.parseColor("#fff200"));
            easybutton.setTextColor(Color.parseColor("#880e4f"));
            mediumbutton.setTextColor(Color.parseColor("#880e4f"));
            difficulty = "hard";
        }

        if (view.getId() == R.id.starter) {
            if (difficulty.equals("non")) {
                Toast diffnotselected = Toast.makeText(MainActivity.this, getResources().getString(R.string.diffnotselected), Toast.LENGTH_SHORT);
                diffnotselected.show();
            }
            else {
                nextActivity(view);
            }
        }
    }
    public void nextActivity(View view) {
        Intent intent = new Intent(view.getContext(), QuizTime.class);
        intent.putExtra("message_key", difficulty);
        startActivity(intent);
    }
}