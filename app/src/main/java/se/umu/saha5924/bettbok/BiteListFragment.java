package se.umu.saha5924.bettbok;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

/**
 * BiteListFragment is responsible for the Fragment connected to a list of Bites.
 */
public class BiteListFragment extends Fragment {

    private RecyclerView mBiteRecyclerView;
    private BiteAdapter mBiteAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bett_list, container, false);

        mBiteRecyclerView = v.findViewById(R.id.bett_recycler_view);
        mBiteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return v;
    }

    // Updates the list of Bites to reflect the current state of each Bite.
    private void updateUI() {
        List<Bite> bites = BiteLab.get(getActivity()).getBites();

        if (mBiteAdapter == null) {
            // A new adapter is needed.
            mBiteAdapter = new BiteAdapter(bites);
            mBiteRecyclerView.setAdapter(mBiteAdapter);
        } else {
            // An adapter already exists and is updated to reflect possible changes.
            mBiteAdapter.setBites(bites);
            mBiteAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_bite_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // The user has requested a new Bite.
            case R.id.new_bite:
                Bite bite = new Bite();
                BiteLab.get(getActivity()).addBite(bite);
                Intent intent = BitePagerActivity.newIntent(getActivity(), bite.getId());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // TODO move to own class
    private class BiteAdapter extends RecyclerView.Adapter<BiteAdapter.BiteViewHolder> {
        private List<Bite> mBite;

        public BiteAdapter(List<Bite> bite) {
            mBite = bite;
        }

        @NonNull
        @Override
        public BiteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bite, parent, false);
            return new BiteViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull BiteViewHolder holder, int position) {
            Bite currentBite = mBite.get(position);
            holder.mPlacementTextView.setText(currentBite.getPlacement());

            Calendar c = currentBite.getCalendar();
            String date = "Den " + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) +
                    " -" + c.get(Calendar.YEAR); //TODO
            holder.mCalendarTextView.setText(date);
        }

        @Override
        public int getItemCount() {
            return mBite.size();
        }

        /**
         * Replaces the Bites the Adapter shows.
         *
         * @param bites The Bites the Adapter should show.
         */
        public void setBites(List<Bite> bites) {
            mBite = bites;
        }

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
                        Intent intent = BitePagerActivity.newIntent(getActivity(),
                                mBite.get(getAbsoluteAdapterPosition()).getId());
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
