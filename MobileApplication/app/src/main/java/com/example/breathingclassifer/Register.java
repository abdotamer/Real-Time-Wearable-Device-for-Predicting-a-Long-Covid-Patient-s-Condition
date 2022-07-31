package com.example.breathingclassifer;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PatternMatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class Register extends AppCompatActivity {
    private ImageView Img;
    private Button btnReg;
    private EditText txtName,txtPass,txtRePass,txtPhone,txtEml,txtEmgName,txtEmgRel,txtEmgPhone,BDay;
    private RadioGroup rdType;
    private ProgressBar pgBar;
    private Calendar calendar;
    private Date BirthDay;
    private int year, month, day;
    private char userTyp;
    FirebaseDatabase database;
    private String Name,Eml,Phone,Pass,RePass,EmgName,EmgRel,EmgPhone;
    private FirebaseAuth Auth;
    User newUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        DatabaseReference db = database.getReference("");
        btnReg = findViewById(R.id.btnReg);
        txtName = findViewById(R.id.txtName);
        BDay = findViewById(R.id.txtBday);
        txtEml = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        txtEmgName = findViewById(R.id.txtEmrgName);
        txtEmgRel = findViewById(R.id.txtRelation);
        txtEmgPhone = findViewById(R.id.txtEmrgPhone);
        txtPass = findViewById(R.id.txtPass);
        txtRePass = findViewById(R.id.txtrePass);
        pgBar = findViewById(R.id.pgBar);
        rdType = findViewById(R.id.rdType);
        txtName.requestFocus();
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        BDay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    showDialog(999);
                    txtPhone.requestFocus();
                }
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pgBar.setVisibility(View.VISIBLE);
                if(Check()) {
//                    ToDo Add Pic
                    User newUser = new User(Name, Eml, Phone,  EmgName, EmgRel, EmgPhone,String.valueOf(userTyp));
                    newUser.setBDay(year, month, day);
                    try {
                        Log.w(TAG, newUser.toString());
                        Eml = txtEml.getText().toString();
                        Pass = txtPass.getText().toString();
                        Auth.createUserWithEmailAndPassword(Eml,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)  {
                                if (task.isSuccessful()) {

                                    String Uid = Auth.getCurrentUser().getUid();
                                    Log.w(TAG, "Done Auth");
                                    Log.w(TAG, "Got ID:" + newUser.getID());
                                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                newUser.setID(Integer.parseInt(snapshot.child("NewID").getValue().toString()));
                                                Log.d(TAG, newUser.toString());
                                                if (newUser.getType().equals("D"))
                                                    newUser.setID(0);
                                                else
                                                    db.child("NewID").setValue(newUser.getID()+1);
                                                Toast.makeText(Register.this, "Id:" + newUser.getID(), Toast.LENGTH_SHORT).show();
                                                db.child("Users").child(Uid).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            User.getAppUser(newUser);

                                                                    Toast.makeText(Register.this, "Welcome", Toast.LENGTH_SHORT).show();

                                                                    startActivity(new Intent(Register.this,HomePage.class));
                                                                    pgBar.setVisibility(View.GONE);
                                                                    Log.w(TAG, "Done Uploading");
                                                                    MainActivity.act.finish();
                                                                    finish();
                                                        }
                                                        else
                                                            Toast.makeText(Register.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                } else {
                                    Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }catch (Exception e)
                    {
                        Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    pgBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean Check() {
        boolean goodToGo = true;
        try {
            Name = txtName.getText().toString().trim();
            Phone = txtPhone.getText().toString().trim();
            Eml = txtEml.getText().toString();
            Pass = txtPass.getText().toString();
            RePass = txtRePass.getText().toString();
            EmgName = txtEmgName.getText().toString();
            EmgPhone = txtEmgPhone.getText().toString();
            EmgRel = txtEmgRel.getText().toString();
            String tBrDay = BDay.getText().toString().trim();
            int typ = rdType.getCheckedRadioButtonId();

            if(Name.isEmpty())
            {
                txtName.setText("");
                txtName.setError("Please Enter your Name");
                if(goodToGo)
                    txtName.requestFocus();
                goodToGo = false;
                //return true;
            }
            if(Name.isEmpty())
            {
                txtName.setText("");
                txtName.setError("Please Enter your Name");
                if(goodToGo)
                    txtName.requestFocus();
                goodToGo = false;
            }
            if(tBrDay.isEmpty())
            {
                BDay.setText("");
                BDay.setError("Please Enter your BirthDay");
                if(goodToGo)
                    txtName.requestFocus();
                goodToGo = false;
            }
            if(Phone.isEmpty()|| !Patterns.PHONE.matcher(Phone).matches())
            {
                txtPhone.setText("");
                txtPhone.setError("Please Enter a valid phone number");
                if(goodToGo)
                    txtPhone.requestFocus();
                goodToGo = false;
            }
            if(Eml.isEmpty()|| !Patterns.EMAIL_ADDRESS.matcher(Eml).matches())
            {
                txtEml.setText("");
                txtEml.setError("Please Enter a valid Email");
                if(goodToGo)
                    txtEml.requestFocus();
            }
            if(Pass.length() < 6)
            {
                txtPass.setText("");
                txtRePass.setText("");
                txtPass.setError("Please Enter a Password with more than 6 characters");
                if(goodToGo)
                    txtPass.requestFocus();
                goodToGo = false;
            }else if(!Pass.matches(RePass)){
                txtPass.setText("");
                txtRePass.setText("");
                txtRePass.setError("Password Does not match");
                if(goodToGo)
                    txtRePass.requestFocus();
                goodToGo = false;
            }
            if (typ == R.id.Doc)
                userTyp = 'D';
            else if (typ == R.id.Pat)
                userTyp = 'P';
            else
            {
                Toast.makeText(this, "please Choose a Valid Type", Toast.LENGTH_SHORT).show();
                goodToGo = false;
            }
            if(EmgName.isEmpty())
            {
                txtEmgName.setText("");
                txtEmgName.setError("Please Enter a Name");
                if(goodToGo)
                    txtEmgName.requestFocus();
                goodToGo = false;
            }
            if(EmgRel.isEmpty())
            {
                txtEmgRel.setText("");
                txtEmgRel.setError("Please your relation to the person");
                if(goodToGo)
                    txtEmgRel.requestFocus();
                goodToGo = false;
            }
            if(EmgPhone.isEmpty()|| !Patterns.PHONE.matcher(EmgPhone).matches())
            {
                txtEmgPhone.setText("");
                txtEmgPhone.setError("Please Enter a valid phone number");
                if(goodToGo)
                    txtEmgPhone.requestFocus();
                goodToGo = false;
            }
        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Check: " + e.getMessage());

        }

        return goodToGo;
    }

    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0,
                              int arg1, int arg2, int arg3) {
            year = arg1;
            month= arg2+1;
            day = arg3;
            setBirthDateTxt(year, month, day);
        }

    };

    private void setBirthDateTxt(int year, int month, int day) {
        BDay.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }
}