package com.air.webechattin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etPhone;

    //FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //if(user != null){
        //    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
        //    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //    startActivity(intent);
        //}
        //else {
            etPhone = (EditText)findViewById(R.id.phoneNumberEditText);
            findViewById(R.id.loginButton).setOnClickListener(this);
        //}
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.loginButton:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String phoneNumber = etPhone.getText().toString().trim();

        if (phoneNumber.isEmpty() || phoneNumber.length() < 9){
            etPhone.setError("Enter a valid phone number");
            etPhone.requestFocus();
            return;
        }

        else {
            Intent intent = new Intent(LoginActivity.this, VerifyPhoneActivity.class);
            intent.putExtra("phoneNumber", phoneNumber);
            startActivity(intent);
        }
    }
}