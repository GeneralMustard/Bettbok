package se.umu.saha5924.bettbok;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Bite {

    private UUID mId;
    private Calendar mCalendar;
    private String mPlacement;

    public Bite() {
        this(UUID.randomUUID());
    }

    public Bite(UUID id) {
        mId = id;
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(new Date());
    }

    public String getImageFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    public UUID getId() {
        return mId;
    }

    public Calendar getCalendar() {
        return mCalendar;
    }

    public void setCalendar(Calendar c) {
        mCalendar = c;
    }

    public String getPlacement() {
        return mPlacement;
    }

    public void setPlacement(String p) {
        mPlacement = p;
    }
}
