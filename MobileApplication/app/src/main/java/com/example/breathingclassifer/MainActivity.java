package com.example.breathingclassifer;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    public static Activity act;
    private FirebaseAuth Auth;
    private Button btnSign, btnReg;
    private TextView Eml, Pss,Frgt;
    private ProgressBar Pg;
    private FirebaseDatabase Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        act = this;
        btnSign = findViewById(R.id.btnSign);
        btnReg = findViewById(R.id.btnReg);
        Eml = findViewById(R.id.edtEmail);
        Pss = findViewById(R.id.edtPass);
        Frgt = findViewById(R.id.MP_frgt);
        Pg = findViewById(R.id.MP_pgBar);
        Auth = FirebaseAuth.getInstance();
        Data = FirebaseDatabase.getInstance();

        Frgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder Build = new AlertDialog.Builder(MainActivity.this);
                Build.setTitle("PLease enter your email");
                final EditText input = new EditText(MainActivity.this);
                input.setHint("Email");
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                Build.setView(input);
                Build.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String Em = input.getText().toString();
                        try {
                            Auth.sendPasswordResetEmail(Em).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainActivity.this, "Reset link has been sent to your email", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }catch (Exception e){
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
            }
        });
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valEml = Eml.getText().toString().trim();
                String valPss = Pss.getText().toString().trim();
                //TODO: Check Email Format
                Pg.setVisibility(View.VISIBLE);
                Log.d(TAG, "onCreate: Email:" + valEml + ",Pass:" + valPss);
                //Toast.makeText(MainActivity.this, "How Dare You!!!!!", Toast.LENGTH_SHORT).show();
                try {
                        //TODO: Continue the errors
                        Auth.signInWithEmailAndPassword(valEml, valPss).addOnCompleteListener(MainActivity.this,new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    try {
                                        Pg.setVisibility(View.INVISIBLE);
                                        throw task.getException();
                                    } catch (FirebaseAuthException e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, e.getMessage());
                                    } catch (Exception e) {
                                        Log.e(TAG, e.getMessage());
                                    }
                                }
                                else {
                                    getUserData();
                                }
                            }


                        });
                }  catch(Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.getMessage());
                }

                }

                //.setValue("Button Clicked");


            }
        );
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Register.class));
            }
        });
        //todo forget Password
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                btnSign.setText("How Dare Youuuuuu!!!!!!!");
//               // myRef.setValue("Fab Clicked");
//            }
//        });
    }
    void getUserData(){
        DatabaseReference db = Data.getReference("");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String UID = Auth.getCurrentUser().getUid();
                User user = User.getAppUser(snapshot.child("Users").child(UID).getValue(User.class));
                Log.d(TAG, "onDataChange: "+user.toString());
//                Toast.makeText(MainActivity.this, "Got data", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "Welcome Back", Toast.LENGTH_SHORT).show();
                Pg.setVisibility(View.GONE);
                startActivity(new Intent(MainActivity.this,HomePage.class));
                finish();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
