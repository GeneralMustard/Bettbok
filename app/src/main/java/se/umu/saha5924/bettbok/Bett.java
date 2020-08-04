package se.umu.saha5924.bettbok;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Bett {

    private UUID mId;
    private Calendar mDatum;
    private String mPlacering;

    public Bett() {
        mId = UUID.randomUUID();
        mDatum = Calendar.getInstance();
        mDatum.setTime(new Date());
    }

    public UUID getmId() {
        return mId;
    }

    public void setmId(UUID mId) {
        this.mId = mId;
    }

    public Calendar getmDatum() {
        return mDatum;
    }

    public void setmDatum(Calendar mDatum) {
        this.mDatum = mDatum;
    }

    public String getmPlacering() {
        return mPlacering;
    }

    public void setmPlacering(String mPlacering) {
        this.mPlacering = mPlacering;
    }
}
