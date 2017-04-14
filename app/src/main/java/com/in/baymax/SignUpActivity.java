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

public class SignUpActivity extends AppCompatActivity {

    EditText name,phone,password,email;
    Button login, signUp;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_sign_up);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_sign_up);

        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/Walkway_Bold.ttf");

        password = (EditText) findViewById(R.id.eT_password);
        password.setTypeface(tf);
        login = (Button) findViewById(R.id.btn_login);
        login.setTypeface(tf);
        signUp = (Button) findViewById(R.id.btn_signUp);
        signUp.setTypeface(tf);
        email = (EditText) findViewById(R.id.eT_email);
        email.setTypeface(tf);


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("TAG", "signed in");

                } else {
                    Log.d("TAG", "signed out");
                }
            }
        };



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // final String mName = name.getText().toString();
                final String mEmail = email.getText().toString();
                final String mPassword = password.getText().toString();
              //  final String mPhone = phone.getText().toString();



                if( mEmail.isEmpty() || mPassword.isEmpty()  )
                {
                    Toast.makeText(SignUpActivity.this, "Please fill in all the fields!", Toast.LENGTH_SHORT).show();
                }

                else {
                    createAccount(mEmail, mPassword);
                }

               // Intent intent = new Intent(SignUpActivity.this, AddContactActivity.class);
               // intent.putExtra("name", mName);
               // intent.putExtra("phone", mPhone);
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
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    private void createAccount(String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG3","Successful");

                        if(!task.isSuccessful())
                        {
                            Toast.makeText(SignUpActivity.this,"Authentication Failed!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(SignUpActivity.this,"Authentication Successful!",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this,ProfileActivity.class));
                        }
                    }
                });
    }


}