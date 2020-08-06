package se.umu.saha5924.bettbok;

import androidx.fragment.app.Fragment;

/**
 * BiteListActivity is responsible for hosting a BiteListFragment.
 */
public class BiteListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new BiteListFragment();
    }

}
