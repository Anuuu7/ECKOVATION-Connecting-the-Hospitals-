package com.example.mobileapptotrackmedicalrecords.view;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapptotrackmedicalrecords.R;
import com.example.mobileapptotrackmedicalrecords.dao.DAO;
import com.example.mobileapptotrackmedicalrecords.form.Hospital;
import com.example.mobileapptotrackmedicalrecords.util.Constants;
import com.example.mobileapptotrackmedicalrecords.util.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ViewHospital extends AppCompatActivity {

    Button hospitalviewdelete;
    Button hospitalviewback;
    Button hospitalviewupdatestatus;

    TextView t1,t2,t3,t4,t5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_hospital);

        final Session s = new Session(getApplicationContext());

        hospitalviewdelete = (Button) findViewById(R.id.hospitalviewdelete);
        hospitalviewback = (Button) findViewById(R.id.hospitalviewback);
        hospitalviewupdatestatus = (Button) findViewById(R.id.hospitalviewupdatestatus);

        t1 = (TextView) findViewById(R.id.hospitalviewname);
        t2 = (TextView) findViewById(R.id.hospitalviewmobile);
        t3 = (TextView) findViewById(R.id.hospitalviewemail);
        t4 = (TextView) findViewById(R.id.hospitalviewaddress);
        t5 = (TextView) findViewById(R.id.hospitalviewstatus);

        Intent i = getIntent();
        savedInstanceState = i.getExtras();
        final String hospitalid = savedInstanceState.getString("hospitalid");

        if (!s.getRole().equals("admin")) {
            hospitalviewupdatestatus.setEnabled(false);
            hospitalviewdelete.setEnabled(false);
        }

        DAO d = new DAO();
        d.getDBReference(Constants.HOSPITAL_DB).child(hospitalid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Hospital hospital = dataSnapshot.getValue(Hospital.class);

                if (hospital != null) {

                    t1.setText("Name :" + hospital.getName());
                    t2.setText("Mobile :" + hospital.getMobile());
                    t3.setText("Email :" + hospital.getEmail());
                    t4.setText("Address :" + hospital.getAddress());

                    if (s.getRole().equals("admin")) {
                        t5.setText("Status :" + hospital.getStatus());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        hospitalviewdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DAO dao = new DAO();
                dao.deleteObject(Constants.HOSPITAL_DB, hospitalid);

                Intent i = new Intent(getApplicationContext(),AdminHome.class);
                startActivity(i);
            }
        });

        hospitalviewupdatestatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DAO dao = new DAO();
                dao.getDBReference(Constants.HOSPITAL_DB).child(hospitalid).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Hospital hospital = dataSnapshot.getValue(Hospital.class);

                        if (hospital != null) {

                            if(hospital.getStatus().equals("yes"))
                            {
                                hospital.setStatus("no");
                            }
                            else if(hospital.getStatus().equals("no"))
                            {
                                hospital.setStatus("yes");
                            }

                            dao.addObject(Constants.HOSPITAL_DB,hospital,hospital.getUsername());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Intent intent = new Intent(getApplicationContext(),AdminHome.class);
                startActivity(intent);
            }
        });

        hospitalviewback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),AdminHome.class);
                startActivity(i);
            }
        });
    }
}