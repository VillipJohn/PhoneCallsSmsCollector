package com.villip.phonecallssmscollector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by villip on 08.05.2017.
 */

public class ListCallSmsActivity extends AppCompatActivity {
    public FirebaseAuth mAuth;
    public DatabaseReference myRef;


    FirebaseUser user = mAuth.getInstance().getCurrentUser();

    public static SoundPool mSoundPool;
    static int mSoundId;

    static int countList;

    public static void onSound(){
        mSoundPool.play(mSoundId, 1, 1, 1, 0, 1);
    }

    public  static class TaskViewHolder extends RecyclerView.ViewHolder{

        TextView mTitleTask;
        Button mDel;
        static int trigger = 1;

        public TaskViewHolder(View itemView) {
            super(itemView);

            onSound();

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

        SharedPreferences sp = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putString("active", "yes");
        e.apply();

        Log.d("LogTag", "ListCallSmsActivity.onCreate");

        //stopService(new Intent(this, NotEmptyListService.class));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .build();
        } else {
            mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }

        mSoundId = mSoundPool.load(this, R.raw.sound2, 1);

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
            protected void populateViewHolder(TaskViewHolder viewHolder, String title,final int position) {
                countList = position;

                viewHolder.mTitleTask.setText(title);
                viewHolder.mDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference itemRef = getRef(position);
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

        SharedPreferences sp = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putString("active", "yes");
        e.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sp = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putString("active", "no");
        e.apply();

        startService(new Intent(this, NotEmptyListService.class));
    }
}

