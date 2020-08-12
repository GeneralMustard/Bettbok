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

    public String getImageFilename(int i) {
        return "IMG_" + getId().toString() + i + ".jpg";
    }

    public int getDaysSinceBite(Calendar now) {
        int days = 0;
        long nowMilli = now.getTimeInMillis();
        long biteMilli = mCalendar.getTimeInMillis();

        if (nowMilli > biteMilli)
            days = (int) ((nowMilli-biteMilli) / (1000*60*60*24));

        return days;
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
