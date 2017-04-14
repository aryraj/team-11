package com.in.baymax;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddContactActivity extends AppCompatActivity {

    ListView mListView;
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 100;
    Cursor c;
    ArrayList contacts;
    Button button;
    FirebaseAuth mAuth;
    Map<String, Object> hashMap, map2;
    String uid;
     FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
         mAuth = FirebaseAuth.getInstance();
         uid = mAuth.getCurrentUser().getUid();


        mListView = (ListView) findViewById(R.id.idList);

        Intent i = getIntent();
        String sName = i.getStringExtra("name");
        String sPhone = i.getStringExtra("phone");



        hashMap.put("name", sName);
        map2.put("phone", sPhone);



        button = (Button) findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference().child(uid);
                myRef.child("profile-details").updateChildren(hashMap);
                myRef.child("profile-details").updateChildren(map2);
                getContacts();

            }
        });





    }

    private void getContacts() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED){
            showContacts();
        } else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, contacts);

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String details = ((TextView) view).getText().toString();
                String arr[] = details.split(":");

                /* TODO
                 * arr[0] returns name and arr[1] return phone number
                 * Save these two values in database and read as emergency contact
                 * Go to https://github.com/ary3003/BOOKWORM_MIL and open 'ListUserActivity' for refrence
                 * */

                Toast.makeText(AddContactActivity.this, "Name: " + arr[0] + "\n" + "Number: " + arr[1], Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){

        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display the names.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void showContacts() {

        c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME
                + " ASC ");

        contacts = new ArrayList();

        while (c.moveToNext()){

            String contactName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            contacts.add(contactName + ":" + phoneNumber);
        }

        c.close();
    }
}