package com.example.mobileapptotrackmedicalrecords.view;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.mobileapptotrackmedicalrecords.R;
import com.example.mobileapptotrackmedicalrecords.dao.DAO;
import com.example.mobileapptotrackmedicalrecords.form.Patient;
import com.example.mobileapptotrackmedicalrecords.util.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;
import java.util.regex.Pattern;

public class PatientRegistration extends AppCompatActivity{

    EditText e1,e2,e3,e4,e5,e6,e7,e8,e9;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_patient_registration);

        e1=(EditText)findViewById(R.id.regpatientusername);
        e2=(EditText)findViewById(R.id.regpatientpassword);
        e3=(EditText)findViewById(R.id.regpatientconformpassword);
        e4=(EditText)findViewById(R.id.regpatientemail);
        e5=(EditText)findViewById(R.id.regpatientmobile);
        e6=(EditText)findViewById(R.id.regpatientname);
        e7=(EditText)findViewById(R.id.regpatientaddress);
        e8=(EditText)findViewById(R.id.regpatientaadhaarnumber);
        e9=(EditText)findViewById(R.id.registerBloodGroup);

        b1=(Button)findViewById(R.id.regpatientsubmit);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username=e1.getText().toString();
                String password=e2.getText().toString();
                String conformPassword=e3.getText().toString();
                String email=e4.getText().toString();
                String mobile=e5.getText().toString();
                String name=e6.getText().toString();
                String address=e7.getText().toString();
                String aadhaarnumber=e8.getText().toString();
                String bloodgroup=e9.getText().toString();

                if(username==null|| password==null|| conformPassword==null|| email==null|| mobile==null|| name==null || address==null || aadhaarnumber==null || bloodgroup==null)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Valid Data",Toast.LENGTH_SHORT).show();
                }
                else if(username.length()==0|| password.length()==0|| conformPassword.length()==0|| email.length()==0|| mobile.length()==0|| name.length()==0 || address.length()==0 || aadhaarnumber.length()==0 || bloodgroup.length()==0)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Valid Data",Toast.LENGTH_SHORT).show();
                }
                else if(mobile.length()!=10)
                {
                    Toast.makeText(getApplicationContext(),"Invalid Mobile",Toast.LENGTH_SHORT).show();
                }
                else if(aadhaarnumber.length()!=12)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Valid Aadhar Number",Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(conformPassword))
                {
                    Toast.makeText(getApplicationContext(),"Password Mismatch",Toast.LENGTH_SHORT).show();
                }
                else if (!isValidEmail(email)) {
                    Toast.makeText(getApplicationContext(),"Enter Valid Email",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try
                    {

                        DAO dao = new DAO();
                        dao.getDBReference(Constants.PATIENT_DB).child(username).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Patient patient1 = (Patient) dataSnapshot.getValue(Patient.class);

                                if (patient1 == null) {

                                    String pattern="0123456789";
                                    String otp="";
                                    Random r=new Random();

                                    for(int i=0;i<5;i++)
                                    {
                                        otp=otp+pattern.charAt(r.nextInt(10));
                                    }

                                    Patient patient=new Patient();

                                    patient.setUsername(username);
                                    patient.setPassword(password);
                                    patient.setEmail(email);
                                    patient.setMobile(mobile);
                                    patient.setName(name);
                                    patient.setAddress(address);
                                    patient.setAadhaarnumber(aadhaarnumber);
                                    patient.setOtp(otp);
                                    patient.setBloodgroup(bloodgroup);

                                    dao.addObject(Constants.PATIENT_DB,patient,patient.getUsername());
                                    Toast.makeText(getApplicationContext(),"Register Success",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(),PatientLogin.class);
                                    startActivity(intent);

                                }else {
                                    Toast.makeText(getApplicationContext(), "Patient All ready Registered", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(getApplicationContext(),"Register Error",Toast.LENGTH_SHORT).show();
                        Log.v("User Registration Ex", ex.toString());
                        ex.printStackTrace();
                    }

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
