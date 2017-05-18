package com.villip.phonecallssmscollector.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.villip.phonecallssmscollector.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText etStartPassword, etPhoneName;

    SharedPreferences mySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //запрос пермишенов при первом запуске приложения
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE},
                1);

        mySharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        etPhoneName = (EditText)  findViewById(R.id.etPhoneName);
        etStartPassword = (EditText) findViewById(R.id.etAdminPassword);
        Button ok =(Button) findViewById(R.id.btn_ok);

        ok.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String startPassword = mySharedPreferences.getString("startPassword", null);
        if(startPassword != null && startPassword.equals("12345678")){
            Intent intent = new Intent(MainActivity.this, EmailPasswordActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_ok){
            if(!etPhoneName.getText().toString().isEmpty()){
                SharedPreferences.Editor e = mySharedPreferences.edit();
                e.putString("phoneName", etPhoneName.getText().toString());
                e.apply();
            }

            String password = etStartPassword.getText().toString();

            SharedPreferences.Editor e = mySharedPreferences.edit();
            e.putString("startPassword", password);
            e.apply();

            if(password.equals("12345678")){
                Intent intent = new Intent(MainActivity.this, EmailPasswordActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

    }

}

