package com.air.webechattin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Register extends AppCompatActivity {

    EditText etPhone;
    String phoneNumber;
    Button bRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etPhone = (EditText)findViewById(R.id.phoneNumberEditText);
        bRegister = (Button)findViewById(R.id.registerButton);


        bRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                phoneNumber = etPhone.getText().toString().trim();

                if (phoneNumber.isEmpty() || phoneNumber.length() < 10){
                    etPhone.setError("Enter a valid phone number!");
                    etPhone.requestFocus();
                    return;
                }

                Intent intent = new Intent(Register.this, VerifyPhone.class);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);
            }
        });

    }
}