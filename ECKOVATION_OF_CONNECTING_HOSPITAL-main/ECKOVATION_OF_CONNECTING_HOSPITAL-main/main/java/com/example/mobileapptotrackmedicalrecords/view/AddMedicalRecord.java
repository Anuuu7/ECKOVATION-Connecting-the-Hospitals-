package com.example.mobileapptotrackmedicalrecords.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.widget.ImageView;

import android.app.ProgressDialog;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;

import java.util.UUID;

import com.example.mobileapptotrackmedicalrecords.R;
import com.example.mobileapptotrackmedicalrecords.dao.DAO;
import com.example.mobileapptotrackmedicalrecords.form.MedicalRecord;
import com.example.mobileapptotrackmedicalrecords.util.Constants;
import com.example.mobileapptotrackmedicalrecords.util.Session;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddMedicalRecord extends AppCompatActivity {

    EditText e1,e2;
    Button b1,b2;

    private Uri imageUri;
    int SELECT_PICTURE = 200;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_medical_record);

        final Session s=new Session(getApplicationContext());

        e1=(EditText)findViewById(R.id.addmedicalrecordtitle);
        e2=(EditText)findViewById(R.id.addmedicalrecorddescription);

        b1=(Button)findViewById(R.id.addmedicalrecordButton);

        imageView = (ImageView) findViewById(R.id.medicalrecordImgView);
        b2=(Button)findViewById(R.id.medicalrecorduploadphoto);

        Intent i=getIntent();
        savedInstanceState=i.getExtras();

        final String patientId=savedInstanceState.getString("patientid");

        //==================================================================================

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name=e1.getText().toString();
                final String description=e2.getText().toString();

                if(name==null || description==null) {
                    Toast.makeText(getApplicationContext(), "Please Enter Valid Data", Toast.LENGTH_SHORT).show();
                }
                else if(name.equals("")|| description.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter Valid Data", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String imageName = UUID.randomUUID().toString();

                    MedicalRecord medicalrecord=new MedicalRecord();

                    medicalrecord.setName(name);
                    medicalrecord.setDescription(description);
                    medicalrecord.setDate(new java.util.Date().toString());
                    medicalrecord.setHospitalid(s.getusername());
                    medicalrecord.setPatientid(patientId);
                    medicalrecord.setImage(imageName);
                    medicalrecord.setId(new DAO().getUnicKey(Constants.MEDICAL_RECORDS_DB));

                    DAO dao=new DAO();

                    try
                    {
                        dao.addObject(Constants.MEDICAL_RECORDS_DB,medicalrecord,medicalrecord.getId());
                        uploadImage(imageName);
                        Toast.makeText(getApplicationContext(),"MedicalRecord Added Success",Toast.LENGTH_SHORT).show();

                        Intent i=new Intent(getApplicationContext(), HospitalHome.class);
                        startActivity(i);
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(getApplicationContext(),"MedicalRecord Adding Error",Toast.LENGTH_SHORT).show();
                        Log.v("Adding  Ex", ex.toString());
                        ex.printStackTrace();
                    }

                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });
    }

    void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                imageUri = data.getData();
                if (null != imageUri) {
                    // update the preview image in the layout
                    imageView.setImageURI(imageUri);
                }
            }
        }
    }

    private void uploadImage(String fileName) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference ref = DAO.getStorageReference().child("images/" + fileName);

        if(imageUri!=null)
        {
            Toast.makeText(getApplicationContext(), "While Uploading", Toast.LENGTH_SHORT).show();

            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Image Null", Toast.LENGTH_SHORT).show();
        }
    }
}
