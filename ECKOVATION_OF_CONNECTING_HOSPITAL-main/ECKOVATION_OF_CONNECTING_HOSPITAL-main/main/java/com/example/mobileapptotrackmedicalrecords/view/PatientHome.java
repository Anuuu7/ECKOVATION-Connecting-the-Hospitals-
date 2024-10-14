package com.example.mobileapptotrackmedicalrecords.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapptotrackmedicalrecords.AppConstants;
import com.example.mobileapptotrackmedicalrecords.GpsUtils;
import com.example.mobileapptotrackmedicalrecords.MainActivity;
import com.example.mobileapptotrackmedicalrecords.R;
import com.example.mobileapptotrackmedicalrecords.util.Session;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.example.mobileapptotrackmedicalrecords.dao.DAO;
import com.example.mobileapptotrackmedicalrecords.form.Family;
import com.example.mobileapptotrackmedicalrecords.form.User;
import com.example.mobileapptotrackmedicalrecords.util.Constants;

public class PatientHome extends AppCompatActivity implements SensorEventListener
{
    Session session;

    Button b11,b12,b13;

    Button b3,b5,b6,b7;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

    private FusedLocationProviderClient mFusedLocationClient;

    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private String txtLocation;

    private boolean isGPS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_patient_home);
        session=new Session(getApplicationContext());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(getApplicationContext(),"dont have accelerometer sensor",Toast.LENGTH_LONG).show();
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();

                        txtLocation=wayLatitude+","+wayLongitude;

                        if (mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };


        final Session s = new Session(getApplicationContext());

        b11 = (Button) findViewById(R.id.patientlistmedicalrecords);
        b11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ListMedicalRecord.class);
                startActivity(i);
            }
        });

        b12 = (Button) findViewById(R.id.patientupdateprofile);
        b12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),UpdateProfile.class);
                startActivity(i);
            }
        });

        b13 = (Button) findViewById(R.id.userlogout);
        b13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s.loggingOut();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });


        //-------------------------------------------------------------------------------
        b3 = (Button) findViewById(R.id.emergencyalert);
        b5 = (Button) findViewById(R.id.userviewusers);
        b6 = (Button) findViewById(R.id.usereditfamily);
        b7 = (Button) findViewById(R.id.useraddfamily);
        Button link = (Button) findViewById(R.id.videolink);

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(),"in function", Toast.LENGTH_SHORT).show();

                if (!isGPS) {
                    Toast.makeText(getApplicationContext(), "Please turn on GPS", Toast.LENGTH_SHORT).show();
                    return;
                }

                getLocation();

                if(txtLocation!=null)
                {
                    Toast.makeText(getApplicationContext(),"location not null", Toast.LENGTH_SHORT).show();

                    final String[] userLatLongs=txtLocation.split(",");

                    DAO d=new DAO();
                    d.getDBReference(Constants.FAMILY_DB).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {

                                final Family family=snapshotNode.getValue(Family.class);

                                if(family!=null && family.getUserName().equals(session.getusername())) {

                                    final Set<String> senders = new HashSet<>();

                                    senders.add(family.getMobile1());
                                    senders.add(family.getMobile2());
                                    senders.add(family.getMobile3());

                                    Toast.makeText(getApplicationContext(), "Family Added",
                                            Toast.LENGTH_LONG).show();

                                    DAO d=new DAO();
                                    d.getDBReference(Constants.USER_DB).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {

                                                User user=(User)snapshotNode.getValue(User.class);

                                                if(user!=null)
                                                {
                                                    Log.v("user info :",user.toString());

                                                    if(!user.getType().equals("user")) {

                                                        String[] latLongs=user.getAddress().split(",");

                                                        float distance = getDistanceFromCurrentPosition(new Double(userLatLongs[0]), new Double(userLatLongs[1]), new Double(latLongs[0]), new Double(latLongs[1]));

                                                        if(distance<10000)
                                                        {
                                                            senders.add(user.getMobile());
                                                        }
                                                    }
                                                }

                                                Toast.makeText(getApplicationContext(), "Nearest Added",
                                                        Toast.LENGTH_LONG).show();
                                            }

                                            Toast.makeText(getApplicationContext(), "Count"+senders.size(),
                                                    Toast.LENGTH_LONG).show();

                                            Intent intent=new Intent(getApplicationContext(),PatientHome.class);
                                            PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

                                            ArrayList<PendingIntent> pendingIntents=new ArrayList<PendingIntent>();
                                            pendingIntents.add(pi);

                                            //Get the SmsManager instance and call the sendTextMessage method to send message
                                            SmsManager sms=SmsManager.getDefault();

                                            for(String mobile : senders)
                                            {
                                                ArrayList<String> parts = sms.divideMessage(session.getusername()+" is in Emergency His Father Number is "+family.getMobile1()+" and is blood group is "+session.getbloodgroup()+" https://maps.google.com/?q="+userLatLongs[0]+","+userLatLongs[1]);
                                                //smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                                                sms.sendMultipartTextMessage(mobile, null, parts,
                                                        pendingIntents, null);

                                                Toast.makeText(getApplicationContext(), "Message Sent successfully!",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Location Null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("going to list users :","");
                Intent i = new Intent(getApplicationContext(),ListUsers.class);
                startActivity(i);
            }
        });

        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("going to list users :","");
                Intent i = new Intent(getApplicationContext(), EditFamily.class);
                startActivity(i);
            }
        });

        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("going to list users :","");
                Intent i = new Intent(getApplicationContext(), AddFamily.class);
                startActivity(i);
            }
        });

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("going to list users :","");
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=T7aNSRoDCmg"));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(i);
            }
        });
    }

    public static float getDistanceFromCurrentPosition(double lat1,double lng1, double lat2, double lng2)
    {
        double earthRadius = 3958.75;

        double dLat = Math.toRadians(lat2 - lat1);

        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;

        int meterConversion = 1609;

        return new Float(dist * meterConversion).floatValue();

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(PatientHome.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(PatientHome.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PatientHome.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);
        } else {

            mFusedLocationClient.getLastLocation().addOnSuccessListener(PatientHome.this, new OnSuccessListener<Location>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        txtLocation=wayLatitude+","+wayLongitude;
                    } else {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(PatientHome.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();
                                txtLocation=wayLatitude+","+wayLongitude;
                            } else {
                                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        }
                    });

                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        long curTime = System.currentTimeMillis();

        if ((curTime - lastUpdate) > 100) {
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

            if (speed > SHAKE_THRESHOLD) {

                if (!isGPS) {
                    Toast.makeText(getApplicationContext(), "Please turn on GPS", Toast.LENGTH_SHORT).show();
                    return;
                }

                getLocation();

                if(txtLocation!=null)
                {
                    final String[] userLatLongs=txtLocation.split(",");

                    DAO d=new DAO();
                    d.getDBReference(Constants.FAMILY_DB).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {

                                final Family family=snapshotNode.getValue(Family.class);

                                if(family!=null && family.getUserName().equals(session.getusername())) {

                                    final Set<String> senders = new HashSet<>();

                                    senders.add(family.getMobile1());
                                    senders.add(family.getMobile2());
                                    senders.add(family.getMobile3());

                                    DAO d=new DAO();
                                    d.getDBReference(Constants.USER_DB).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {

                                                User user=(User)snapshotNode.getValue(User.class);

                                                if(user!=null)
                                                {
                                                    Log.v("user info :",user.toString());

                                                    if(!user.getType().equals("user")) {

                                                        String[] latLongs=user.getAddress().split(",");

                                                        float distance = getDistanceFromCurrentPosition(new Double(userLatLongs[0]), new Double(userLatLongs[1]), new Double(latLongs[0]), new Double(latLongs[1]));

                                                        if(distance<10000)
                                                        {
                                                            senders.add(user.getMobile());
                                                        }
                                                    }
                                                }
                                            }

                                            Intent intent=new Intent(getApplicationContext(),PatientHome.class);
                                            PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

                                            ArrayList<PendingIntent> pendingIntents=new ArrayList<PendingIntent>();
                                            pendingIntents.add(pi);

                                            //Get the SmsManager instance and call the sendTextMessage method to send message
                                            SmsManager sms=SmsManager.getDefault();

                                            for(String mobile : senders)
                                            {
                                                ArrayList<String> parts = sms.divideMessage(session.getusername()+" is in Emergency His Father Number is "+family.getMobile1()+" and is blood group is "+session.getbloodgroup()+" https://maps.google.com/?q="+userLatLongs[0]+","+userLatLongs[1]);
                                                //smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                                                sms.sendMultipartTextMessage(mobile, null, parts,
                                                        pendingIntents, null);

                                                Toast.makeText(getApplicationContext(), "Message Sent successfully!",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                Toast.makeText(getApplicationContext(),"accident occured x: "+x+" \n y:"+y+" \n z:"+z+" \n speed:"+speed,Toast.LENGTH_LONG).show();
            }

            last_x = x;
            last_y = y;
            last_z = z;
        }
    }
}
