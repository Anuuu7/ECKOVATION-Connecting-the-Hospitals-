package com.example.mobileapptotrackmedicalrecords.view;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobileapptotrackmedicalrecords.MainActivity;
import com.example.mobileapptotrackmedicalrecords.R;
import com.example.mobileapptotrackmedicalrecords.dao.DAO;
import com.example.mobileapptotrackmedicalrecords.form.Family;
import com.example.mobileapptotrackmedicalrecords.form.User;
import com.example.mobileapptotrackmedicalrecords.util.Constants;
import com.example.mobileapptotrackmedicalrecords.util.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

public class EditFamily extends AppCompatActivity {

    EditText e1,e2,e3;
    Button b1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_family);

        final Session session=new Session(getApplicationContext());

        e1=(EditText)findViewById(R.id.editfamilymobile1);
        e2=(EditText)findViewById(R.id.editfamilymobile2);
        e3=(EditText)findViewById(R.id.editfamilymobile3);

        b1=(Button)findViewById(R.id.editFamilyButton);

        DAO d=new DAO();
        d.getDBReference(Constants.FAMILY_DB).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {

                    final Family family = snapshotNode.getValue(Family.class);
                    String id=snapshotNode.getKey();

                    if(family!=null && family.getUserName().equals(session.getusername())) {

                        e1.setText(family.getMobile1());
                        e2.setText(family.getMobile2());
                        e3.setText(family.getMobile3());

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String m1=e1.getText().toString();
                String m2=e2.getText().toString();
                String m3=e3.getText().toString();

                if(m1==null|| m2==null|| m3==null)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Valid Data",Toast.LENGTH_SHORT).show();
                }
                else if(m1.length()!=10 || m2.length()!=10 || m3.length()!=10) {
                    Toast.makeText(getApplicationContext(), "Invalid Mobile", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DAO d=new DAO();
                    d.getDBReference(Constants.FAMILY_DB).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {

                                final Family family = snapshotNode.getValue(Family.class);
                                String id=snapshotNode.getKey();

                                if(family!=null && family.getUserName().equals(session.getusername())) {

                                    family.setMobile1(m1);
                                    family.setMobile2(m2);
                                    family.setMobile3(m3);

                                    new DAO().addObject(Constants.FAMILY_DB,family,id);

                                    Intent i = new Intent(getApplicationContext(),PatientHome.class);
                                    startActivity(i);

                                    break;
                                }
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
