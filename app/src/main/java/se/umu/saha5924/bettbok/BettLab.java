package se.umu.saha5924.bettbok;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BettLab {

    private static List<Bett> mBett;

    private static BettLab bettLab;

    public static BettLab get(Context context) {
        if (bettLab == null) {
            bettLab = new BettLab(context);
        }
        return bettLab;
    }
    private BettLab(Context context) {
        mBett = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Bett bett = new Bett();
            bett.setmPlacering("Bett #" + i);
            mBett.add(bett);
        }
    }

    public List<Bett> getAllBett() {
        return mBett;
    }

    public static Bett getBett(UUID id) {
        for (Bett b : mBett) {
            if (b.getmId().equals(id)) return b;
        }
        return null;
    }

}
