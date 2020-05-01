package mohit.codex_iter.www.awol.model;

public class Lecture {
    private String sem;
    private String subject;
    private String name;
    private String link;

    public Lecture(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public Lecture (String subject) {
        this.subject = subject;
    }
    public String getSem() {
        return sem;
    }

    public void setSem(String sem) {
        this.sem = sem;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


}
