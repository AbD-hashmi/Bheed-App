package com.example.projects.mainsource;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener{
    EditText ed1;
    Button btn;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ed1 = (EditText) findViewById(R.id.editText);
        btn = (Button) findViewById(R.id.button4);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        mAuth = FirebaseAuth.getInstance();
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button4)
        {
            Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
            String userEmail = ed1.getText().toString().trim();
            if(userEmail.equals(""))
            {
                ed1.setError("Email is blank");
                ed1.requestFocus();
                return;

            }
            else {
                Toast.makeText(this, "Test1", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);
            mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        progressBar.setVisibility(View.GONE );
                        Toast.makeText(ForgotPassword.this, "Password Reset Email is send", Toast.LENGTH_SHORT).show();
                       finish();
                    }
                    else 
                    {
                        Toast.makeText(ForgotPassword.this, "Password Reset email could not be send", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }

    }}
}
