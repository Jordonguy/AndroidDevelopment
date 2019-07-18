package com.example.memorygame;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    private TextView countLabel;
    private ImageView questionImage;
    private EditText editText;

    private String rightAnswer;
    private int rightAnswerCount = 0;
    private int quizCount = 1;

    ArrayList<ArrayList<String>> quizArray = new ArrayList<>();

    String quizData[][]={
            //{"Image Name", "Right Answer"}
            {"taco", "taco"},
            {"sausage", "sausage"},
            {"icecream", "icecream"},
            {"hotdog", "hotdog"},
            {"croissant", "croissant"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        countLabel = findViewById(R.id.countLabel);
        questionImage = findViewById(R.id.questionImage);
        editText = findViewById(R.id.editTextUsername);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){

                    if(keyCode == KeyEvent.KEYCODE_ENTER) {
                        checkAnswer();
                    }
                }

                return false;
            }
        });

        //Creating quizArray from quizData.
        for(int i = 0; i < quizData.length; i++) {

            //Preparing Array
            ArrayList<String> tmpArray = new ArrayList<>();
            tmpArray.add(quizData[i][0]); //Setting Image name in the Array
            tmpArray.add(quizData[i][1]); //Setting the correct answer in the Array

            //Adding tmpArray to quizArray.
            quizArray.add(tmpArray);

        }

        showNextQuiz();

    }

    public void showNextQuiz() {

        //updating quizCountLabel.
        countLabel.setText("Question :" + quizCount);

        //Generate random number between 0 and 4 (quizArray's size -1 because they start at ---> 0 <---)
        Random random = new Random();
        int randomNum = random.nextInt(quizArray.size());

        //Picking one quiz setter.
        ArrayList<String> quiz = quizArray.get(randomNum);

        //Setting Image to the right question
        // Array format: ["Image Name", "Right Answer")
        questionImage.setImageResource(
                getResources().getIdentifier(quiz.get(0), "drawable", getPackageName()));
        rightAnswer = quiz.get(1);

        //Removing a quiz from the quizArray after being completed
        quizArray.remove(randomNum);



    }

    public void checkAnswer() {

        //Getting editText.
        String answer = editText.getText().toString().toLowerCase().trim();

        String alertTitle;

        if(answer.equals(rightAnswer)) {
            //Correct!!!
            alertTitle = "Correct!";
            rightAnswerCount++;

        } else {
            //Incorrect!!!
            alertTitle = "Incorrect";

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(alertTitle);
        builder.setMessage("Answer : " + rightAnswer);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                editText.setText("");

                if(quizArray.size() < 1) {
                    //Condition is quiz array is empty after all questions have been attempted
                    //Showing Results
                    showResult();
                } else {
                    quizCount++;
                    showNextQuiz();
                }

            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public void showResult() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Result");
        builder.setMessage(rightAnswerCount + " / 5");
        builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                recreate();
            }
        });
        builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }
}
