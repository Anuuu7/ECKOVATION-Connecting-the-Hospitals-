package com.example.mobileapptotrackmedicalrecords.view;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mobileapptotrackmedicalrecords.R;
import com.example.mobileapptotrackmedicalrecords.dao.DAO;
import com.example.mobileapptotrackmedicalrecords.form.User;
import com.example.mobileapptotrackmedicalrecords.util.Constants;
import com.example.mobileapptotrackmedicalrecords.util.MapUtil;
import com.example.mobileapptotrackmedicalrecords.util.Session;

public class AdminListUser extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list_user);

        listView=(ListView) findViewById(R.id.AdminUsersList);

        DAO dao=new DAO();
        dao.setDataToAdapterList(listView,User.class, Constants.USER_DB,"all");

        final Session s=new Session(getApplicationContext());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.v("in list action perform ","in list action perform");

                String item = listView.getItemAtPosition(i).toString();
                item= MapUtil.stringToMap(s.getViewMap()).get(item);

                Intent intent= new Intent(getApplicationContext(),ViewUser.class);;
                intent.putExtra("userid",item);
                startActivity(intent);
            }
        });
    }
}
