package com.example.sneha.latlong.Activity;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sneha.latlong.Navigation;
import com.example.sneha.latlong.Service.AppLocationService;
import com.example.sneha.latlong.Service.LocationAddress;
import com.example.sneha.latlong.R;
import com.example.sneha.latlong.Util.Api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//import static com.example.sneha.latlong.Activity.Sign_Up.userID;

class latlongdetails {

    public double lat;
    public double longi;


    public latlongdetails() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public latlongdetails(double lat, double longi) {
        this.lat = lat;
        this.longi=longi;
    }

}

public class MyActivity extends Activity {

    String userID;
    public static String name;
    Button logoff,dust,n,forum;
    Button btnShowAddress;
    DatabaseReference mDataRef;
    TextView tvAddress,profile;
    public static int flag = 1;
    AppLocationService appLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        retro();

        appLocationService = new AppLocationService(
                MyActivity.this);

        final ArrayList<latlongdetails> ll = new ArrayList<latlongdetails>();

        if(ContextCompat.checkSelfPermission(MyActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
            requestPermissionforLoc();

        profile=(TextView)findViewById(R.id.Profile);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        logoff=(Button)findViewById(R.id.Logout);
        forum =(Button)findViewById(R.id.openforum);

        mDataRef=FirebaseDatabase.getInstance().getReference();
        mDataRef.child("LATLONG").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                Log.e("seqid",String.valueOf(map));

                for(String x : map.keySet())
                {

                    Map<Double,Double> y = (Map<Double, Double>) map.get(x);
                    Log.e("seqid",String.valueOf(y.get("lat")));
                    Log.e("seqid",String.valueOf(y.get("longi")));
                    latlongdetails a = new latlongdetails(y.get("lat"),y.get("longi"));
                    ll.add(a);
                }

                for(latlongdetails y: ll)
                {
                    Log.e("lat",String.valueOf(y.lat));
                    Log.e("long",String.valueOf(y.longi));
                }
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



        btnShowAddress = (Button) findViewById(R.id.btnShowAddress);
        btnShowAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (ContextCompat.checkSelfPermission(MyActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    requestPermissionforLoc();
                else{
                Location location = appLocationService
                        .getLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(latitude, longitude,
                            getApplicationContext(), new GeocoderHandler());
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
                if(ActivityCompat.shouldShowRequestPermissionRationale(MyActivity.this,Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    new android.support.v7.app.AlertDialog.Builder(MyActivity.this)
                            .setTitle("Permission Request")
                            .setMessage("Enable location services")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(MyActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},flag);

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
                    ActivityCompat.requestPermissions(MyActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},flag);
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

        logoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
              //logout
                FirebaseAuth.getInstance().signOut();
                startActivity(intent);
                finish();
            }
        });

    }

    private void retro() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://latlong-c835f.firebaseio.com/")//url of firebase app
                .addConverterFactory(GsonConverterFactory.create())//use for convert JSON file into object
                .build();
        Api api = retrofit.create(Api.class);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            userID = user.getUid();
            Log.d("Retro", userID);


            String url = "https://latlong-c835f.firebaseio.com/users/" + userID + ".json";
            Call<Users_new> call2 = api.getData(url);

            call2.enqueue(new Callback<Users_new>() {
                @Override
                public void onResponse(Call<Users_new> call, Response<Users_new> response) {
                    Log.e("SuccessRetro", "Success");
                    if (response.body() != null) {

                        Log.e("SuccessRetroContact", String.valueOf(response));


                        Log.e("SuccessRetroContact", response.body().getContact());

                        Log.e("SuccessRetroName", response.body().getUsername());

                        Log.e("SuccessRetroCountry", response.body().getCountry());

                        Log.e("SuccessRetroEmail", response.body().getEmail());

                        profile.setText("Profile:\nUsername: " + response.body().getUsername() +
                                "\nEmail: " + response.body().getEmail() + "\nContact: " + response.body().getContact() +
                                "\nCountry: " + response.body().getCountry());
                    }
                }

                @Override
                public void onFailure(Call<Users_new> call, Throwable t) {
                    Log.e("FailureRetro", "Failure");
                    Toast.makeText(getApplicationContext(), "Connect to internet to load profile", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void requestPermissionforLoc() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(MyActivity.this,Manifest.permission.ACCESS_FINE_LOCATION))
        {
            new android.support.v7.app.AlertDialog.Builder(MyActivity.this)
                    .setTitle("Permission Request")
                    .setMessage("Enable location services")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MyActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},flag);

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
            ActivityCompat.requestPermissions(MyActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},flag);
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MyActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MyActivity.this.startActivity(intent);
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

    public class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            tvAddress.setText(locationAddress);
        }
    }

}
