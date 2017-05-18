package com.villip.phonecallssmscollector.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by villip on 08.05.2017.
 */

public class CallReceiver extends BroadcastReceiver {
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    FirebaseUser user = mAuth.getInstance().getCurrentUser();


    @Override
    public void onReceive(Context context, Intent intent) {
        myRef = FirebaseDatabase.getInstance().getReference();

        Log.i("Log_tag", "onReceive is working");

        try {
            System.out.println("Receiver start");

            //String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            Log.i("Log_tag", state);

            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                Date date = new Date();
                SimpleDateFormat sdfr = new SimpleDateFormat("HH:mm");
                String callStartTime = sdfr.format(date);



                try {
                    SharedPreferences sp = context.getSharedPreferences("MyPrefs", context.MODE_PRIVATE);
                    //sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    String phoneName = sp.getString("phoneName", null);

                    String result = phoneName + "   " + number + "    " + callStartTime;

                    myRef.child(
                            user.getUid()).child("Tasks").
                            push().
                            setValue(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

}
