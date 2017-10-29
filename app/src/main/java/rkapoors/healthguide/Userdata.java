package rkapoors.healthguide;

/**
 * Created by KAPOOR's on 03-10-2017.
 */
public class Userdata {

    public String time;
    public String value;
    public String comment;
    public String dosage;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Userdata() {
    }

    public Userdata(String time, String comment, String value, String dosage) {
        this.time = time;
        this.value = value;
        this.comment=comment;
        this.dosage=dosage;
    }
}