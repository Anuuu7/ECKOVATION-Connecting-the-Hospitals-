package com.example.mobileapptotrackmedicalrecords.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobileapptotrackmedicalrecords.R;
import com.example.mobileapptotrackmedicalrecords.dao.DAO;
import com.example.mobileapptotrackmedicalrecords.form.Hospital;
import com.example.mobileapptotrackmedicalrecords.util.Constants;
import com.example.mobileapptotrackmedicalrecords.util.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class HospitalLogin extends AppCompatActivity {

    private Session session;
    EditText e1,e2;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());

        setContentView(R.layout.activity_hospital_login);

        e1=(EditText)findViewById(R.id.hospitalusername);
        e2=(EditText)findViewById(R.id.hospitalpassword);
        b1=(Button)findViewById(R.id.hospitalloginsubmit);

        b1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final String username=e1.getText().toString();
                final String password=e2.getText().toString();

                if(username==null|| password==null || username.length()<=0|| password.length()<=0)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter UserName and Password", Toast.LENGTH_SHORT).show();
                }
                else {

                    DAO d = new DAO();
                    d.getDBReference(Constants.HOSPITAL_DB).child(username).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Hospital hospital = (Hospital) dataSnapshot.getValue(Hospital.class);

                            if (hospital == null) {
                                Toast.makeText(getApplicationContext(), "Invalid User Name ", Toast.LENGTH_SHORT).show();
                            } else if (hospital != null && hospital.getPassword().equals(password)) {

                                if (hospital.getStatus().equals("yes")) {

                                    session.setusername(username);
                                    session.setRole("hospital");

                                    Intent i = new Intent(getApplicationContext(),HospitalHome.class);
                                    startActivity(i);

                                } else {
                                    Toast.makeText(getApplicationContext(), "your Account is Not yet Activated", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "In valid Password", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
}