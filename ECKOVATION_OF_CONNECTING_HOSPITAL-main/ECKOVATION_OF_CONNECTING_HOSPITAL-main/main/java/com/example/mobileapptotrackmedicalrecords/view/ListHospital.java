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
import com.example.mobileapptotrackmedicalrecords.form.Hospital;
import com.example.mobileapptotrackmedicalrecords.util.Constants;
import com.example.mobileapptotrackmedicalrecords.util.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListHospital extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.mobileapptotrackmedicalrecords.R.layout.activity_list_hospital);

        listView=(ListView) findViewById(R.id.HospitalList);
        final Session s = new Session(getApplicationContext());

        final DAO dao=new DAO();
        dao.getDBReference(Constants.HOSPITAL_DB).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<String> al=new ArrayList<String>();

                for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {

                    Hospital hospital = (Hospital) snapshotNode.getValue(Hospital.class);

                    if(hospital!=null)
                    {
                        if (s.getRole().equals("admin")) {
                            al.add(hospital.getUsername());
                        }
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(listView.getContext(),
                        android.R.layout.simple_list_item_1, (al.toArray(new String[al.size()])));

                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String hospital = listView.getItemAtPosition(position).toString();

                Intent intent=new Intent(getApplicationContext(),ViewHospital.class);
                intent.putExtra("hospitalid",hospital);
                startActivity(intent);
            }
        });
    }
}