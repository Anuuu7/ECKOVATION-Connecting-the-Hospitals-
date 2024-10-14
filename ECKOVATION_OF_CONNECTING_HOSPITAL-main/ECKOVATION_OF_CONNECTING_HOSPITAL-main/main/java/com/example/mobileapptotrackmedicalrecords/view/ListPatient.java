package com.example.mobileapptotrackmedicalrecords.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mobileapptotrackmedicalrecords.R;
import com.example.mobileapptotrackmedicalrecords.dao.DAO;
import com.example.mobileapptotrackmedicalrecords.form.Patient;
import com.example.mobileapptotrackmedicalrecords.util.Constants;
import com.example.mobileapptotrackmedicalrecords.util.MapUtil;
import com.example.mobileapptotrackmedicalrecords.util.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ListPatient extends AppCompatActivity {

    EditText e1;
    Button b1;

    ListView listView;

    private final int REQ_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_patient);
        final Session s = new Session(getApplicationContext());

        e1=(EditText)findViewById(R.id.userpatientsearchname);
        listView=(ListView) findViewById(R.id.SearchPatientList);
        ImageView speak = findViewById(R.id.speak);

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        b1=(Button)findViewById(R.id.patientsearchsubmit);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String patientname=e1.getText().toString();

                if(patientname==null)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Patient ID", Toast.LENGTH_SHORT).show();
                }
                else if(patientname.length()==0)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Patient ID",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try
                    {
                        final Map<String,String> viewMap=new HashMap<String,String>();

                        Toast.makeText(getApplicationContext(),"Search:"+patientname,Toast.LENGTH_SHORT).show();

                        final DAO dao=new DAO();
                        dao.getDBReference(Constants.PATIENT_DB).addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                int i=1;

                                for (DataSnapshot snapshotNode : dataSnapshot.getChildren()) {

                                    Patient patient = (Patient) snapshotNode.getValue(Patient.class);

                                    if (patient != null) {

                                        String key=snapshotNode.getKey();

                                        if (patient.getAadhaarnumber().equals(patientname) ||
                                                patient.getName().toLowerCase().contains(patientname.toLowerCase()) ||
                                                patient.getUsername().toLowerCase().contains(patientname.toLowerCase()))
                                        {
                                            viewMap.put(i+")"+patient.getName(),key);
                                            i++;
                                        }
                                    }
                                }

                                ArrayList<String> al = new ArrayList<String>(viewMap.keySet());

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(listView.getContext(),
                                        android.R.layout.simple_list_item_1, (al.toArray(new String[al.size()])));

                                listView.setAdapter(adapter);

                                final Session s = new Session(getApplicationContext());
                                s.setViewMap(MapUtil.mapToString(viewMap));
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String item = listView.getItemAtPosition(i).toString();
                String id= MapUtil.stringToMap(s.getViewMap()).get(item);

                Intent intent = new Intent(getApplicationContext(), ViewPatient.class);
                intent.putExtra("patient", id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    e1.setText(result.get(0));
                }
                break;
            }
        }
    }
}