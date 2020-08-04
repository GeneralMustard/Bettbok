package se.umu.saha5924.bettbok;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/*
    SingleFragmentActivity is a class that contains
    the functionality needed for hosting fragments.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    /**
     * Classes that extends SingleFragmentActivity needs to override
     * this method to return the Fragment the class is hosting.
     * @return The Fragment to be hosted.
     */
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}

