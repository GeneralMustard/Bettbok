package se.umu.saha5924.bettbok;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.util.UUID;

public class BiteEditActivity extends SingleFragmentActivity {

    private static final String EXTRA_BITE_ID = "bite_id";

    @Override
    protected Fragment createFragment() {
        UUID biteId;
        if (getIntent() != null) {
            biteId = (UUID) getIntent().getSerializableExtra(EXTRA_BITE_ID);
            return BiteEditFragment.newInstance(biteId);
        }
        return BiteEditFragment.newInstance();
    }

    public static Intent newIntent(Context packageContext, UUID id) {
        Intent intent = new Intent(packageContext, BiteEditActivity.class);
        intent.putExtra(EXTRA_BITE_ID, id);
        return intent;
    }

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, BiteEditActivity.class);
    }

}
