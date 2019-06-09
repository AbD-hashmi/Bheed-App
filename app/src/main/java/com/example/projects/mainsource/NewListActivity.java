package com.example.projects.mainsource;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewListActivity extends AppCompatActivity {
    DatabaseReference databaseReference,mStudentDatabase;
    RecyclerView mStudentList;
    String address,user_key;
    ProgressDialog progressDialog;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);
        setTitle("List of Pumps");

        mStudentList = (RecyclerView)findViewById(R.id.recycle_view);
        mStudentList.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(NewListActivity.this);

        mStudentList.setLayoutManager(mLayoutManager);

        mStudentDatabase= FirebaseDatabase.getInstance().getReference("Stations");
        progressDialog = new ProgressDialog(NewListActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);

        progressDialog=ProgressDialog.show(this,null,"Loading... Please wait.");


    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<PoserClass, StudentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<PoserClass, NewListActivity.StudentViewHolder>(
                PoserClass.class,
                R.layout.pumplist,
                NewListActivity.StudentViewHolder.class,
                mStudentDatabase){
            @Override
            protected void populateViewHolder(final NewListActivity.StudentViewHolder studentviewHolder, PoserClass PoserClass, final int position) {

                final String user_id = getRef(position).getKey();
                final TextView studentname = (TextView) studentviewHolder.mview.findViewById(R.id.student_name);


                mStudentDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child("Address").exists()) {
                            address = dataSnapshot.child("Address").getValue().toString();


                            studentname.setText(address);

                                final String user_key0 = dataSnapshot.child("Sno").getValue().toString();

                           int p= Integer.parseInt(user_key0);
                           p=p-1;
                           final String user_key5=String .valueOf(p);
                            studentviewHolder.mview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(NewListActivity.this, StationDetails.class);
                                    intent.putExtra("id",user_key5);
                                    startActivity(intent);

                                }
                            });

                            progressDialog.dismiss();

                        }else{
                            TextView studentname = (TextView) studentviewHolder.mview.findViewById(R.id.student_name);
                            studentname.setText("No students found");
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(NewListActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };

        mStudentList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder{

        View mview;

        public StudentViewHolder(View itemView) {
            super(itemView);

            mview = itemView;
        }

        public void setValues(String address){

            TextView studentname = (TextView) mview.findViewById(R.id.student_name);
            studentname.setText(address);

        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
