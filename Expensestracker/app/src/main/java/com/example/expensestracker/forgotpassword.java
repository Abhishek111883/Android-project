package com.example.expensestracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class forgotpassword extends AppCompatActivity {


    //Declaration
    Button forgotbutton,backbtn;
    EditText email;

    FirebaseAuth mAuth;

    String stremail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        //Initialization
        email = findViewById(R.id.editEmailName);
        forgotbutton = findViewById(R.id.forgotbtn);
        backbtn = findViewById(R.id.back);

        mAuth = FirebaseAuth.getInstance();


        //forgot password process

        forgotbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stremail = email.getText().toString().trim();
                if (!TextUtils.isEmpty (stremail)) {
                    ResetPassword();
                }else {
                    email.setError("Email field can't be empty");
                }
            }
        });

        //back button

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void ResetPassword() {

        mAuth.sendPasswordResetEmail(stremail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess (Void unused) {
                        Toast.makeText (forgotpassword. this, "Reset Password link has been sent to your registered Email", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure (@NonNull Exception e) {
            Toast.makeText(forgotpassword. this, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        });
    }
}