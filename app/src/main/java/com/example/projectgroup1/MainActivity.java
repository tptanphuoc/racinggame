package com.example.projectgroup1;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import model.Account;

public class MainActivity extends AppCompatActivity {
    List<Account> userAccounts = new ArrayList<>();
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        relativeLayout = findViewById(R.id.mlayout);

        userAccounts.add(new Account("admin1", "admin1"));
        userAccounts.add(new Account("admin2", "admin2"));
        userAccounts.add(new Account("admin3", "admin3"));

        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        TextView userName = findViewById(R.id.username);
        TextView password = findViewById(R.id.password);
        Button loginBtn = findViewById(R.id.loginbtn);
        loginBtn.setOnClickListener(view -> {
            String enteredUsername = userName.getText().toString().trim();
            String enteredPassword = password.getText().toString().trim();

            Account loggedInAccount = null;

            for (Account account : userAccounts) {
                if (account.getUsername().equals(enteredUsername) && account.getPassword().equals(enteredPassword)) {
                    loggedInAccount = account;
                    break;
                }
            }
            if (loggedInAccount != null) {
                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                intent.putExtra("loggedInAccount", loggedInAccount);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
