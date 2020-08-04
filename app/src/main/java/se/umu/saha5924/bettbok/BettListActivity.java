package se.umu.saha5924.bettbok;

import androidx.fragment.app.Fragment;

public class BettListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new BettListFragment();
    }

}
