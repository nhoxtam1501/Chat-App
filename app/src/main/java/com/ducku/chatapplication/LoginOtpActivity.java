package com.ducku.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.ducku.chatapplication.databinding.ActivityLoginOtpBinding;
import com.ducku.chatapplication.utils.AppUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginOtpActivity extends AppCompatActivity {
    ActivityLoginOtpBinding loginOtpBinding;
    EditText loginOtp;
    Button loginNextBtn;
    TextView resendOtp;
    ProgressBar loginProgressBar;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    PhoneAuthProvider.ForceResendingToken resendingToken;
    final Long TIME_OUT_SECONDS = 60L;
    String phone;
    String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginOtpBinding = ActivityLoginOtpBinding.inflate(getLayoutInflater());
        setContentView(loginOtpBinding.getRoot());
        loginOtp = loginOtpBinding.loginOtp;
        loginNextBtn = loginOtpBinding.loginNextBtn;
        resendOtp = loginOtpBinding.resendOtpTextview;
        loginProgressBar = loginOtpBinding.loginProgressBar;

        auth.getFirebaseAuthSettings().forceRecaptchaFlowForTesting(true);

        phone = Objects.requireNonNull(getIntent().getExtras()).getString("phone");
        sendOtp(phone, false);


        loginNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredOtp = loginOtp.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
                signIn(credential);
            }
        });

        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOtp(phone, true);
            }
        });
    }

    void sendOtp(String phoneNumber, boolean isResend) {
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // This callback will be invoked in two situations:
                        // 1 - Instant verification. In some cases the phone number can be instantly
                        //     verified without needing to send or enter a verification code.
                        // 2 - Auto-retrieval. On some devices Google Play services can automatically
                        //     detect the incoming verification SMS and perform verification without
                        //     user action.
                        signIn(phoneAuthCredential);
                        setInProgress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        setInProgress(false);
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            AppUtils.showToast(getApplicationContext(), "Invalid request");
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            AppUtils.showToast(getApplicationContext(), "The SMS quota for the project has been exceeded");
                        } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                            // reCAPTCHA verification attempted with null Activity
                            AppUtils.showToast(getApplicationContext(), "reCAPTCHA verification attempted with null Activity");
                        }
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        resendingToken = forceResendingToken;
                        AppUtils.showToast(getApplicationContext(), "OTP sent successfully");
                        setInProgress(false);
                    }
                });
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    void signIn(PhoneAuthCredential phoneAuthCredential) {
        setInProgress(true);
        auth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                FirebaseUser user = task.getResult().getUser();
                Log.d("Firebase User: ", user.getPhoneNumber());
                Intent intent = new Intent(getApplicationContext(), LoginUsernameActivity.class);
                intent.putExtra("phone", phone);
                startActivity(intent);
            } else {
                AppUtils.showToast(getApplicationContext(), "OTP verification failed.");
            }
        });
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            loginProgressBar.setVisibility(View.VISIBLE);
            loginNextBtn.setVisibility(View.GONE);
        } else {
            loginProgressBar.setVisibility(View.GONE);
            loginNextBtn.setVisibility(View.VISIBLE);
        }
    }

    void startResendTimer() {
        resendOtp.setEnabled(false);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            Long remainTime = TIME_OUT_SECONDS;

            @Override
            public void run() {
                remainTime--;
                resendOtp.setText("OTP resend in " + remainTime + " seconds");
                if (remainTime <= 0) {
                    remainTime = TIME_OUT_SECONDS;
                    timer.cancel();
                    runOnUiThread(() -> {
                        resendOtp.setText("Resend OTP");
                        resendOtp.setEnabled(true);
                    });
                }
            }
        }, 0, 1000);
    }
}