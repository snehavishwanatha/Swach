package com.example.sneha.latlong.Activity;;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sneha.latlong.Navigation;
import com.example.sneha.latlong.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import static com.example.sneha.latlong.Activity.Sign_Up.userID;


public class MainActivity extends AppCompatActivity {
    Button login,checkin;
    TextView signuplink;
    EditText emailid,password,empno,emppwd;
    String emaill,passwordl;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            userID = user.getUid();
            Log.d("Retro", userID);

            // User is signed in
            Intent intent = new Intent(getApplicationContext(),Navigation.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }


        checkin = (Button) findViewById(R.id.checkin);
        login = (Button)findViewById(R.id.loginbutton);
        empno = (EditText) findViewById(R.id.eid);
        emppwd= (EditText) findViewById(R.id.epwd);
        emailid = (EditText) findViewById(R.id.loginemail);
        password= (EditText) findViewById(R.id.loginpassword);
        signuplink=(TextView) findViewById(R.id.SignupLink);

        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String en=empno.getText().toString();
                final String ep=emppwd.getText().toString();
                if(en.isEmpty()) {
                    empno.setError("Require email id");
                    empno.requestFocus();
                    return;
                }

                else if(ep.isEmpty()) {
                    emppwd.setError("enter password");
                    emppwd.requestFocus();
                    return;
                }
                else {
                    final ArrayList<String> eid = new ArrayList<String>();
                    final ArrayList<String>  epass = new ArrayList<String>();
                    mDatabase.child("Employee").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                            Log.e("seqid",String.valueOf(map.keySet()));

                            for(String y: map.keySet())
                            {

                                if(y.equals(en))
                                {
                                    Log.e("seqid",String.valueOf(y));

                                    if( String.valueOf(map.get(y)).equals(ep)) {
                                        Log.e("seqid", String.valueOf(map.get(y)));
                                        Toast.makeText(getApplicationContext(), "Account found", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getApplicationContext(), DeptUpload.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                                else Toast.makeText(getApplicationContext(), "Account details not found", Toast.LENGTH_SHORT).show();

                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });


                }
            }
        });

        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }

            private void userLogin() {
                emaill=emailid.getText().toString();
                passwordl=password.getText().toString();

                if(emaill.isEmpty()) {
                    emailid.setError("Require email id");
                    emailid.requestFocus();
                    return;
                }

                if(passwordl.isEmpty()) {
                    password.setError("enter password");
                    password.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(emaill).matches()) {
                    emailid.setError("Please enter a valid email");
                    emailid.requestFocus();
                    return;
                }

                if (passwordl.length() < 6) {
                    password.setError("incorrect password");
                    password.requestFocus();
                    return;
                }


                mAuth.signInWithEmailAndPassword(emaill, passwordl).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(),Navigation.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });



        signuplink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Sign_Up.class);
                startActivity(intent);
            }
        });


    }
}
