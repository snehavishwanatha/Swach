package com.example.sneha.latlong;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sneha.latlong.Activity.MainActivity;
import com.example.sneha.latlong.Activity.MapsAct;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Navigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public String name;
    private EditText editText, etd;
    private Button button;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        editText = findViewById(R.id.et);
        etd = findViewById(R.id.etd);
        button = findViewById(R.id.btn);
        recyclerView = findViewById(R.id.list);

        final EditText et = (EditText)findViewById(R.id.et);
        final EditText etd = (EditText)findViewById(R.id.etd);
        et.setText(" ");
        etd.setText(" ");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userID = user.getUid();


        DatabaseReference mDataRef= FirebaseDatabase.getInstance().getReference();
        mDataRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                Log.e("user",String.valueOf(map));

                for(String x: map.keySet())
                {
                    if(userID.equals(x)){
                        Map<String,Object> s = (Map<String, Object>)map.get(x);
                        Log.e("name",String.valueOf(s.get("username")));
                        name = String.valueOf(s.get("username"));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("posts").push();
                Map<String, Object> map = new HashMap<>();
                if(editText.getText().toString().equals(" ") || etd.getText().toString().equals(" "))
                {
                    Toast.makeText(Navigation.this, "Enter the fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    map.put("name", name);
                    map.put("title", editText.getText().toString());
                    map.put("desc", etd.getText().toString());
                }
                adapter.notifyDataSetChanged();
                databaseReference.setValue(map);

                et.setText(" ");
                etd.setText(" ");
            }
        });

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        fetch();
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("posts");

        FirebaseRecyclerOptions<Model> options =
                new FirebaseRecyclerOptions.Builder<Model>()
                        .setQuery(query, new SnapshotParser<Model>() {
                            @NonNull
                            @Override
                            public Model parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Model(snapshot.child("name").getValue().toString(),
                                        snapshot.child("title").getValue().toString(),
                                        snapshot.child("desc").getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Model, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_view, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, Model model) {
                holder.setTxtTitle(model.getmTitle());
                holder.setTxtDesc(model.getmDesc());
                holder.setName(model.getmId());
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Toast.makeText(ForumRec.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent i = new Intent(getApplicationContext(),Navigation.class);
                    startActivity(i);
        } else if (id == R.id.nav_gallery) {
            Intent i = new Intent(getApplicationContext(),Upload.class);
                    startActivity(i);
        } else if (id == R.id.nav_slideshow) {
            Intent i = new Intent(getApplicationContext(),MapsAct.class);
                    startActivity(i);
        } else if (id == R.id.nav_manage) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","WMDeptartment@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Complain regarding..");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "I wish to complain about ...");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));


        } else if (id == R.id.nav_share) {

            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            //logout
            FirebaseAuth.getInstance().signOut();
            startActivity(intent);
            finish();

        }
        InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}


class ViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout root;
    public TextView txtTitle;
    public TextView txtDesc;
    public TextView txtName;

    public ViewHolder(View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.list_root);
        txtTitle = itemView.findViewById(R.id.list_title);
        txtDesc = itemView.findViewById(R.id.list_desc);
        txtName = itemView.findViewById(R.id.fname);
    }

    public void setTxtTitle(String string) {
        txtTitle.setText(string);
    }


    public void setTxtDesc(String string) {
        txtDesc.setText(string);
    }

    public void setName(String string){
        txtName.setText(string);
    } }

