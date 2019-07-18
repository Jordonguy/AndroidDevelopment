package com.example.memorygame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainMenu extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private TextView textViewCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();

            startActivity(new Intent(this, MainActivity.class));
        }


        textViewCurrentUser = (TextView) findViewById(R.id.textViewCurrentUser);

        findViewById(R.id.btnMatchTwo).setOnClickListener(this);
        findViewById(R.id.btnQuiz).setOnClickListener(this);
        findViewById(R.id.btnQuickMaths).setOnClickListener(this);
        findViewById(R.id.btnProfile).setOnClickListener(this);

        //displaying Current users email or their name
        loadUserInformation();

    }

    private void loadUserInformation(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) {
            if (user.getDisplayName() != null) {
                textViewCurrentUser.setText("Welcome " + user.getDisplayName());
            } else {
                textViewCurrentUser.setText("Welcome " + user.getEmail());
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnMatchTwo:
                startActivity(new Intent(this, MatchTwo.class));
                break;
            case R.id.btnQuiz:
                startActivity(new Intent(this, QuizActivity.class));
                break;
            case R.id.btnQuickMaths:
                startActivity(new Intent(this, QuickMathsActivity.class));
                break;
            case R.id.btnProfile:
                startActivity(new Intent(this, Profile.class));
                break;
        }
    }
}
