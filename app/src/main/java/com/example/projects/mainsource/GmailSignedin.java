package com.example.projects.mainsource;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.HashMap;
import java.util.Map;

public class GmailSignedin extends AppCompatActivity implements View.OnClickListener {
    EditText ed1, ed2, ed3, ed4, ed5;
    Button button;
    MaterialBetterSpinner materialBetterSpinner;
    String vehicle[] = {"Four Wheeler", "Three Wheeler", "Bus"};
    private FirebaseAuth mAuth;
    ProgressBar progressBar;

    String email = null;

    String vehicle_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmail_signedin);
        ed1 = (EditText) findViewById(R.id.edittext1);
        ed2 = (EditText) findViewById(R.id.edittext2);
        ed3 = (EditText) findViewById(R.id.edittext3);
        ed4 = (EditText) findViewById(R.id.edittext4);
        ed5 = (EditText) findViewById(R.id.edittext5);
        button = (Button) findViewById(R.id.button3);


        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        // button.setEnabled(false);
      materialBetterSpinner = (MaterialBetterSpinner) findViewById(R.id.spinner);
        //mspinner=(MaterialSpinner)findViewById(R.id.spinner2);
        mAuth = FirebaseAuth.getInstance();


        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, vehicle);
         adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        materialBetterSpinner.setAdapter(adapter2);
        FirebaseUser user;
        FirebaseAuth mAuth;
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        String na=user.getDisplayName().toString();
//        String phn=user.getPhoneNumber().toString();
        String ema=user.getEmail().toString();

        ed2.setText(ema);
        ed1.setText(na);
       // ed3.setText(phn);



        button.setOnClickListener(this);
    }


    public void Register() {

        email = ed2.getText().toString();


        if (email.equals("")) {
            ed2.setError("Enter Email Id");
            ed2.requestFocus();
            return;
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ed2.setError("Please enter a valid email");
            ed2.requestFocus();
            return;
        }




        String name = ed1.getText().toString();

        String phone_no = ed3.getText().toString();
        //String vehicle_type = materialBetterSpinner.getText().toString();

        if (name.equals("")) {
            ed1.setError("Enter Name");
            ed1.requestFocus();
            return;
        }
        if (phone_no.equals("")) {
            ed3.setError("Enter Mobile Number");
            ed3.requestFocus();
            return;
        }

        vehicle_type = materialBetterSpinner.getText().toString();

        progressBar.setVisibility(View.VISIBLE);

        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        Map new_post = new HashMap<String, String>();
        new_post.put("name", name);
        //new_post.put("email",email);
        new_post.put("phoneNo", phone_no);
        new_post.put("email", email);
        new_post.put("vehicle Type", vehicle_type);

        current_user_db.setValue(new_post);

        startActivity(new Intent(GmailSignedin.this, MapsActivity.class));

      /*  progressBar.setVisibility(View.GONE);
        if (task.isSuccessful()) {
            finish();


        } else {

            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }*/
    }



    @Override
    public void onClick(View view) {
        if (view==button){
            Register();
        }
    }
}