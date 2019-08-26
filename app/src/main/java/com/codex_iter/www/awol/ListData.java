package com.codex_iter.www.awol;
import java.util.Locale;
import java.util.Scanner;


/*
 * ListData class will hold data for displaying in ListView
 * */
public class ListData {

    private String sub,code,upd,theory,lab,percent,that="",labt="",old="",bunk_text_str="Don't Bunk Anymore";
    private double thT,thp,lat,lap,tc,tha,la;
     static ListData[] ld;
    public String getClasses() {
        return Integer.toString((int)(thT+lat));
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getOld() {
        return old;
    }

    public void setOld(String old) {
        this.old = old;
    }

    public String getCode() {
        return code;
    }

    public String getThat() {
        return that;
    }

    public void setThat(String that) {
        this.that = that;
    }

    public String getLabt() {
        return labt;
    }

    public void setLabt(String labt) {
        this.labt = labt;
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
        this.upd = upd;
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
            String res = " ("+String.format(Locale.US, "%.0f",((thp / thT) * 100) )+"%)";
            this.theory = theory;
            setThat(res);
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
            this.lab = lab ;
                    setLabt(" ("+String.format(Locale.US, "%.0f",((lap / lat) * 100))+"%)");
        }
    }

    public String getBunk_text_str() {
        return bunk_text_str;
    }


    public String getAbsent()
    {   int i=(int)Math.floor(lat+thT-thp-lap);
        return Integer.toString(i);
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = String.format(Locale.US, "%.1f",new Scanner(percent).nextDouble());
    }
    public void setBunk()
    {
        double percent_class=Double.parseDouble(getPercent());
        double total_class = Double.parseDouble(getClasses());
        double absent = Double.parseDouble(getAbsent());
        double present = total_class - absent;
        int i;
        if(percent_class>=75) {
            //to be continued...
            for (i = 0; i != -99; i++) {
                double p = (present / (total_class + i)) * 100;
                if (p < 75) break;
            }
            i--;
            if (i != 1) {
                this.bunk_text_str = "BUNK " + i + " classes for 75%";
            }else {
                this.bunk_text_str = "BUNK " + i + " class for 75%";
            }
        }
        else
        {
            for (i = 0; i != -99; i++) {
                double p = ((present + i) / (total_class + i) * 100);
                if (p > 75) break;
            }
            i--;
            if (i != 1) {
                this.bunk_text_str = "Attend " + i + " classes for 75%";
            } else {
                this.bunk_text_str = "Attend " + i + " class for 75%";
            }
        }
    }


}


