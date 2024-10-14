package com.example.mobileapptotrackmedicalrecords.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.mobileapptotrackmedicalrecords.R;
import com.example.mobileapptotrackmedicalrecords.dao.DAO;
import com.example.mobileapptotrackmedicalrecords.form.MedicalRecord;
import com.example.mobileapptotrackmedicalrecords.util.Constants;
import com.example.mobileapptotrackmedicalrecords.util.MapUtil;
import com.example.mobileapptotrackmedicalrecords.util.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListMedicalRecord extends AppCompatActivity {

    ListView listView;
    String patientId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_medical_record);

        listView=(ListView) findViewById(R.id.MedicalRecordsList);
        final Session s=new Session(getApplicationContext());

        final Map<String,Object> map=new HashMap<>();
        final Map<String,String> viewMap=new HashMap<String,String>();

        final DAO dao = new DAO();

        if(s.getRole().equals("hospital"))
        {
            Intent i=getIntent();
            savedInstanceState=i.getExtras();
            patientId=savedInstanceState.getString("patientid");
        }
        else
        {
            patientId=s.getusername();
        }

        dao.getDBReference(Constants.MEDICAL_RECORDS_DB).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int j = 1;

                for (DataSnapshot snapshotNode : dataSnapshot.getChildren()) {

                    String key = snapshotNode.getKey();
                    MedicalRecord medicalrecord = snapshotNode.getValue(MedicalRecord.class);

                    if(medicalrecord!=null)
                    {
                        if(medicalrecord.getPatientid().equals(patientId))
                        {
                            viewMap.put(j + "_" + medicalrecord.getName(), key);

                            j++;
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String item = listView.getItemAtPosition(i).toString();
                String id= MapUtil.stringToMap(s.getViewMap()).get(item);

                Intent intent = new Intent(getApplicationContext(), ViewMedicalRecord.class);
                intent.putExtra("medicalrecord", id);
                startActivity(intent);
            }
        });
    }
}
