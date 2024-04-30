package com.ducku.chatapplication.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ducku.chatapplication.model.UserModel;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.auth.User;

import java.util.Date;

public class AppUtils {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, UserModel userModel) {
        intent.putExtra("username", userModel.getUsername());
        intent.putExtra("phone", userModel.getPhone());
        intent.putExtra("userId", userModel.getUserId());
    }

    public static UserModel getUserModelFromIntent(Intent intent) {
        UserModel userModel = new UserModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        return userModel;
    }
}
