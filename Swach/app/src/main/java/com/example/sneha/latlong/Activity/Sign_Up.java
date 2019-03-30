package com.example.sneha.latlong.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sneha.latlong.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
class User {

    public String username;
    public String contact;
    public String country;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String contact, String email,String country) {
        this.username = username;
        this.contact=contact;
        this.email = email;
        this.country=country;
    }

}
public class Sign_Up extends AppCompatActivity {
    Button signup;
    EditText email,password,contact,username,country;
    String emails,passwords,name,no,countryloc;
    public static String userID;

    private FirebaseAuth mAuth;
   private DatabaseReference mDatabase;
   // private DatabaseReference mDatabaseref,mDemoref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        final Spinner spinner = (Spinner) findViewById(R.id.signupspinner);
        country=(EditText)findViewById(R.id.signupcountry);
        signup = (Button) findViewById(R.id.email_sign_in_button);
        email = (EditText) findViewById(R.id.signupemail);
        password= (EditText) findViewById(R.id.signuppassword);
        contact= (EditText) findViewById(R.id.signupcontact);
        username= (EditText) findViewById(R.id.signupname);

        String[] items = new String[]{"Karnataka","TamilNadu","Goa"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                       long arg3) {
                String anyvariable=String.valueOf(spinner.getSelectedItem());

                country.setText(anyvariable);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        mAuth = FirebaseAuth.getInstance();
       mDatabase = FirebaseDatabase.getInstance().getReference();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               registerUser();
            }

            private void registerUser() {
                    name=username.getText().toString();
                    no=contact.getText().toString();
                    emails=email.getText().toString();
                    passwords=password.getText().toString();
                    countryloc=country.getText().toString();


                    if(countryloc.equals(" "))
                    {
                        country.setError("Pick a country");
                        country.requestFocus();
                        return;
                    }
                    if(emails.isEmpty()) {
                        email.setError("Require email id");
                        email.requestFocus();
                        return;
                    }
                if(no.isEmpty()) {
                    contact.setError("Require contact");
                    contact.requestFocus();
                    return;
                }
                if(name.isEmpty()) {
                    username.setError("Require name");
                    username.requestFocus();
                    return;
                }
                    if(passwords.isEmpty()) {
                        password.setError("enter password");
                        password.requestFocus();
                        return;
                    }
                if (!Patterns.EMAIL_ADDRESS.matcher(emails).matches()) {
                    email.setError("Please enter a valid email");
                    email.requestFocus();
                    return;
                }

                if (passwords.length() < 6) {
                    password.setError("Minimum length of password should be 6");
                    password.requestFocus();
                    return;
                }

                if (no.length() < 10) {
                    contact.setError("{Please enter valid contact");
                    contact.requestFocus();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(emails,passwords).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(name,no,emails,countryloc);
                            userID =mAuth.getCurrentUser().getUid();
                            mDatabase.child("users").child(userID).setValue(user);
                            Toast.makeText(getApplicationContext(), "Successful Sign_in", Toast.LENGTH_SHORT).show();



                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                        else {

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
            }
        });

    }
}