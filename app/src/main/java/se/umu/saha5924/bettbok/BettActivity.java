package se.umu.saha5924.bettbok;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.util.UUID;


public class BettActivity extends SingleFragmentActivity {

    private static final String EXTRA_BETT_ID = "se.umu.saha5924.bettbok.bett_id";

    @Override
    protected Fragment createFragment() {
        UUID bettId = (UUID) getIntent().getSerializableExtra(EXTRA_BETT_ID);
        return BettFragment.newInstance(bettId);
    }

    public static Intent newIntent(Context packageContext, UUID id) {
        Intent intent = new Intent(packageContext, BettActivity.class);
        intent.putExtra(EXTRA_BETT_ID, id);
        return intent;
    }

}