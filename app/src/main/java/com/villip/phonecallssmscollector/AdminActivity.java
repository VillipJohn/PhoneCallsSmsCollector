package com.villip.phonecallssmscollector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText etAdminPassword;
    private Button ok;

    SharedPreferences mySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        etAdminPassword = (EditText) findViewById(R.id.etAdminPassword);

        ok =(Button) findViewById(R.id.btnOkAdmin);

        ok.setOnClickListener(this);

        mySharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String adminPassword = mySharedPreferences.getString("adminPassword", null);
        if(adminPassword != null && adminPassword.equals("12345678")){
            Intent intent = new Intent(this, ListCallSmsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnOkAdmin){
            String password = etAdminPassword.getText().toString();

            SharedPreferences.Editor e = mySharedPreferences.edit();
            e.putString("adminPassword", password);
            e.apply();
            if(password.equals("12345678")){
                Intent intent = new Intent(AdminActivity.this, ListCallSmsActivity.class);
                startActivity(intent);
            }
        }
    }
}
