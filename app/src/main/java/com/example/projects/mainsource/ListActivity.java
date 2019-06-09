package com.example.projects.mainsource;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
        RecyclerView station_list_recycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        station_list_recycler = (RecyclerView) findViewById(R.id.list_stations);
        station_list_recycler.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        String[] place_array = intent.getStringArrayExtra("strings");
        station_list_recycler.setAdapter(new StationsAdapter(place_array));
    }
}

