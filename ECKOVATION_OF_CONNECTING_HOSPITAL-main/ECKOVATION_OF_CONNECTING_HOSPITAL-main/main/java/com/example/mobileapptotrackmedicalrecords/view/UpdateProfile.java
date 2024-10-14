package com.example.mobileapptotrackmedicalrecords.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.mobileapptotrackmedicalrecords.R;
import com.example.mobileapptotrackmedicalrecords.dao.DAO;
import com.example.mobileapptotrackmedicalrecords.form.Patient;
import com.example.mobileapptotrackmedicalrecords.util.Constants;
import com.example.mobileapptotrackmedicalrecords.util.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

import android.widget.RadioButton;
import android.widget.RadioGroup;

public class UpdateProfile extends AppCompatActivity {

    EditText e3,e4,e7,e8,e9;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update_profile);

        e3=(EditText)findViewById(R.id.updatePatientEmail);
        e4=(EditText)findViewById(R.id.updatePatientMobile);
        e7=(EditText)findViewById(R.id.updatePatientaddress);
        e8=(EditText)findViewById(R.id.updatePatientPass);
        e9=(EditText)findViewById(R.id.updatePatientConPass);

        final Session s = new Session(getApplicationContext());

        b1=(Button)findViewById(R.id.updateProfileButton);

        DAO d=new DAO();
        d.getDBReference(Constants.PATIENT_DB).child(s.getusername()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Patient patient=dataSnapshot.getValue(Patient.class);

                if(patient!=null)
                {
                    e3.setText(patient.getEmail());
                    e4.setText(patient.getMobile());
                    e7.setText(patient.getAddress());
                    e8.setText(patient.getPassword());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String patientmail=e3.getText().toString();
                String patientmobile=e4.getText().toString();
                String address=e7.getText().toString();
                String patientpass=e8.getText().toString();
                String patientconpass=e9.getText().toString();

                if(patientmail==null || patientmobile==null || address==null|| patientpass==null
                        || patientconpass==null)
                {
                    Toast.makeText(getApplicationContext(),"Missing Inputs",Toast.LENGTH_SHORT).show();
                }
                else if(patientmail.equals("") || patientmobile.equals("")
                        || address.equals("") || patientpass.equals("")
                        || patientconpass.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Missing Inputs",Toast.LENGTH_SHORT).show();
                }
                else if(patientmobile.length()<10||patientmobile.length()>12)
                {
                    Toast.makeText(getApplicationContext(),"Invalid Mobile",Toast.LENGTH_SHORT).show();
                }
                else if(!patientpass.equals(patientconpass))
                {
                    Toast.makeText(getApplicationContext(),"Password Mismatch",Toast.LENGTH_SHORT).show();
                }
                else if (!isValidEmail(patientmail)) {
                    Toast.makeText(getApplicationContext(),"Enter Valid Email",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DAO d=new DAO();
                    d.getDBReference(Constants.PATIENT_DB).child(s.getusername()).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Patient patient=dataSnapshot.getValue(Patient.class);

                            if(patient!=null) {

                                patient.setEmail(patientmail);
                                patient.setMobile(patientmobile);
                                patient.setAddress(address);
                                patient.setPassword(patientpass);

                                new DAO().addObject(Constants.PATIENT_DB,patient,patient.getUsername());

                                Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();

                                Intent i = new Intent(getApplicationContext(),PatientHome.class);
                                startActivity(i);
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

    public static boolean isValidEmail(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}