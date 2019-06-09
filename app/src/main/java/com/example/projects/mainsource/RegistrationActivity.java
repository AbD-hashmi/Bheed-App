package com.example.projects.mainsource;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    EditText ed1, ed2, ed3, ed4, ed5;
    Button button;
    MaterialBetterSpinner materialBetterSpinner;
    String vehicle[]={"Four Wheeler","Three Wheeler","Bus"};
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    String password = null;
    String email=null;
    String confirm_pass=null;
    String vehicle_type =null;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ed1=(EditText)findViewById(R.id.edittext1);
        ed2=(EditText)findViewById(R.id.edittext2);
        ed3=(EditText)findViewById(R.id.edittext3);
        ed4=(EditText)findViewById(R.id.edittext4);
        ed5=(EditText)findViewById(R.id.edittext5);
        button=(Button)findViewById(R.id.button3);


        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        // button.setEnabled(false);
        materialBetterSpinner = (MaterialBetterSpinner) findViewById(R.id.spinner);
        //mspinner=(MaterialSpinner)findViewById(R.id.spinner2);
        mAuth = FirebaseAuth.getInstance();
        button.setOnClickListener(this);


        ArrayAdapter adapter2=new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,vehicle);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        materialBetterSpinner.setAdapter(adapter2);

    }




    public void Register()
    {

         email = ed2.getText().toString();

         password = ed4.getText().toString();
         confirm_pass = ed5.getText().toString();

        if (email.equals("")) {
            ed2.setError("Enter Email Id");
            ed2.requestFocus();
            return;
        }

        if (password.equals("")) {
            ed4.setError("Enter Password");
            ed4.requestFocus();
            return;
        }

        if (confirm_pass.equals("")) {
            ed5.setError("Enter Password");
            ed5.requestFocus();
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ed2.setError("Please enter a valid email");
            ed2.requestFocus();
            return;
        }


        if (ed4.length() < 6) {
            ed4.setError("Minimum lenght of password should be 6");
            ed4.requestFocus();
            return;
        }
        if(password.equals(confirm_pass))
        {

        }
        else
        {

            ed5.setError("Password Incorrect");
            ed5.requestFocus();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    finish();
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

                    vehicle_type=materialBetterSpinner.getText().toString();
                    String user_id = mAuth.getCurrentUser().getUid();
                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                        Map new_post = new HashMap<String,String>();
                        new_post.put("name",name);
                        new_post.put("phoneNo",phone_no);
                        new_post.put("email",email);
                        Log.d("vehicle",""+vehicle_type);
                        new_post.put("vehicle_Type",vehicle_type);

                        current_user_db.setValue(new_post);

                    startActivity(new Intent(RegistrationActivity.this, MapsActivity.class));
                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    @Override
    public void onClick(View view) {
       if(view.getId() == R.id.button3)
       {
           Register();
       }
    }
}