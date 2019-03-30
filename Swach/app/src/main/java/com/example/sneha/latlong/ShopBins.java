package com.example.sneha.latlong;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


//import com.example.sneha.latlong.Activity.latlongdetails;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class latlongdetaileee {

    public double lat;
    public double longi;


    public latlongdetaileee() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public latlongdetaileee(double lat, double longi) {
        this.lat = lat;
        this.longi=longi;
    }

}
public class ShopBins extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    MyRecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopbins);

        DatabaseReference mDataRef = FirebaseDatabase.getInstance().getReference();
        final ArrayList<latlongdetaileee> ll = new ArrayList<latlongdetaileee>();

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
                    latlongdetaileee a = new latlongdetaileee(y.get("lat"),y.get("longi"));
                    ll.add(a);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Log.e("stry",String.valueOf(ll));
        // set up the RecyclerView
        /*RecyclerView recyclerView = findViewById(R.id.rvAnimals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, ll);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);*/
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
}


