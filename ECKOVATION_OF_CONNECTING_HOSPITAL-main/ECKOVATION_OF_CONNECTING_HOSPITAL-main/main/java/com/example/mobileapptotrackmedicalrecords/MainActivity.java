package com.example.mobileapptotrackmedicalrecords;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapptotrackmedicalrecords.view.AdminLogin;
import com.example.mobileapptotrackmedicalrecords.view.HospitalLogin;
import com.example.mobileapptotrackmedicalrecords.view.HospitalRegistration;
import com.example.mobileapptotrackmedicalrecords.view.PatientLogin;
import com.example.mobileapptotrackmedicalrecords.view.PatientRegistration;

public class MainActivity extends AppCompatActivity {

    Button b1,b2,b3,b4,b5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = (Button) findViewById(R.id.adminlogin);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AdminLogin.class);
                startActivity(i);
            }
        });

        b2 = (Button) findViewById(R.id.patientregistration);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PatientRegistration.class);
                startActivity(i);
            }
        });

        b3 = (Button) findViewById(R.id.patientlogin);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PatientLogin.class);
                startActivity(i);
            }
        });

        b4= (Button) findViewById(R.id.hospitalregistration);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), HospitalRegistration.class);
                startActivity(i);
            }
        });

        b5= (Button) findViewById(R.id.hospitallogin);
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), HospitalLogin.class);
                startActivity(i);
            }
        });
    }
}
