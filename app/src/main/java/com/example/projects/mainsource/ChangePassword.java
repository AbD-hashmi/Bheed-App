package com.example.projects.mainsource;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {

    EditText e1,e2;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        e1 = findViewById(R.id.editText2);
        e2 = findViewById(R.id.editText3);
        progressBar = findViewById(R.id.progressBar3);
        mAuth = FirebaseAuth.getInstance();
        btn = findViewById(R.id.btnsave);
        btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnsave){
            Toast.makeText(this, "Chla", Toast.LENGTH_SHORT).show();
        chngPassword();
        }
    }

    private void chngPassword() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String password = e1.getText().toString();
        String confPassword = e2.getText().toString();
        if (password.equals("")) {
            e1.setError("Enter Email Id");
            e1.requestFocus();
            return;
        }
        if (confPassword.equals("")) {
            e2.setError("Enter Email Id");
            e2.requestFocus();
            return;
        }
        if (e1.length() < 6) {
            e1.setError("Minimum lenght of password should be 6");
            e1.requestFocus();
            return;
        }
        if (e2.length() < 6) {
            e2.setError("Minimum lenght of password should be 6");
            e2.requestFocus();
            return;
        }

        if (!password.equals(confPassword)) {
            Toast.makeText(this, "password is incorrect", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        if(user!=null)
        {
            user.updatePassword(confPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBar.setVisibility(View.GONE);
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ChangePassword.this, "Password is changed", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        finish();

                        Intent i = new Intent(ChangePassword.this,LoginActivity.class);
                        startActivity(i);
                    }
                    else {
                        Toast.makeText(ChangePassword.this, "Your Password could not be changed!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
