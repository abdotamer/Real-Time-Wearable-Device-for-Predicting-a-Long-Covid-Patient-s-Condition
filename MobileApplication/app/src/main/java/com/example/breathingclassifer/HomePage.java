package com.example.breathingclassifer;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.security.Key;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HomePage extends AppCompatActivity {
    int ID;
    GraphView gr;
    ProgressBar pg;
    ImageView Img;
    ConstraintLayout Idlay;
    Spinner spinner;
    RadioGroup Rg;
    RadioButton RbId,RbName;
    TextView typTxt,gretTxt,PatInfo;
    Button addBtn;
    Button ad;
    EditText NewID;
    List<Integer> SpnrIDList = new ArrayList<Integer>();
    List<String> SpnrNameList = new ArrayList<String>();
    FirebaseDatabase Database;
    User user;
    ArrayAdapter<String> arrayAdapterS;
    ArrayAdapter<Integer> arrayAdapterI;
    Interpreter interpreter;
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        user = User.getAppUser(new User());
        gr = findViewById(R.id.HM_Grph);
        spinner = findViewById(R.id.HM_IdSpn);
        typTxt = findViewById(R.id.HM_Typ);
        gretTxt = findViewById(R.id.HM_Greeting);
        Img = findViewById(R.id.Hm_Img);
        Idlay = findViewById(R.id.HM_IdLay);
        PatInfo = findViewById(R.id.HM_txtPat);
        pg = findViewById(R.id.HM_PgBar);
        addBtn = findViewById(R.id.HM_addNew);
        Rg = findViewById(R.id.HM_RG);
        RbId = findViewById(R.id.HM_RBID);
        RbName = findViewById(R.id.HM_RbName);
        Database=FirebaseDatabase.getInstance();

        if(user.getType().equals("P"))
        {
            Idlay.setVisibility(View.GONE);
            ID = user.getID();
            gretTxt.setText("Hello, "+user.getName().split(" ")[0]);
            getData(ID);
        }
        else {
            Idlay.setVisibility(View.VISIBLE);
            gretTxt.setText("Hello, Dr. "+user.getName().split(" ")[0]);
            PrepareSpinner();
        }

        PatInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this,Patinet_Details.class).putExtra("T","P"));

            }
        });
        Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this,Patinet_Details.class).putExtra("T","U"));
            }
        });




//    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//        @SuppressLint("ResourceAsColor")
//        @Override
//        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            ((TextView) parent.getChildAt(0)).setTextColor(R.color.black);
//            if (parent.getItemAtPosition(position).equals("Choose Football players from lis")){
//            }else {
//                String item = parent.getItemAtPosition(position).toString();
//                Toast.makeText(parent.getContext(),"Selected: " +item, Toast.LENGTH_SHORT).show();
//            }
//        }
//        @Override
//        public void onNothingSelected(AdapterView<?> parent) {
//        }
//    });
    }

    private void PrepareSpinner() {
        ArrayList<String> PatLst = user.getPatIds();
        DatabaseReference db = Database.getReference("Users");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (String UID:PatLst){
                    //Toast.makeText(HomePage.this, "Found", Toast.LENGTH_SHORT).show();
                    SpnrIDList.add(snapshot.child(UID).child("id").getValue(Integer.class));
                    SpnrNameList.add(snapshot.child(UID).child("name").getValue(String.class));
                    RbId.setVisibility(View.VISIBLE);
                    PatInfo.setVisibility(View.VISIBLE);
                }
                Log.d(TAG, "ID: "+SpnrIDList);
                Log.d(TAG, "Name: "+SpnrNameList);
                if(SpnrNameList.isEmpty()) {
                    SpnrNameList.add(0,"Please Choose add a patient to your list");
                    RbId.setVisibility(View.INVISIBLE);
                    RbName.performClick();
                    PatInfo.setVisibility(View.GONE);
                }
                else {
                    arrayAdapterS = new ArrayAdapter(HomePage.this, R.layout.spinner_view, SpnrNameList);
                    arrayAdapterI = new ArrayAdapter(HomePage.this, R.layout.spinner_view, SpnrIDList);
                    arrayAdapterS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    arrayAdapterI.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(arrayAdapterS);
                    RbId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "Radio Button:" + SpnrIDList);
                            spinner.setAdapter(arrayAdapterI);
                        }
                    });
                    RbName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "Radio Button:" + SpnrNameList);
                            spinner.setAdapter(arrayAdapterS);
                        }
                    });
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            getData(SpnrIDList.get(position));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

                pg.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        spinner.setDropDownVerticalOffset(45);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonShowPopupWindowClick(v);
            }
        });



    }
    private void getData(int ID) {
        pg.setVisibility(View.VISIBLE);
        Map<Long,Integer> Pat = new TreeMap<>();
        DatabaseReference db =Database.getReference("");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot Year :snapshot.child("Patterns").child(String.valueOf(ID)).getChildren())
                    for(DataSnapshot Month: Year.getChildren())
                        for(DataSnapshot Day: Month.getChildren())
                            for(DataSnapshot Hour: Day.getChildren())
                                for(DataSnapshot Mn: Hour.getChildren())
                                    for(DataSnapshot Sec: Mn.getChildren())
                                        for(DataSnapshot mSec:Sec.getChildren())
                                        {
                                            try {

                                                Long Ky = Long.valueOf(0);
                                                int Y,M,D,H,m,S,ms;
                                                Y = Integer.parseInt(Year.getKey());
                                                M = Integer.parseInt(Month.getKey());
                                                D = Integer.parseInt(Day.getKey());
                                                H = Integer.parseInt(Hour.getKey());
                                                m = Integer.parseInt(Mn.getKey());
                                                S = Integer.parseInt(Sec.getKey());
                                                ms = Integer.parseInt(mSec.getKey());
                                                Date dt = new Date(Y,M,D,H,m,S);
                                                Ky = dt.getTime()/100+ms;
                                                Pat.put(Ky,mSec.getValue(Integer.class));
                                            } catch (Exception e){
                                                Toast.makeText(HomePage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                Gragh(Pat);
                Log.d("Pattern is",Pat.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Gragh(@NonNull Map<Long, Integer> pat) {
        gr.getGridLabelRenderer().setVerticalLabelsVisible(false);
        gr.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        gr.getViewport().setScrollable(true);
        gr.getViewport().setScrollableY(false);
        gr.getViewport().setXAxisBoundsManual(true);
        gr.getViewport().setMaxX(300);
        gr.getViewport().setMinX(0);
        gr.getViewport().setYAxisBoundsManual(false);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        for(long indx:pat.keySet())
            series.appendData(new DataPoint(indx,pat.get(indx)),true,20000);
        gr.addSeries(series);
        gr.getViewport().scrollToEnd();

        pg.setVisibility(View.GONE);
    }

    private void prepareAI(){
        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
                .build();
        FirebaseModelDownloader.getInstance()
                .getModel("LongCovidClassification", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,conditions)
                .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
                    @Override
                    public void onSuccess(CustomModel model) {
                        File modelFile = model.getFile();
                        if (modelFile != null) {
                            interpreter = new Interpreter(modelFile);
                        }
                    }
                });

    }

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.add_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, -450);
        NewID = popupView.findViewById(R.id.AD_Id);
        ad = popupView.findViewById(R.id.AD_ad);

        ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pg.setVisibility(View.VISIBLE);
                DatabaseReference db = Database.getReference("Users");
                int newid = Integer.parseInt(NewID.getText().toString());
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot UID : snapshot.getChildren()) {
                            if (UID.child("id").getValue(Integer.class) == newid) {
                                String name = UID.child("name").getValue(String.class);
                                AlertDialog.Builder Build = new AlertDialog.Builder(HomePage.this);
                                Build.setMessage("Patient name: " + name)
                                        .setTitle("Please Confirm the patient name")
                                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                SpnrIDList.add(newid);
                                                SpnrNameList.add(name);
                                                switch(user.addPatID(UID.getKey())){
                                                    case 1:
                                                        String u = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                        db.child(u).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(HomePage.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                                                                    popupWindow.dismiss();
                                                                    pg.setVisibility(View.GONE);
                                                                } else
                                                                    Toast.makeText(HomePage.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }); break;
                                                    case 0:
                                                        Toast.makeText(HomePage.this, "Please try again", Toast.LENGTH_SHORT).show(); break;
                                                    case -1:
                                                        Toast.makeText(HomePage.this, "Already in the list", Toast.LENGTH_SHORT).show(); break;

                                                }

                                            }
                                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        NewID.setText("");
                                        NewID.requestFocus();
                                        pg.setVisibility(View.GONE);
                                    }
                                }).create().show();


                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return true;
            }
        });
    }
}