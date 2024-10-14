package com.example.mobileapptotrackmedicalrecords.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapptotrackmedicalrecords.MainActivity;
import com.example.mobileapptotrackmedicalrecords.R;
import com.example.mobileapptotrackmedicalrecords.util.Session;

public class HospitalHome extends AppCompatActivity {

    Button b1,b2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_home);

        final Session s = new Session(getApplicationContext());

        b1 = (Button) findViewById(R.id.hospitalsearchpatient);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ListPatient.class);
                startActivity(i);
            }
        });

        b2 = (Button) findViewById(R.id.hospitallogout);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s.loggingOut();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }
}
