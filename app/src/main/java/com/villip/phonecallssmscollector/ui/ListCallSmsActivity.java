package com.villip.phonecallssmscollector.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.villip.phonecallssmscollector.service.NotEmptyListService;
import com.villip.phonecallssmscollector.R;

/**
 * Created by villip on 08.05.2017.
 */

public class ListCallSmsActivity extends AppCompatActivity {
    public FirebaseAuth mAuth;
    public DatabaseReference myRef;

    ChildEventListener mChildEventListener;

    SharedPreferences mySharedPreferences;
    String soundCheck;

    FirebaseUser user = mAuth.getInstance().getCurrentUser();

    public  static class TaskViewHolder extends RecyclerView.ViewHolder{
        TextView mTitleTask;
        Button mDel;
        static int trigger = 1;

        public TaskViewHolder(View itemView) {
            super(itemView);
            mTitleTask = (TextView) itemView.findViewById(R.id.tv_title_task);
            mDel = (Button) itemView.findViewById(R.id.btn_del);

            if(trigger % 2 == 0){
                mTitleTask.setBackgroundResource(android.R.color.holo_orange_light);
            }

            trigger++;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_calls);

        myRef = FirebaseDatabase.getInstance().getReference();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_list_tasks);

        FirebaseRecyclerAdapter<String,TaskViewHolder> adapter;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new FirebaseRecyclerAdapter<String, TaskViewHolder>(
                String.class,
                R.layout.call_layout,
                TaskViewHolder.class,
                myRef.child(user.getUid()).child("Tasks")
        ) {
            @Override
            protected void populateViewHolder(final TaskViewHolder viewHolder, String title,final int position) {

                viewHolder.mTitleTask.setText(title);
                viewHolder.mDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference itemRef = getRef(viewHolder.getAdapterPosition());
                        itemRef.removeValue();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mySharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        soundCheck = mySharedPreferences.getString("soundCheck", null);

        if (mChildEventListener == null && (soundCheck != null && soundCheck.equals("yes"))){
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sound2);
                    mediaPlayer.start();
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            };

            myRef.child(user.getUid()).child("Tasks").addChildEventListener(mChildEventListener);
        }
    }

    @Override
    protected void onPause() {
        if (mChildEventListener != null) {
            myRef.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if(soundCheck != null && soundCheck.equals("yes")){
            startService(new Intent(this, NotEmptyListService.class));
        }
        super.onStop();
    }
}

