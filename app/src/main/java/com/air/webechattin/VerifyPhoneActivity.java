package com.air.webechattin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity implements View.OnClickListener {

    String verificationId;
    EditText etCode;
    FirebaseAuth fbAuth;
    Button bSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        fbAuth = FirebaseAuth.getInstance();
        etCode = findViewById(R.id.codeEditText);
        
        Intent intent = getIntent();
        String phoneNumber = intent.getStringExtra("phoneNumber");
        sendVerificationCode(phoneNumber);

        findViewById(R.id.signInButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String code = etCode.getText().toString().trim();
        if (code.isEmpty() || code.length() < 6){
            etCode.setError("Enter valid code");
            etCode.requestFocus();
            return;
        }

        verifyVerificationCode(code);
    }

    //sending verification code
    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+385" + phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);;
    }
    //is the code sent
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            //manual entry
            if(code != null){
                etCode.setText(code);
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e){
            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationId = s;
        }
    };

    //code verification
    private void verifyVerificationCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential).addOnCompleteListener(VerifyPhoneActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(VerifyPhoneActivity.this, "Successful verification!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(VerifyPhoneActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    String errMessage = "Unsuccessful verification.";

                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        errMessage = "Invalid code!";
                    }

                    Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), errMessage, Snackbar.LENGTH_LONG);
                    snackbar.setAction("Dissmiss", new View.OnClickListener(){
                       @Override
                       public void onClick (View v){

                       }
                    });
                    snackbar.show();
                }
            }
        });
    }


}