package com.air.webechattin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Register extends AppCompatActivity {

    EditText phone;
    String phoneNumber;
    Button registerButton;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        phone = (EditText)findViewById(R.id.phoneNumber);
        registerButton = (Button)findViewById(R.id.registerButton);


        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                phoneNumber = phone.getText().toString().trim();

                if (phoneNumber.isEmpty() || phoneNumber.length() < 10){
                    phone.setError("Enter a valid phone number!");
                    phone.requestFocus();
                    return;
                }

                Intent intent = new Intent(Register.this, VerifyPhone.class);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);
            }
        });

    }
}