package com.codex_iter.www.awol;

import java.util.Scanner;

/*
 * ListData class will hold data for displaying in ListView
 * */
public class ListData {

    String sub,code,upd,theory,lab,percent;
    int thT,thp,lat,lap;


    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUpd() {
        return upd;
    }

    public void setUpd(String upd) {
        this.upd = upd.substring(0,11);
    }

    public String getTheory() {
        return theory;
    }

    public void setTheory(String theory) {
        if (theory.equals("Not Applicable")) {
            this.theory = theory;
            thp = 0;
            thT = 0;
        } else {
            Scanner in = new Scanner(theory);
            thp = in.nextInt();
            char c = in.next().charAt(0);
            thT = in.nextInt();
            this.theory = theory + "(" + ((thp / thT) * 100) + " %)";
        }
    }
    public String getLab() {
        return lab;
    }

    public void setLab(String lab) {
        if (lab.equals("Not Applicable")) {
            this.lab = lab;
            lap = 0;
            lat = 0;
        } else {
            Scanner in = new Scanner(lab);
            lap = in.nextInt();
            char c = in.next().charAt(0);
            lat = in.nextInt();
            this.lab = lab + "(" + Double.toString((lap / lat) * 100).substring(0,3) + " %)";
        }
    }
    public String getAbsent()
    {   int i=lat+thT-thp-lap;
        return Integer.toString(i);
    }
    public String getPercent() {
        return percent;
    }
    public String getStatus()
    {
        int n= new Scanner(percent).nextInt();
                if(0<n && n<40)
                    return "very poor";
                else if(40<=n && n<60)
                    return "poor";
                else if(60<=n && n<75)
                    return "satisfactory";
                else if(75<=n && n<85)
                    return "good";
                else
                    return "excellent";
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }
}


