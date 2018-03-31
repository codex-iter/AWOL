package com.codex_iter.www.awol;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/*
 * ListData class will hold data for displaying in ListView
 * */
public class ListData {

    String sub,code,upd,theory,lab,percent,classes;
    double thT,thp,lat,lap,tc;

    public String getClasses() {
        return Double.toString(thT+lat);
    }


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
            String res = String.format("%.2f",((thp / thT) * 100) );
            this.theory = theory + "(" + res + " %)";
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
            this.lab = lab + "(" + String.format("%.2f",((lap / lat) * 100)).substring(0,3) + " %)";
        }
    }

    public String getAbsent()
    {   int i=(int)Math.floor(lat+thT-thp-lap);
        return Integer.toString(i);
    }
    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }
}


