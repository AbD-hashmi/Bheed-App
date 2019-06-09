package com.example.projects.mainsource;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class User_profile extends AppCompatActivity {
    Toolbar toolbar;
    TextView info,Id;
    EditText name, mobile, vType;
    Button save, changePassword, logout;
    CircleImageView cvi;
    private FirebaseAuth mAuth;
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        info = (TextView) findViewById(R.id.info);

        Id=(TextView)findViewById(R.id.Email);

        name = (EditText) findViewById(R.id.userName);

        mobile = (EditText) findViewById(R.id.userNumber);

        vType=(EditText)findViewById(R.id.vehicle);

        save = (Button) findViewById(R.id.save);

        changePassword = (Button) findViewById(R.id.password);

        logout = (Button) findViewById(R.id.logout);

        cvi = (CircleImageView) findViewById(R.id.image_profile);

        cvi.setEnabled(false);
        name.setEnabled(false);
        mobile.setEnabled(false);
        save.setEnabled(false);
        changePassword.setEnabled(false);
        vType.setEnabled(false);

        mAuth=FirebaseAuth.getInstance();

        int displayHeight = getWindowManager().getDefaultDisplay().getHeight();
        cvi.getLayoutParams().height = displayHeight / 3;
        if (ContextCompat.checkSelfPermission(User_profile.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(User_profile.this, new String[]
                    {android.Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
        }
        cvi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                startActivityForResult(Intent.createChooser(intent, "Select image"), 0);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in= new Intent(User_profile.this,ChangePassword.class);
                startActivity(in);

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 mAuth.signOut();
                Intent i= new Intent(User_profile.this,LoginActivity.class);
                startActivity(i);
            }
        });


        FirebaseAuth mAuth;
        mAuth=FirebaseAuth.getInstance();
        DatabaseReference databaseReference;
        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").exists()){
                    String nameu=dataSnapshot.child("name").getValue().toString();
                    name.setText(nameu);
                }
                if (dataSnapshot.child("email").exists()){
                    String emailu=dataSnapshot.child("email").getValue().toString();
                    Id.setText(emailu);
                }
                if (dataSnapshot.child("phoneNo").exists()){
                    String phnu=dataSnapshot.child("phoneNo").getValue().toString();
                    mobile.setText(phnu);
                }
                if (dataSnapshot.child("vehicle Type").exists()){
                    String vehiu=dataSnapshot.child("vehicle Type").getValue().toString();
                    vType.setText(vehiu);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK && data == null && data != null && data.getData() != null)
            ;
        {

            Uri uri = data.getData();
            try {/*
                Uri uri = data.getData();
                InputStream imageStrem = getContentResolver().openInputStream(uri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStrem);
                cvi.setImageBitmap(selectedImage);*/

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                cvi.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.editprofile) {
            cvi.setEnabled(true);
            name.setEnabled(true);
            mobile.setEnabled(true);
            save.setEnabled(true);
            changePassword.setEnabled(true);
            vType.setEnabled(true);

        }
        return true;

    }

    public void edit() {

        String s = name.getText().toString();
        String s2 = mobile.getText().toString();
        String s3 = vType.getText().toString();
        if (s.equals(""));
        {
            name.setError("Enter Name");

            if (s2.length() > 10)
                mobile.setError("Enter Correct Mobile Number");


            s3.equals("");
            vType.setError("Enter Vehicle Type");
            return;
        }

    }
}
