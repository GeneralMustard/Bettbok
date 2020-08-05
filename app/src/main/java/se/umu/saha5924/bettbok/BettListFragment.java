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

import se.umu.saha5924.bettbok.database.BiteDbSchema;

/**
 * BettListFragment is responsible for the Fragment connected to a list of Betts.
 */
public class BettListFragment extends Fragment {

    private RecyclerView mBettRecyclerView;
    private BettAdapter mBettAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bett_list, container, false);

        mBettRecyclerView = v.findViewById(R.id.bett_recycler_view);
        mBettRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return v;
    }

    private void updateUI() {
        List<Bett> bites = BettLab.get(getActivity()).getBites();

        if (mBettAdapter == null) {
            // A new adapter is needed.
            mBettAdapter = new BettAdapter(bites);
            mBettRecyclerView.setAdapter(mBettAdapter);
        } else {
            // An adapter already exists and is updated to reflect possible changes.
            mBettAdapter.setBites(bites);
            mBettAdapter.notifyDataSetChanged();
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
            case R.id.new_bite:
                Bett bite = new Bett();
                BettLab.get(getActivity()).addBite(bite);
                Intent intent = BettPagerActivity.newIntent(getActivity(), bite.getmId());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class BettAdapter extends RecyclerView.Adapter<BettAdapter.BettViewHolder> {
        private List<Bett> mBett;

        public BettAdapter(List<Bett> bett) {
            mBett = bett;
        }

        @NonNull
        @Override
        public BettViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bett, parent, false);
            return new BettViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull BettViewHolder holder, int position) {
            Bett currentBett = mBett.get(position);
            holder.mPlaceringTextView.setText(currentBett.getmPlacering());

            Calendar c = currentBett.getmDatum();
            String date = "Den " + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) +
                    " -" + c.get(Calendar.YEAR); //TODO
            holder.mDatumTextView.setText(date);
        }

        @Override
        public int getItemCount() {
            return mBett.size();
        }

        /**
         * Replaces the Bites the Adapter shows.
         *
         * @param bites The Bites the Adapter should show.
         */
        public void setBites(List<Bett> bites) {
            mBett = bites;
        }

        private class BettViewHolder extends RecyclerView.ViewHolder {
            private TextView mPlaceringTextView;
            private TextView mDatumTextView;

            BettViewHolder(@NonNull View itemView) {
                super(itemView);
                mPlaceringTextView = itemView.findViewById(R.id.bett_placering_text_view);
                mDatumTextView = itemView.findViewById(R.id.bett_datum_text_view);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = BettPagerActivity.newIntent(getActivity(),
                                mBett.get(getAbsoluteAdapterPosition()).getmId());
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
