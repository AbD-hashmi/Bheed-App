package com.example.projects.mainsource;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StationDetails extends AppCompatActivity {

    String uid,address,ot,ct,np;
    EditText add,opt,nof;
    FirebaseAuth mAuth;
    DatabaseReference dataref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_details);


        add = (EditText) findViewById(R.id.name);
        opt = (EditText) findViewById(R.id.smno);
        nof=(EditText)findViewById(R.id.scool);

        add.setEnabled(false);
        opt.setEnabled(false);
        nof.setEnabled(false);

        Bundle b=getIntent().getExtras();
        uid=b.getString("id");

        mAuth= FirebaseAuth.getInstance();
        dataref = FirebaseDatabase.getInstance().getReference("Stations").child(uid);

        dataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.child("Address").exists()) {
                    Toast.makeText(StationDetails.this, "Please fill Station details First", Toast.LENGTH_SHORT).show();
                } else {
                    if (dataSnapshot.child("Address").exists()){
                        address = dataSnapshot.child("Address").getValue().toString();
                    add.setText(address);}

                    if (dataSnapshot.child("Timing").exists()){
                        ot = dataSnapshot.child("Timing").getValue().toString();
                        opt.setText(ot);}

                    if (dataSnapshot.child("No of Pumps").exists()){
                        np=dataSnapshot.child("No of Pumps").getValue().toString();
                        nof.setText(np);
                    }else{
                        nof.setText("4");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(StationDetails.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
