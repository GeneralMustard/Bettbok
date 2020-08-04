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

public class BettPagerActivity extends AppCompatActivity {

    private static final String EXTRA_BETT_ID = "se.umu.saha5924.bettbok.bett_id";

    private ViewPager2 mViewPager;
    private FragmentStateAdapter mPagerAdapter;
    private List<Bett> mBett;

    public static Intent newIntent(Context packageContext, UUID id) {
        Intent intent = new Intent(packageContext, BettPagerActivity.class);
        intent.putExtra(EXTRA_BETT_ID, id);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bett_pager);

        UUID bettId = (UUID) getIntent().getSerializableExtra(EXTRA_BETT_ID);
        mBett = BettLab.get(this).getAllBett();

        mPagerAdapter = new ScreenSlidePagerAdapter(this);
        mViewPager = findViewById(R.id.bett_view_pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(findCurrentItem(bettId));
    }

    private int findCurrentItem(UUID id) {
        for (int i = 0; i < mBett.size(); i++) {
            if (mBett.get(i).getmId().equals(id)) return i;
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
            Bett bett = mBett.get(position);
            return BettFragment.newInstance(bett.getmId());
        }

        @Override
        public int getItemCount() {
            return mBett.size();
        }
    }
}
