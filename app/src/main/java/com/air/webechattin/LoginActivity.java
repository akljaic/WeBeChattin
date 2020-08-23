package com.air.webechattin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    FirebaseUser currentUser;

    Button mLoginButton;
    Button mPhoneLoginButton;
    EditText mUserEmail;
    EditText mUserPassword;
    TextView mNeedNewAccount;
    TextView mForgetPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitializeFields();

        mNeedNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToRegisterActivity();
            }
        });


        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowUserToLogin();
            }
        });
    }

    private void AllowUserToLogin() {
        String email = mUserEmail.getText().toString();
        String password = mUserPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter an email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter a password...", Toast.LENGTH_SHORT).show();
        }
    }

    private void InitializeFields() {
        mLoginButton = (Button)findViewById(R.id.login_button);
        mPhoneLoginButton = (Button)findViewById(R.id.phone_login_button);
        mUserEmail = (EditText)findViewById(R.id.login_email);
        mUserPassword = (EditText)findViewById(R.id.login_password);
        mNeedNewAccount = (TextView)findViewById(R.id.need_new_account_link);
        mForgetPassword = (TextView)findViewById(R.id.forget_password_link);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser != null){
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}