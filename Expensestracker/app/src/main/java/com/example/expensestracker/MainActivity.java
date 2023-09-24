package com.example.expensestracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class MainActivity extends AppCompatActivity {
    ImageView img;

    private EditText loginemail;

    private EditText loginpass;
    private Button loginbtn;

    private TextView forgot;

    private TextView signup;


    // firebase

    private FirebaseAuth logauth;

    public ProgressDialog logdialog;


    private Button refreshButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isNetworkAvailable()) {
            // No internet connection, display "No Internet" screen
            setContentView(R.layout.activity_internet_connectivity);
            initializeNoInternetScreen();
        } else {
            initializeApp();
        }
    }

    private void initializeNoInternetScreen() {
        refreshButton = findViewById(R.id.refreshbutton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    // Internet connection is available, proceed with your app
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();
                } else {
                        Toast toast = Toast.makeText(MainActivity.this, "Still no internet", Toast.LENGTH_SHORT);
                            toast.show();
// Set the custom duration (e.g., 3000 milliseconds or 3 seconds)
                    int durationInMilliseconds = 500;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, durationInMilliseconds);
                }
            }
        });
    }

    // Check for internet connectivity
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void initializeApp() {

        //Initialization
        loginemail = findViewById(R.id.editTextEmail);
        loginpass = findViewById(R.id.editTextPasss);
        loginbtn = findViewById(R.id.buttonLogin);
        forgot = findViewById(R.id.forgot_password);
        signup = findViewById(R.id.signup);
        img = findViewById(R.id.backimg);
        logdialog = new ProgressDialog(this);
        logauth = FirebaseAuth.getInstance();




        // if user is login already// dont want tologin agian

        if (logauth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }
        logindetails();
    }

    @Override
    public void onBackPressed() {
        // Exit the app when the back button is pressed
        finishAffinity();
    }

    private void logindetails() {

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginemail.getText().toString().trim();
                String pass = loginpass.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    loginemail.setError("NAME REQUIRED....");
                    return;

                }if (TextUtils.isEmpty(pass)){
                    loginpass.setError("PASSWORD REQUIRED....");
                    return;
                }

                logdialog.setMessage("PROCESSING....");



                ///login process
                logauth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            logdialog.dismiss();
                            Toast.makeText(getApplicationContext(),"LOGIN SUCCESSFUL",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        }else {
                            logdialog.dismiss();
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(getApplicationContext(),"INVALID EMAIL",Toast.LENGTH_SHORT).show();
                            }
                            else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                Toast.makeText(getApplicationContext()," INVALID PASSWORD ",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        // for forgot password page
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, forgotpassword.class));
            }
        });
        // Registration page
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, registration.class));
            }
        });

    }
}