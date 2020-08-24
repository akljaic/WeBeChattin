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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {
    Button mVerifyButton;
    EditText mInputVerificationCode;

    //ProgressDialog loadingBar;

    //phone auth
    FirebaseAuth mAuth;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        String phoneNumber = intent.getStringExtra("phoneNumber");
        SendVerificationCode(phoneNumber);

        InitializeFields();

        mVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerifyCode();
            }
        });
    }

    private void InitializeFields() {
        mVerifyButton = (Button)findViewById(R.id.verify_code_button);
        mInputVerificationCode = (EditText)findViewById(R.id.verification_code_input);

        //loadingBar = new ProgressDialog(this);
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(VerifyPhoneActivity.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

    private void SendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+385" + phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e){
            //loadingBar.dismiss();
            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
            mVerificationId = verificationId;
            mResendToken = token;

            //loadingBar.dismiss();

            Toast.makeText(VerifyPhoneActivity.this, "Code has been sent", Toast.LENGTH_LONG).show();
        }
    };

    private void VerifyCode() {
        String verificationCode = mInputVerificationCode.getText().toString();

        if (TextUtils.isEmpty(verificationCode)){
            Toast.makeText(VerifyPhoneActivity.this, "Enter the verification code", Toast.LENGTH_LONG).show();
        }
        else {
            //loadingBar.setTitle("Code verification");
            //loadingBar.setMessage("Please wait...");
            //loadingBar.setCanceledOnTouchOutside(false);
            //loadingBar.show();

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
            signInWithPhoneAuthCredential(credential);
        }
    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //loadingBar.dismiss();
                            Toast.makeText(VerifyPhoneActivity.this, "Verification completed successfully", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();
                        }
                        else {
                            String message = task.getException().toString();
                            Toast.makeText(VerifyPhoneActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}