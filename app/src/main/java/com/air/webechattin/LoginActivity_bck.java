package com.air.webechattin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;

public class LoginActivity_bck extends AppCompatActivity implements View.OnClickListener {

    FirebaseUser currentUser;

    EditText etPhone;

    //FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_bck);

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
    protected void onStart() {
        super.onStart();

        if (currentUser != null){
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity_bck.this, MainActivity.class);
        startActivity(mainIntent);
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
            Intent intent = new Intent(LoginActivity_bck.this, VerifyPhoneActivity_bck.class);
            intent.putExtra("phoneNumber", phoneNumber);
            startActivity(intent);
        }
    }
}