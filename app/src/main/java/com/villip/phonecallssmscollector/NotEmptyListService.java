package com.villip.phonecallssmscollector;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class NotEmptyListService extends Service {
    FirebaseAuth mAuth;

    FirebaseUser user = mAuth.getInstance().getCurrentUser();

    public NotEmptyListService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final int fStartId = startId;

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

// Attach a listener to read the data at our posts reference
        myRef.child(user.getUid()).child("Tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    /*String forLog = dataSnapshot.getValue(String.class);
                    Log.d("LogTag", "dataSnapshot  -  " + forLog);*/

                    SharedPreferences sp = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    String activeActivity = sp.getString("active", null);

                    if(activeActivity != null && activeActivity.equals("no")){
                        Log.d("LogTag", "NptEmptyListService.onStartCommand.onDataChange");
                        /*Intent intent= new Intent(getApplicationContext(), ListCallSmsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);*/
                        stopSelf();
                    }
                }
                //Post post = dataSnapshot.getValue(Post.class);
                //System.out.println(post);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent intent= new Intent(getApplicationContext(), ListCallSmsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }
}
