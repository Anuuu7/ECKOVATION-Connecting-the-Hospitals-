package com.example.mobileapptotrackmedicalrecords.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapptotrackmedicalrecords.MainActivity;
import com.example.mobileapptotrackmedicalrecords.R;
import com.example.mobileapptotrackmedicalrecords.dao.DAO;
import com.example.mobileapptotrackmedicalrecords.form.Hospital;
import com.example.mobileapptotrackmedicalrecords.util.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class HospitalRegistration extends AppCompatActivity{

    EditText e1,e2,e3,e4,e5,e6,e7;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hospital_registration);

        e1=(EditText)findViewById(R.id.reghospitalusername);
        e2=(EditText)findViewById(R.id.reghospitalpassword);
        e3=(EditText)findViewById(R.id.reghospitalconformpassword);
        e4=(EditText)findViewById(R.id.reghospitalemail);
        e5=(EditText)findViewById(R.id.reghospitalmobile);
        e6=(EditText)findViewById(R.id.reghospitalname);
        e7=(EditText)findViewById(R.id.reghospitaladdress);

        b1=(Button)findViewById(R.id.reghospitalsubmit);
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

                if(username==null|| password==null|| conformPassword==null|| email==null|| mobile==null|| name==null || address==null)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Valid Data",Toast.LENGTH_SHORT).show();
                }
                else if(username.length()==0|| password.length()==0|| conformPassword.length()==0|| email.length()==0|| mobile.length()==0|| name.length()==0 || address.length()==0)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Valid Data",Toast.LENGTH_SHORT).show();
                }
                else if(mobile.length()!=10)
                {
                    Toast.makeText(getApplicationContext(),"Invalid Mobile",Toast.LENGTH_SHORT).show();
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
                    Hospital hospital=new Hospital();

                    hospital.setUsername(username);
                    hospital.setPassword(password);
                    hospital.setEmail(email);
                    hospital.setMobile(mobile);
                    hospital.setName(name);
                    hospital.setAddress(address);
                    hospital.setStatus("no");

                    try
                    {

                        DAO dao = new DAO();
                        dao.getDBReference(Constants.HOSPITAL_DB).child(username).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Hospital hospital1 = (Hospital) dataSnapshot.getValue(Hospital.class);

                                if (hospital1 == null) {

                                    dao.addObject(Constants.HOSPITAL_DB,hospital,hospital.getUsername());

                                    Toast.makeText(getApplicationContext(),"Register Success",Toast.LENGTH_SHORT).show();

                                    Intent i=new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);

                                }else {
                                    Toast.makeText(getApplicationContext(), "Hospital All ready Registered", Toast.LENGTH_SHORT).show();
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
