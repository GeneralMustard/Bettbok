package se.umu.saha5924.bettbok;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;
import java.util.UUID;

public class BitePagerActivity extends AppCompatActivity {

    private static final String EXTRA_BITE_ID = "se.umu.saha5924.bettbok.bite_id";

    private ViewPager2 mViewPager;
    private FragmentStateAdapter mPagerAdapter;
    private List<Bite> mBite;

    public static Intent newIntent(Context packageContext, UUID id) {
        Intent intent = new Intent(packageContext, BitePagerActivity.class);
        intent.putExtra(EXTRA_BITE_ID, id);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bite_pager);

        UUID bettId = (UUID) getIntent().getSerializableExtra(EXTRA_BITE_ID);
        mBite = BiteLab.get(this).getBites();

        mPagerAdapter = new ScreenSlidePagerAdapter(this);
        mViewPager = findViewById(R.id.bite_view_pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(findCurrentItem(bettId));
    }

    private int findCurrentItem(UUID id) {
        for (int i = 0; i < mBite.size(); i++) {
            if (mBite.get(i).getId().equals(id)) return i;
        }
        return 0;
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {

        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Bite bite = mBite.get(position);
            return BiteFragment.newInstance(bite.getId());
        }

        @Override
        public int getItemCount() {
            return mBite.size();
        }
    }
}
