package com.ducku.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.ducku.chatapplication.databinding.ActivityLoginPhoneNumberActivityBinding;
import com.hbb20.CountryCodePicker;

public class LoginPhoneNumberActivity extends AppCompatActivity {
    ActivityLoginPhoneNumberActivityBinding phoneNumberActivityBinding;
    Button sendOtpBtn;
    EditText phoneInput;
    ProgressBar progressBar;
    CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phoneNumberActivityBinding = ActivityLoginPhoneNumberActivityBinding.inflate(getLayoutInflater());
        setContentView(phoneNumberActivityBinding.getRoot());

        sendOtpBtn = phoneNumberActivityBinding.sendOtpBtn;
        phoneInput = phoneNumberActivityBinding.loginMobileNumber;
        progressBar = phoneNumberActivityBinding.loginProgressBar;
        countryCodePicker = phoneNumberActivityBinding.loginCountrycode;

        progressBar.setVisibility(View.GONE);
        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        countryCodePicker.setDefaultCountryUsingNameCode("VN");
        sendOtpBtn.setOnClickListener(v -> {
            if (!countryCodePicker.isValidFullNumber()) {
                phoneInput.setError("Invalid phone number");
                return;
            }
            Intent intent = new Intent(getApplicationContext(), LoginOtpActivity.class);
            intent.putExtra("phone", countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);
        });

    }
}