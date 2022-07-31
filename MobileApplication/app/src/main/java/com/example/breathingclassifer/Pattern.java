package com.example.breathingclassifer;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;

public class Pattern {
    public Dictionary<String,Dictionary<String,Dictionary<String,Dictionary<String,Dictionary<String,Dictionary<String, ArrayList<Integer>>>>>>>  Patr;

    public Pattern() {
    }

    public Dictionary<String, Dictionary<String, Dictionary<String, Dictionary<String, Dictionary<String, Dictionary<String, ArrayList<Integer>>>>>>> getPatr() {
        return Patr;
    }

    public ArrayList<Integer> GetPattern()
    {
        ArrayList<Integer> P = new ArrayList<>();
        Enumeration<String> Year ,Month,Day,Hour,Mn,Sec;
        Year=Patr.keys();
        while (Year.hasMoreElements()){
            Month = Patr.get(Year.nextElement()).keys();
            while (Month.hasMoreElements()){
                Day = Patr.get(Year.nextElement()).get(Month.nextElement()).keys();
                while (Day.hasMoreElements()){
                    Hour = Patr.get(Year.nextElement()).get(Month.nextElement()).get(Day.nextElement()).keys();
                    while (Hour.hasMoreElements()){
                        Mn = Patr.get(Year.nextElement()).get(Month.nextElement()).get(Day.nextElement()).get(Hour.nextElement()).keys();
                        while (Mn.hasMoreElements()){
                            Sec = Patr.get(Year.nextElement()).get(Month.nextElement()).get(Day.nextElement()).get(Hour.nextElement()).get(Mn.nextElement()).keys();
                            while (Sec.hasMoreElements())
                                for(int i :Patr.get(Year.nextElement()).get(Month.nextElement()).get(Day.nextElement()).get(Hour.nextElement()).get(Mn.nextElement()).get(Sec.nextElement())){
                                    P.add(i);
                                }

                        }
                    }
                }
            }
        }
        return P;
    }
    public void setPatr(Dictionary<String, Dictionary<String, Dictionary<String, Dictionary<String, Dictionary<String, Dictionary<String, ArrayList<Integer>>>>>>> patr) {
        Patr = patr;
    }

    @Override
    public String toString() {
        return "Pattern{" +
                "Patr=" + Patr +
                '}';
    }
}
