package com.air.webechattin;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class PhoneLoginActivity extends AppCompatActivity {

    Button mSendVerificationCodeButton;
    EditText mInputPhoneNumber;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();

        InitializeFields();

        mSendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendVerificationCode();
            }
        });
    }

    private void InitializeFields() {
        mSendVerificationCodeButton = (Button)findViewById(R.id.send_verification_code_button);
        mInputPhoneNumber = (EditText)findViewById(R.id.phone_number_input);
    }

    private void SendVerificationCode() {
        String phoneNumber = mInputPhoneNumber.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(PhoneLoginActivity.this, "Enter your phone number", Toast.LENGTH_SHORT).show();
        }
        if (phoneNumber.length() < 9){
            Toast.makeText(PhoneLoginActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(PhoneLoginActivity.this, VerifyPhoneActivity.class);
            intent.putExtra("phoneNumber", phoneNumber);
            startActivity(intent);
        }
    }
}