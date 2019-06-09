package com.example.projects.mainsource;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by utkar on 15-03-2018.
 */

public class StationsAdapter extends RecyclerView.Adapter<StationsAdapter.StationsViewHolder> {

         private String[] data;
         public StationsAdapter(String[] data)
         {
            this.data = data;
         }
    @Override
    public StationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item,parent,false);
        return new StationsViewHolder(view);

    }

    @Override
    public void onBindViewHolder(StationsViewHolder holder, int position) {

             String title = data[position];
             holder.textView.setText(title);

    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class StationsViewHolder extends RecyclerView.ViewHolder
    {
         TextView textView;
         TextView textView1;
        public StationsViewHolder(View itemView) {

            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text1);
            textView1 = (TextView) itemView.findViewById(R.id.test);

        }
    }
}
