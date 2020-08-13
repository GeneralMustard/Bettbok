package se.umu.saha5924.bettbok;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Bite {

    private UUID mId;           // Id of the Bite.
    private Calendar mCalendar; // Calendar representing the time of the Bite.
    private String mPlacement;  // String representing where on the body the Bite was found.
    private String mStage;      // String representing the stage of the tick responsible for the Bite.

    /**
     * Constructor for a Bite.
     * The time is set to the current time.
     */
    public Bite() {
        this(UUID.randomUUID());
    }

    /**
     * Constructor for a Bite with a given id.
     *
     * @param id The id of the Bite.
     */
    public Bite(UUID id) {
        mId = id;
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(new Date());
        mStage = "Inget valt";
    }

    /**
     * Will create a String used for giving a name to an image for the Bite.
     * The name will contain the id of the Bite and the given int.
     *
     * @param i The int that will be added to the name.
     * @return The String representing the name of the image of a Bite.
     */
    public String getImageFilename(int i) {
        return "IMG_" + getId().toString() + i + ".jpg";
    }

    /**
     * Will calculate the number of days from the Bite to current time.
     * If the current time is before the time of the Bite 0 will be returned.
     *
     * @return The number of days between the Bite and current time.
     */
    public int getDaysSinceBite() {
        int days = 0;
        long nowMilli = Calendar.getInstance().getTimeInMillis();
        long biteMilli = mCalendar.getTimeInMillis();

        if (nowMilli > biteMilli)
            days = (int) ((nowMilli-biteMilli) / (1000*60*60*24));

        return days;
    }

    public String getStage() {
        return mStage;
    }

    public void setStage(String s) {
        mStage = s;
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
