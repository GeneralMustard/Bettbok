package se.umu.saha5924.bettbok;

import androidx.fragment.app.Fragment;

/**
 * BettListActivity is responsible for hosting a BettListFragment.
 */
public class BettListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new BettListFragment();
    }

}
