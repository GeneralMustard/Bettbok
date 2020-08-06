package se.umu.saha5924.bettbok;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.UUID;

public class BiteActivity extends AppCompatActivity {

    private static final String EXTRA_BITE_ID = "bite_id";

    public static Intent newIntent(Context packageContext, UUID id) {
        Intent intent = new Intent(packageContext, BiteActivity.class);
        intent.putExtra(EXTRA_BITE_ID, id);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        UUID biteId = (UUID) getIntent().getSerializableExtra(EXTRA_BITE_ID);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment f = BiteFragment.newInstance(biteId);
        ft.add(R.id.fragment_container, f);
        ft.commit();
    }
}
