package com.example.memorygame;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

public class QuickMathsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;



    private TextView countLabel;
    private TextView questionText;
    private TextView streakText;
    private EditText editText;

    private String rightAnswer;
    //private int rightAnswerCount = 0;
    private int quizCount = 1;
    public int streakCount = 0;
    private Boolean onStreak = true;

    ArrayList<ArrayList<String>> mathquestionsArray = new ArrayList<>();

    String quickmathsData[][]= {
            //{"Question", "Right Answer"}
            {"89 + 68 =", "157"},
            {"31 + 92 =", "123"},
            {"41 + 81 =", "122"},
            {"67 + 42 =", "109"},
            {"225 / 5 =", "45"},
            {"145 / 5 =", "29"},
            {"70 / 2 =", "35"},
            {"114 / 6 =", "19"},
            {"1 x 667 =", "667"},
            {"2 x 7 =", "14"},
            {"52 x 2 =", "104"},
            {"10 x 99 =", "990"},
            {"2 x 28 =", "58"},
            {"88 - 83 =", "5"},
            {"80 - 31 =", "49"},
            {"97 - 32 =", "65"},
            {"68 - 19 =", "49"},
            {"55 - 30 =", "25"},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = firebaseAuth.getInstance();


        setContentView(R.layout.activity_quick_maths);

        countLabel = findViewById(R.id.countLabel);
        questionText = findViewById(R.id.questionText);
        editText = findViewById(R.id.editTextAnswer);
        streakText = findViewById(R.id.streakTextView);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN) {

                    if(keyCode == KeyEvent.KEYCODE_ENTER){
                        checkAnswer();
                    }

                }
                return false;
            }
        });

        //Creating the mathsquestionsArray
        for (int i = 0; i < quickmathsData.length; i++) {

            //Preparing the Array.
            ArrayList<String> tmpArray = new ArrayList<>();
            tmpArray.add(quickmathsData[i][0]); //Question
            tmpArray.add(quickmathsData[i][1]); //Answer

            //Add tmpArray to mathquestionsArray
            mathquestionsArray.add(tmpArray);

        }

        showNextQuestion();

    }

    public void showNextQuestion() {

        //Updating countLabel.
        countLabel.setText("Question : " + quizCount );

        Random random = new Random();
        int randomNum = random.nextInt(mathquestionsArray.size());

        // Picking one question set.
        ArrayList<String> quiz = mathquestionsArray.get(randomNum);

        //Setting Question and Correct Answer.
        questionText.setText(quiz.get(0));
        rightAnswer = quiz.get(1);
        streakText.setText("Streak :" + streakCount);

        //Remove this quiz from Array.

        mathquestionsArray.remove(randomNum);

    }

    public void checkAnswer(){

        //Get editText.
        String answer = editText.getText().toString();

        String alertTitle;

        if(answer.equals(rightAnswer)) {
            //Correct!
            alertTitle = "Correct!";
            streakCount++;


        } else {
            //Failed
            alertTitle = "Incorrect!";
            onStreak = false;
            gameOver();

        }

        //Creating Dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(alertTitle);
        builder.setMessage("Answer : " + rightAnswer);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                editText.setText("");

                if (onStreak=false) {
                    //If user gets a question wrong.
                    //Game over show results
                    gameOver();
                } else {
                    quizCount++;
                    showNextQuestion();
                }

            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public void gameOver() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Streak Ended!");
        builder.setMessage("Your streak ended at " + streakCount);
        builder.setPositiveButton("Try Again?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recreate();
                UserScore userScore = new UserScore(
                    streakCount
                );

                FirebaseDatabase.getInstance().getReference("UserScore").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(streakCount).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(QuickMathsActivity.this, "Streak Saved", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                UserScore userScore = new UserScore(
                        streakCount
                );

                FirebaseDatabase.getInstance().getReference("UserScore").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(streakCount).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(QuickMathsActivity.this, "Streak Saved", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                startActivity(new Intent(QuickMathsActivity.this, MainMenu.class));
            }
        });
        builder.setCancelable(false);
        builder.show();

    }





}
