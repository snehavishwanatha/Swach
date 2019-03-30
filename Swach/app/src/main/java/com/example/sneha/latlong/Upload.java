package com.example.sneha.latlong;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sneha.latlong.R;
import com.example.sneha.latlong.Service.AppLocationService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

class latlongdetail {

    public double lat;
    public double longi;


    public latlongdetail() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public latlongdetail(double lat, double longi) {
        this.lat = lat;
        this.longi=longi;
    }

}

public class Upload extends AppCompatActivity {

    AppLocationService appLocationService;
    Button upload,dustbin;
    CheckBox checkbox;
    double seqid;
    final int flag = 1;
    double latitude,longitude;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        appLocationService = new AppLocationService(
                Upload.this);

        final ImageButton img = (ImageButton)findViewById(R.id.imageButton);
        checkbox=(CheckBox)findViewById(R.id.checkBox);


        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkbox.isChecked())
                    img.setVisibility(View.VISIBLE);
                else img.setVisibility(View.GONE);//TO SHOW THE BUTTON
            }
        });



        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (ContextCompat.checkSelfPermission(Upload.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    requestPermissionforLoc();
                else{
                    Location location = appLocationService
                            .getLocation(LocationManager.GPS_PROVIDER);
                    mDatabase = FirebaseDatabase.getInstance().getReference();

                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        latlongdetail lld= new latlongdetail(latitude,longitude);

                        String s1 = String.valueOf(latitude);
                        s1 = s1.replace(".","LL");
                        String s2 = String.valueOf(longitude);
                        s2 = s2.replace(".","LL");

                        mDatabase.child("LATLONG").child("custom"+s1+s2).setValue(lld);

                        Toast.makeText(Upload.this," Upload successful! Thank you! #swachBharat ",Toast.LENGTH_SHORT);
                        checkbox.setChecked(false);



                    } else {
                        if (!isLocationEnabled())
                            showSettingsAlert();
                        else {
                            Toast.makeText(getApplicationContext(), "Check internet connection and try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }



            private void requestPermissionforLoc() {
                if(ActivityCompat.shouldShowRequestPermissionRationale(Upload.this, android.Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    new android.support.v7.app.AlertDialog.Builder(Upload.this)
                            .setTitle("Permission Request")
                            .setMessage("Enable location services")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(Upload.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},flag);

                                }
                            })
                            .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();

                }
                else
                {
                    ActivityCompat.requestPermissions(Upload.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},flag);
                }
            }

            private boolean isLocationEnabled() {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    //All location services are enabled
                    return true;
                }
                return false;

            }

        });





    }
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                Upload.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        Upload.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

}

