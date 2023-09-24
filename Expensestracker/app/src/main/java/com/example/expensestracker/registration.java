package com.example.expensestracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class registration extends AppCompatActivity {



    private EditText regname;
    private EditText regemail;

    private EditText regpass;
    private Button regbtn;
    private TextView signin;



    // Firebase//

    private FirebaseAuth regauth;

    public ProgressDialog regdiag;


    String newpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Initialization
        regdiag = new ProgressDialog(this);

        regauth = FirebaseAuth.getInstance();

        RegistrationMethod();

    }

    private void RegistrationMethod() {

        regname = findViewById(R.id.registrationname);
        regemail = findViewById(R.id.registrationemail);
        regpass = findViewById(R.id.registrationpass);
        regbtn = findViewById(R.id.buttonRegister);
        signin = findViewById(R.id.regsignin);


        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newname = regname.getText().toString().trim();
                String newemail = regemail.getText().toString().trim();
                 newpass = regpass.getText().toString().trim();

                if (TextUtils.isEmpty(newname)){
                    regname.setError("NAME REQUIRED....");
                    return;
                }
                if (TextUtils.isEmpty(newemail)){
                    regemail.setError("EMAIL REQUIRED..");
                    return;
                }
                if (TextUtils.isEmpty(newpass)){
                    regpass.setError("PASSWORD REQUIRED..");
                    return;
                }

                regdiag.setMessage("PROCESSING...");
                regdiag.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                regdiag.show();


                //check email for same mail is registered are not
                checkEmailExistence(newemail);



            }
        });

            // back to login page
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(registration.this, MainActivity.class));
            }
        });

    }


    /// checking email

    private void checkEmailExistence(String newemail) {
       regauth.fetchSignInMethodsForEmail(newemail)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            if (result != null && result.getSignInMethods() != null
                                    && !result.getSignInMethods().isEmpty()) {
                                // Email exists in Firebase
                                Toast.makeText(registration.this, "Email,Already exists!", Toast.LENGTH_SHORT).show();
                                regdiag.dismiss();
                            } else {

                                //Registration Method
                                regauth.createUserWithEmailAndPassword(newemail,newpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {


                                        if (task.isSuccessful()){
                                            regdiag.dismiss();
                                            Toast.makeText(getApplicationContext(),"REGISTRATION SUCCESSFUL",Toast.LENGTH_SHORT).show();

                                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                                        }else {
                                            regdiag.dismiss();
                                            Toast.makeText(getApplicationContext(),"REGISTRATION FAILED",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    }
                });

    }
}