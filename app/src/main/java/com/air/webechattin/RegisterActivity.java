package com.air.webechattin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {

    Button mCreateAccountButton;
    EditText mUserEmail;
    EditText mUserPassword;
    TextView mAlreadyHaveAccount;

    FirebaseAuth mAuth;
    DatabaseReference rootReference;

    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        rootReference = FirebaseDatabase.getInstance().getReference();

        InitializeFields();

        mAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToLoginActivity();
            }
        });

        mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });
    }


    private void InitializeFields() {
        mCreateAccountButton = (Button)findViewById(R.id.register_button);
        mUserEmail = (EditText)findViewById(R.id.register_email);
        mUserPassword = (EditText)findViewById(R.id.register_password);
        mAlreadyHaveAccount = (TextView)findViewById(R.id.already_have_an_account_link);

        loadingBar = new ProgressDialog(this);
    }

    private void SendUserToLoginActivity() {
        Intent registerIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(registerIntent);
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void CreateNewAccount() {
        String email = mUserEmail.getText().toString();
        String password = mUserPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter an email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter a password...", Toast.LENGTH_SHORT).show();
        }
        if (password.length() < 6){
            mUserPassword.setError("Minimum password length is 6!");
            mUserPassword.requestFocus();
            return;
        }

        else {
            loadingBar.setTitle("Creating new account");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        String currentUserID = mAuth.getCurrentUser().getUid();
                        rootReference.child("Users").child(currentUserID).setValue("");

                        rootReference.child("Users").child(currentUserID).child("device_token").setValue(deviceToken);

                        SendUserToMainActivity();
                        Toast.makeText(RegisterActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    else {
                        String message = task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }
}