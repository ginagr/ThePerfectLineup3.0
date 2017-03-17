package ggr.tpl7;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.text.ParseException;
import java.util.List;

import ggr.tpl7.model.AthleteLab;
import ggr.tpl7.model.Boat;
import ggr.tpl7.model.BoatLab;

public class BoatListFragment extends Fragment{

    private static final String EXTRA_PORTS = "ggr.tpl17.ports";
    private static final String EXTRA_STARBOARDS = "ggr.tpl17.starboards";
    private static final String EXTRA_COXS = "ggr.tpl17.coxs";

    private static final String EXTRA_BOAT_ID = "ggr.tpl17.current_boat";

    private RecyclerView boatRecyclerView;
    private BoatAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_boat_list, container, false);

        boatRecyclerView = (RecyclerView) view
                .findViewById(R.id.boat_recycler_view);
        boatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));



        try {
            updateUI();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            updateUI();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateUI() throws ParseException {
        BoatLab boatLab = BoatLab.get(getActivity());
        List<Boat> boat = boatLab.getBoats();

        if (adapter == null) {
            adapter = new BoatAdapter(boat);
            boatRecyclerView.setAdapter(adapter);
        } else {
            adapter.setBoats(boat);
            adapter.notifyDataSetChanged();
        }
    }

    private class BoatHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Button boatButtonView;
        private ImageView editBoatImageView;

        private Boat boat;

        public BoatHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            boatButtonView = (Button) itemView.findViewById(R.id.boat_button_fragment);
            editBoatImageView = (ImageView) itemView.findViewById(R.id.boat_list_edit);
        }

        public void bindBoat(Boat bBoat) {
            boat = bBoat;
            String html = boat.getName() + "\n" + boat.getBoatSize().toString();
            boatButtonView.setText(html);

            editBoatImageView.setOnClickListener(this);

            boatButtonView.setOnClickListener(this);

            if(boat.isCurrent()){
                boatButtonView.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorAccent));
            } else {
                boatButtonView.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorPrimaryDark));
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.boat_list_edit:
                    Intent i = BoatPagerActivity.newIntent(getActivity(), boat.getId());
                    startActivity(i);
                    break;
                case R.id.boat_button_fragment:
                    BoatLab.get(getActivity()).changeCurrentBoat(boat);
                    i = new Intent(getActivity(), LineupActivity.class);
                    i.putExtra(EXTRA_BOAT_ID, boat.getId());
                    Log.e("BoatListFragment", "Switching to boat with id: " + boat.getId() + "   " + boat.getName());
                    startActivity(i);
                    break;
            }
        }
    }

    private class BoatAdapter extends RecyclerView.Adapter<BoatHolder> {

        private List<Boat> boats;

        public BoatAdapter(List<Boat> lBoat) {
            boats = lBoat;
        }

        @Override
        public BoatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_boat, parent, false);
            return new BoatHolder(view);
        }

        @Override
        public void onBindViewHolder(BoatHolder holder, int position) {
            Boat boat = boats.get(position);
            holder.bindBoat(boat);
        }

        @Override
        public int getItemCount() {
            return boats.size();
        }

        public void setBoats(List<Boat> boats) {
            this.boats = boats;
        }
    }
}
