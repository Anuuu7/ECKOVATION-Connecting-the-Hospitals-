package com.example.mobileapptotrackmedicalrecords.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapptotrackmedicalrecords.R;
import com.example.mobileapptotrackmedicalrecords.dao.DAO;
import com.example.mobileapptotrackmedicalrecords.form.MedicalRecord;
import com.example.mobileapptotrackmedicalrecords.util.Constants;
import com.example.mobileapptotrackmedicalrecords.util.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

public class ViewMedicalRecord extends AppCompatActivity {

    TextView t1,t2,t3,t4;
    ImageView imageView;

    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medical_record);

        back=(Button) findViewById(R.id.viewmedicalrecordback);

        final Session session=new Session(getApplicationContext());

        Intent i=getIntent();
        savedInstanceState=i.getExtras();

        final String medicalrecord=savedInstanceState.getString("medicalrecord");

        t1=(TextView) findViewById(R.id.viewmedicalrecordt1);
        t2=(TextView)findViewById(R.id.viewmedicalrecordt2);
        t3=(TextView) findViewById(R.id.viewmedicalrecordt3);
        t4=(TextView) findViewById(R.id.viewmedicalrecordt4);
        imageView = (ImageView) findViewById(R.id.productviewimage);

        DAO d=new DAO();
        d.getDBReference(Constants.MEDICAL_RECORDS_DB).child(medicalrecord).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                MedicalRecord medicalrecord=dataSnapshot.getValue(MedicalRecord.class);

                if(medicalrecord!=null)
                {
                    t1.setText("Title:"+medicalrecord.getName());
                    t2.setText("Description:"+medicalrecord.getDescription());
                    t3.setText("Date of Treatment:"+medicalrecord.getDate());
                    t4.setText("Hospital:"+medicalrecord.getHospitalid());

                    StorageReference ref = DAO.getStorageReference().child("images/" + medicalrecord.getImage());
                    long ONE_MEGABYTE = 1024 * 1024 *5;
                    ref.getBytes(ONE_MEGABYTE)
                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {

                                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                    if(bm!=null)
                                    {
                                        imageView.setImageBitmap(bm);
                                    }
                                    else
                                    {
                                        Log.v("voidmain ","bm null");
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            Log.v("voidmain ","image reading failure");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String role= session.getRole();

                if(role.equals("hospital"))
                {
                    Intent i=new Intent(getApplicationContext(),HospitalHome.class);
                    startActivity(i);
                }
                else if(role.equals("patient"))
                {
                    Intent i=new Intent(getApplicationContext(),PatientHome.class);
                    startActivity(i);
                }
            }
        });
    }
}
