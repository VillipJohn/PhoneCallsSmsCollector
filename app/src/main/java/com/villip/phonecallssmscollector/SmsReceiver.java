package com.villip.phonecallssmscollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by villip on 08.05.2017.
 */

public class SmsReceiver extends BroadcastReceiver {
    // Get the object of SmsManager
    //final SmsManager sms = SmsManager.getDefault();

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    FirebaseUser user = mAuth.getInstance().getCurrentUser();

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        myRef = FirebaseDatabase.getInstance().getReference();
        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                // get sms objects
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus.length == 0) {
                    return;
                }
                // large message might be broken into many
                SmsMessage[] messages = new SmsMessage[pdus.length];
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());
                }
                String sender = messages[0].getOriginatingAddress();
                String message = sb.toString();

                SharedPreferences sp = context.getSharedPreferences("MyPrefs", context.MODE_PRIVATE);
                String phoneName = sp.getString("phoneName", null);

                String result = phoneName + "  " + message;

                myRef.child(
                        user.getUid()).child("Tasks").
                        push().
                        setValue(result);

                Log.i("SmsReceiver", "message:    " + message);

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                // prevent any other broadcast receivers from receiving broadcast
                // abortBroadcast();
            }
        }
    }
}
