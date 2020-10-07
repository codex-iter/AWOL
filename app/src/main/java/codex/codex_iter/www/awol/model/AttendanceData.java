package codex.codex_iter.www.awol.model;

import java.util.Locale;
import java.util.Scanner;

public class AttendanceData {
    private String sub,code,upd,theory,lab,percent,that="",labt="",old="",bunk_text_str="Don't Bunk Anymore";
    private double thT;
    private double thp;
    private double lat;
    private double lap;
    public static AttendanceData[] attendanceData;

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
        this.percent = String.format(Locale.US, "%.1f", new Scanner(percent).nextDouble());
    }

    public void setBunk(int pref_min_attendance) {
        double percent_class = Double.parseDouble(getPercent()); // 70.83
        double total_class = Double.parseDouble(getClasses()); // 21
        double absent = Double.parseDouble(getAbsent()); // 3
        double present = total_class - absent; // 18
        int i;
        if (percent_class >= pref_min_attendance) {
            for (i = 0; i != -99; i++) {
                double p = (present / (total_class + i)) * 100;
                if (p < pref_min_attendance) break;
            }
            i--;
            if (i > 0) {
                if (i != 1) {
                    this.bunk_text_str = "Bunk " + i + " classes for " + pref_min_attendance + "%";
                } else {
                    this.bunk_text_str = "Bunk " + i + " class for " + pref_min_attendance + "%";
                }
            }
        }
        else
        {
            for (i = 0; i != -99; i++) {
                double p = ((present + i) / (total_class + i) * 100);
                if (p > pref_min_attendance) break;
            }
            i--;
            if (i > 0) {
                if (i != 1) {
                    this.bunk_text_str = "Attend " + i + " classes for " + pref_min_attendance + "%";
                } else {
                    this.bunk_text_str = "Attend " + i + " class for " + pref_min_attendance + "%";
                }
            }
        }
    }


}


