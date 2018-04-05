package com.codex_iter.www.awol;
import java.util.Scanner;


/*
 * ListData class will hold data for displaying in ListView
 * */
public class ListData {

    String sub,code,upd,theory,lab,percent,classes;
    double thT,thp,lat,lap,tc;
    int status;

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

    public int getStatus(){
        double d = new Scanner(percent).nextDouble();
        if(d<65)
            return 1;
        else if(d>=65 && d<75)
            return 2;
        else if(d>=75 && d<90)
            return 3;
        else return 4;
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


