package com.example.breathingclassifer;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class Patinet_Details extends AppCompatActivity {
    Spinner spinner;
    ProgressBar PgBar;
    ConstraintLayout IdChooseLay,UserIDLay;
    TextView  txtName,txtEmail,txtBDay,txtType,txtEmgName,txtEmgRel,txtEmgPhone,txtPhone,txtID;
    Button CALL;
    FirebaseDatabase database;
    FirebaseAuth Auth;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patinet_details);
        Bundle extras = getIntent().getExtras();

        spinner = findViewById(R.id.PD_IdSpn);
        PgBar = findViewById(R.id.PD_pgBar);
        IdChooseLay = findViewById(R.id.PD_IdLay);
        UserIDLay = findViewById(R.id.PD_UsrID);
        txtID = findViewById(R.id.PD_UsrIDTxt);
        CALL = findViewById(R.id.PD_Call);
        database = FirebaseDatabase.getInstance();
        Auth = FirebaseAuth.getInstance();
        txtName = findViewById(R.id.PD_txtName);
        txtEmail= findViewById(R.id.PD_txtEmail);
        txtBDay = findViewById(R.id.PD_txtBday);
        txtType= findViewById(R.id.PD_txtTyp);
        txtEmgName = findViewById(R.id.PD_txtEmrgName);
        txtEmgRel= findViewById(R.id.PD_txtRelation);
        txtEmgPhone= findViewById(R.id.PD_txtEmrgPhone);
        txtPhone = findViewById(R.id.PD_txtPhone);
        String UID = Auth.getCurrentUser().getUid();
        if (extras != null) {
            Log.d(TAG, "onCreate: "+extras.toString());
            String Md = extras.getString("T");
            //Toast.makeText(Patinet_Details.this, Md, Toast.LENGTH_SHORT).show();
            PreparePage(Md);
        }

        PgBar.setVisibility(View.GONE);
    }

    private void PreparePage(String Mod) {

        if(Mod.equals("U"))
        {
            user = User.getAppUser(new User());
            if (user.getType().equals("D")) {
                UserIDLay.setVisibility(View.GONE);
                txtType.setText("Doctor");
            } else {
                UserIDLay.setVisibility(View.VISIBLE);
                txtType.setText("Patient");
            }
            txtID.setText(String.valueOf(user.getID()));
            IdChooseLay.setVisibility(View.GONE);
            CALL.setVisibility(View.GONE);
            txtName.setText(user.getName());
            txtEmail.setText(user.getEml());
            txtBDay.setText(user.getBdayStr());

            txtEmgName.setText(user.getEmrgName());
            txtEmgRel.setText(user.getEmrgRelation());
            txtEmgPhone.setText(user.getEmrgPhone());
            txtPhone.setText(user.getPhone());

        }
        else if (Mod.equals("P"))
        {
            findViewById(R.id.PD_Type).setVisibility(View.GONE);
            txtType.setVisibility(View.GONE);
            IdChooseLay.setVisibility(View.VISIBLE);
            CALL.setVisibility(View.VISIBLE);
            PrepareSpinner();
//            DatabaseReference db = database.getReference("");
//            db.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    user = snapshot.child("Users").child(UID).getValue(User.class);
//                    Log.d(TAG, "onDataChange: "+user.toString());
//                    Toast.makeText(Patinet_Details.this, "Got data", Toast.LENGTH_SHORT).show();
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Toast.makeText(Patinet_Details.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @SuppressLint("ResourceAsColor")
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    ((TextView) parent.getChildAt(0)).setTextColor(R.color.black);
//                    if (parent.getItemAtPosition(position).equals("Choose Football players from lis")){
//                    }else {
//                        String item = parent.getItemAtPosition(position).toString();
//                        Toast.makeText(parent.getContext(),"Selected: " +item, Toast.LENGTH_SHORT).show();
//                    }
//                }
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//                }
//            });
//            Log.d("Before Delay","");
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            },2000);
//            //Todo Getting user details
//            Log.d("After Delay","");
//
//

        }


    }

    private void PrepareSpinner() {
        //Todo: Same Function from HomePage
        List<String> SpnrIDList = new ArrayList<String>();
        List<String> SpnrNameList = new ArrayList<String>();

        SpnrNameList.add(0,"Please Choose a name/ID");
        ArrayAdapter<Dictionary<Integer,String>> arrayAdapter;
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, SpnrNameList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    private boolean getUserbyUId(String UID){
        Log.d(TAG, "getUSerbyUId: "+UID);
        DatabaseReference db = database.getReference("");
        final boolean[] Dn = {false};
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.child("Users").child(UID).getValue(User.class);
                Log.d(TAG, "onDataChange: "+user.toString());
                Toast.makeText(Patinet_Details.this, "Got data", Toast.LENGTH_SHORT).show();
                Dn[0] = true;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Patinet_Details.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Dn[0] = false;
            }
        });
        return Dn[0];
    }
}

