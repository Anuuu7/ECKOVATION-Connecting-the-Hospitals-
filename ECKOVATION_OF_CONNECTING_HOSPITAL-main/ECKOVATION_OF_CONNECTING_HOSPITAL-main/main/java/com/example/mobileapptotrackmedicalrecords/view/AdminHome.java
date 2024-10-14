package com.example.mobileapptotrackmedicalrecords.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapptotrackmedicalrecords.MainActivity;
import com.example.mobileapptotrackmedicalrecords.R;
import com.example.mobileapptotrackmedicalrecords.util.Session;

public class AdminHome extends AppCompatActivity {

    Button b1,b2;
    Button addPolice;
    Button viewUsers;
    Button adminLogout;
    Button addHospital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        final Session s = new Session(getApplicationContext());

        addPolice=(Button) findViewById(R.id.addpolice);
        viewUsers=(Button) findViewById(R.id.adminviewusers);
        adminLogout=(Button) findViewById(R.id.adminlogout);
        addHospital=(Button) findViewById(R.id.addhospital);

        b1 = (Button) findViewById(R.id.adminlisthospitals);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ListHospital.class);
                startActivity(i);
            }
        });

        b2 = (Button) findViewById(R.id.adminlogout);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s.loggingOut();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });

        addHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddHospital.class);
                startActivity(i);
            }
        });

        addPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddPolice.class);
                startActivity(i);
            }
        });

        viewUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.v("in list view action ","");
                Intent i = new Intent(getApplicationContext(),AdminListUser.class);
                startActivity(i);
            }
        });
    }
}
