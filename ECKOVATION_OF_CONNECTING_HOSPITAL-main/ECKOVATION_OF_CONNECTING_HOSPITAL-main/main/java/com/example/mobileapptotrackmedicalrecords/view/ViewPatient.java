package com.example.mobileapptotrackmedicalrecords.view;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.example.mobileapptotrackmedicalrecords.R;
import com.example.mobileapptotrackmedicalrecords.dao.DAO;
import com.example.mobileapptotrackmedicalrecords.form.Patient;
import com.example.mobileapptotrackmedicalrecords.util.Constants;
import com.example.mobileapptotrackmedicalrecords.util.Session;

public class ViewPatient extends AppCompatActivity {

    TextView t1,t2,t3,t4;
    Button cancel;
    Button addmedicalrecord;
    Button listmedicalrecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient);

        cancel=(Button) findViewById(R.id.viewPatientCanel);
        addmedicalrecord=(Button) findViewById(R.id.viewPatientAddMedicalRecord);
        listmedicalrecord=(Button) findViewById(R.id.viewPatientListMedicalRecords);

        final Session session=new Session(getApplicationContext());
        final String role=session.getRole();

        if(!role.equals("hospital")) {
            addmedicalrecord.setEnabled(false);
        }

        Log.v("in view patient role ",role);

        Intent i=getIntent();
        savedInstanceState=i.getExtras();

        final String patientId=savedInstanceState.getString("patient");

        t1=(TextView) findViewById(R.id.textviewname);
        t2=(TextView)findViewById(R.id.textviewemail);
        t3=(TextView)findViewById(R.id.textviewmobile);
        t4=(TextView)findViewById(R.id.textviewaddress);

        DAO d=new DAO();
        d.getDBReference(Constants.PATIENT_DB).child(patientId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Patient patient=dataSnapshot.getValue(Patient.class);

                if(patient!=null)
                {
                    t1.setText("Patient Name : "+patient.getName());
                    t2.setText("Email: " + patient.getEmail());
                    t3.setText("Mobile: " + patient.getMobile());
                    t4.setText("Address: " + patient.getAadhaarnumber());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(role.equals("hospital")) {

                    Intent i = new Intent(getApplicationContext(), HospitalHome.class);
                    startActivity(i);
                }
            }
        });

        addmedicalrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),AddMedicalRecord.class);
                i.putExtra("patientid",patientId);
                startActivity(i);
            }
        });

        listmedicalrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),ListMedicalRecord.class);
                i.putExtra("patientid",patientId);
                startActivity(i);
            }
        });
    }
}
