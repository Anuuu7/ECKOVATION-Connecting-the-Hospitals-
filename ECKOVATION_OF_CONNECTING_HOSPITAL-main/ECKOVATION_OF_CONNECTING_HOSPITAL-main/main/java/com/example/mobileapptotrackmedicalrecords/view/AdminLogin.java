package com.example.mobileapptotrackmedicalrecords.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobileapptotrackmedicalrecords.R;
import com.example.mobileapptotrackmedicalrecords.util.Session;

public class AdminLogin extends AppCompatActivity {

    private Session session;
    EditText e1,e2;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());

        setContentView(R.layout.activity_admin_login);

        e1=(EditText)findViewById(R.id.adminusername);
        e2=(EditText)findViewById(R.id.adminpassword);
        b1=(Button)findViewById(R.id.adminloginsubmit);

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

                    if (username.equals("admin") && password.equals("admin")) {

                        session.setusername(username);
                        session.setRole("admin");

                        Intent i = new Intent(getApplicationContext(),AdminHome.class);
                        startActivity(i);

                    } else {
                        Toast.makeText(getApplicationContext(), "In valid Username or Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}