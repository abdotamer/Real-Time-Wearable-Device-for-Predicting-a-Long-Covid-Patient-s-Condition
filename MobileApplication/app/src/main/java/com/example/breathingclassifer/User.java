package com.example.breathingclassifer;

import static android.content.ContentValues.TAG;

import android.util.Log;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class User {
    private String Name,Eml,Phone,EmrgName,EmrgRelation,EmrgPhone,BdayStr,Type;
    private int ID,age;
    private ArrayList<String> PatIds = new ArrayList<>();
    private static User AppUser;
    public static User getAppUser( User user)
    {
        if(AppUser ==null)
            AppUser = user;
        return AppUser;
    }
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
    public void setBDay(int Year,int month,int day) {
        Calendar bd = Calendar.getInstance();
        bd.set(Year,month,day);
        Date dateBd = bd.getTime();
        this.BdayStr = DateFormat.getDateInstance().format(dateBd);
        Log.d(TAG, "setBDay: "+String.valueOf(day)+"/"+String.valueOf(month)+"/"+String.valueOf(Year));
        this.age = getAge();
    }
    public int getAge(){

        if(BdayStr != null) {
            Date bd = new Date(BdayStr),cr =new Date();
            this.age = cr.getYear() - bd.getYear();
            if ((bd.getMonth() > cr.getMonth() || (bd.getMonth() == cr.getMonth() &&bd.getDay() > cr.getDay())))
            {
                this.age--;
            }
            return this.age;
        }
        else
            return 0;
    }
    public User() {
    }
    public User(String name, String eml, String phone, String emrgName, String emrgRelation, String emrgPhone, String type) {
        this.Name = name;
        this.Eml = eml;
        this.Phone = phone;
        this.EmrgName = emrgName;
        this.EmrgRelation = emrgRelation;
        this.EmrgPhone = emrgPhone;
        this.Type = String.valueOf(type);
        Log.w(TAG, "User: "+this.Eml.toString());

    }

    @Override
    public String toString() {
        return "User{" +
                "Name='" + Name + '\'' +
                ", Eml='" + Eml + '\'' +
                ", Phone='" + Phone + '\'' +
                ", EmrgName='" + EmrgName + '\'' +
                ", EmrgRelation='" + EmrgRelation + '\'' +
                ", EmrgPhone='" + EmrgPhone + '\'' +
                ", BdayStr='" + BdayStr + '\'' +
                ", Type='" + Type + '\'' +
                ", ID=" + ID +
                ", age=" + age +
                ", PatIds=" + PatIds +
                '}';
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEml() {
        return Eml;
    }

    public void setEml(String eml) {
        Eml = eml;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmrgName() {
        return EmrgName;
    }

    public void setEmrgName(String emrgName) {
        EmrgName = emrgName;
    }

    public String getEmrgRelation() {
        return EmrgRelation;
    }

    public void setEmrgRelation(String emrgRelation) {
        EmrgRelation = emrgRelation;
    }

    public String getEmrgPhone() {
        return EmrgPhone;
    }

    public void setEmrgPhone(String emrgPhone) {
        EmrgPhone = emrgPhone;
    }

    public String getBdayStr() {
        return BdayStr;
    }

    public void setBdayStr(String bdayStr) {
        BdayStr = bdayStr;
        setAge(getAge());
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = String.valueOf(type);
    }
    public void setAge(int age) {
        this.age = age;
    }
    public int addPatID(String Uid){
        if(PatIds.contains(Uid))
            return -1;
        if(PatIds.add(Uid))
            return 1;
        else
            return 0;
    }
    public boolean remPatID(String Uid) {
        return PatIds.remove(Uid);
    }


    public ArrayList<String> getPatIds() {
        return PatIds;
    }

    public void setPatIds(ArrayList<String> patIds) {
        PatIds = patIds;
    }
}

