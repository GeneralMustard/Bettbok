package se.umu.saha5924.bettbok;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

/**
 * BiteListFragment is responsible for showing all Bites.
 */
public class BiteListFragment extends Fragment {

    private NavController mNavController;
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater
            , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bite_list, container, false);

        mRecyclerView = v.findViewById(R.id.bite_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // A floating action button that when pressed will
        // create a new bite and navigate to BiteEditFragment.
        FloatingActionButton mAddFab = v.findViewById(R.id.fab_add_bite);
        mAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bite bite = new Bite();
                BiteLab.get(getActivity()).addBite(bite);

                // Add the id of the new Bite as an argument in the Bundle.
                Bundle args = new Bundle();
                args.putSerializable(BiteFragment.ARG_BITE_ID, bite.getId());
                mNavController.navigate(R.id.action_biteListFragment_to_biteEditFragment, args);
            }
        });

        updateUI();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = Navigation.findNavController(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    // Update the list of Bites and create an adapter
    // to reflect the current state of each Bite.
    private void updateUI() {
        List<Bite> bites = BiteLab.get(getActivity()).getBites();
        BiteAdapter mBiteAdapter = new BiteAdapter(bites);
        mRecyclerView.setAdapter(mBiteAdapter);
    }

    // The Adapter for the RecyclerView.
    private class BiteAdapter extends RecyclerView.Adapter<BiteAdapter.BiteViewHolder> {
        private List<Bite> mBite; // The list of Bites the Adapter handles.

        public BiteAdapter(List<Bite> bite) {
            mBite = bite;
        }

        @NonNull
        @Override
        public BiteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_bite, parent, false);
            return new BiteViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull BiteViewHolder holder, int position) {
            // Find the Bite that should be shown on a certain position.
            Bite currentBite = mBite.get(position);

            // Show the information of the Bite
            holder.mPlacementTextView.setText(currentBite.getPlacement());
            Calendar c = currentBite.getCalendar();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH)+1;
            int day = c.get(Calendar.DAY_OF_MONTH);
            holder.mCalendarTextView.setText(getString(R.string.show_date, day, month, year));
        }

        @Override
        public int getItemCount() {
            return mBite.size();
        }

        // The ViewHolder responsible for showing a Bite in the RecyclerView
        private class BiteViewHolder extends RecyclerView.ViewHolder {
            private TextView mPlacementTextView;
            private TextView mCalendarTextView;

            BiteViewHolder(@NonNull View itemView) {
                super(itemView);
                mPlacementTextView = itemView.findViewById(R.id.bite_placement_text_view);
                mCalendarTextView = itemView.findViewById(R.id.bite_date_text_view);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // When a Bite is chosen, pass that Bites id
                        // to BiteFragment as an argument in the Bundle.
                        Bundle args = new Bundle();
                        args.putSerializable(BiteFragment.ARG_BITE_ID
                                , mBite.get(getAbsoluteAdapterPosition()).getId());
                        mNavController.navigate(R.id.action_biteListFragment_to_biteFragment, args);
                    }
                });
            }
        }
    }
}
