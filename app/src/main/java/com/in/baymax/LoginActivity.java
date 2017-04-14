package com.in.baymax;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText l_email, l_password;
    Button btn_log, btn_s_up;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        //initializing firebase instance
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("TAG5", "User signed in");
                } else {
                    Log.d("TAG6", "User signed out");
                }
            }
        };


        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/Walkway_Bold.ttf");


        l_email = (EditText) findViewById(R.id.eT_email_login);
        l_email.setTypeface(tf);
        l_password = (EditText) findViewById(R.id.eT_password_login);
        l_password.setTypeface(tf);
        btn_log = (Button) findViewById(R.id.btn_logIn);
        btn_log.setTypeface(tf);
        btn_s_up = (Button) findViewById(R.id.btn_sign_up);
        btn_s_up.setTypeface(tf);

        btn_s_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });


        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mEmail = l_email.getText().toString();
                String mPassword = l_password.getText().toString();

                if(mEmail.isEmpty() ||mPassword.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Please fill in all the fields!", Toast.LENGTH_SHORT).show();
                }

                else {
                    signIn(mEmail, mPassword);
                }
            }


        });
    }



    @Override
    public void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if(mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    public void signIn(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "signIncomplete");
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();

                        }

                        else
                        {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MapsActivity.class));
                        }
                    }
                });
    }

}

