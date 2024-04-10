package com.ducku.chatapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ducku.chatapplication.databinding.ActivityLoginUsernameBinding;

public class LoginUsernameActivity extends AppCompatActivity {
    ActivityLoginUsernameBinding usernameBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usernameBinding = ActivityLoginUsernameBinding.inflate(getLayoutInflater());
        setContentView(usernameBinding.getRoot());

    }
}