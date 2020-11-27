package codex.codex_iter.www.awol.model;

public class Student {
    private String name;
    private String redgNo;
    private String password;
    private String branch;
    private String academic_year;
    private String semester;
    private long averagePresent;
    private int averageAbsent;
    private Attendance[] attendances;
    private Result[] result;
    private String offlineAttendance;
    private String offlineResult;

    public long getAveragePresent() {
        return averagePresent;
    }

    public void setAveragePresent(long averagePresent) {
        this.averagePresent = averagePresent;
    }

    public int getAverageAbsent() {
        return averageAbsent;
    }

    public void setAverageAbsent(int averageAbsent) {
        this.averageAbsent = averageAbsent;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRedgNo(String redgNo) {
        this.redgNo = redgNo;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setAcademic_year(String academic_year) {
        this.academic_year = academic_year;
    }

    public void setAttendances(Attendance[] attendances) {
        this.attendances = attendances;
    }

    public String getName() {
        return name;
    }

    public String getRedgNo() {
        return redgNo;
    }

    public String getBranch() {
        return branch;
    }

    public String getAcademic_year() {
        return academic_year;
    }

    public Attendance[] getAttendances() {
        return attendances;
    }

    public Result[] getResult() {
        return result;
    }

    public void setResult(Result[] result) {
        this.result = result;
    }

    public String getOfflineAttendance() {
        return offlineAttendance;
    }

    public void setOfflineAttendance(String offlineAttendance) {
        this.offlineAttendance = offlineAttendance;
    }

    public String getOfflineResult() {
        return offlineResult;
    }

    public void setOfflineResult(String offlineResult) {
        this.offlineResult = offlineResult;
    }
}
