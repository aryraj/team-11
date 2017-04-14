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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    EditText p_phone, p_name;
    Button btn_save_p;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String uid;
    FirebaseDatabase database;
    String s_phone, s_name;
    Map<String, Object> nameMap;
    Map<String, Object> phoneMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth= FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();



        //initializing firebase instance
      /*  mAuth = FirebaseAuth.getInstance();

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
        }; */


        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/Walkway_Bold.ttf");


        p_name = (EditText) findViewById(R.id.eT_name_pro);
        //p_name.setTypeface(tf);
        p_phone = (EditText) findViewById(R.id.eT_phone_pro);
       // p_phone.setTypeface(tf);
        btn_save_p = (Button) findViewById(R.id.btn_save_pro);
        //btn_save_p.setTypeface(tf);


        s_name = p_name.getText().toString();
        s_phone = p_phone.getText().toString();

        nameMap = new HashMap<>();
        phoneMap = new HashMap<>();

        nameMap.put("name", s_name);
        phoneMap.put("phone", s_phone);



        btn_save_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child(uid).child("profile-details");
                myRef.updateChildren(nameMap);
                myRef.updateChildren(phoneMap);
                startActivity(new Intent(ProfileActivity.this, MapsActivity.class));
            }
        });




    }







}

