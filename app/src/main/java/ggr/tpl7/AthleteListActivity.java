package ggr.tpl7;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import static ggr.tpl7.R.layout.bottom_menu;

public class AthleteListActivity extends SingleFragmentActivity implements View.OnClickListener {

    Button alphabetical;
    Button notBoated;
    LayoutInflater inflater;
    View firstView;

    @Override
    protected Fragment createFragment() {
        sort();
        return new AthleteListFragment();
    }

    public void sort(){
        Log.e("AthleteListActivity", "Called sort");

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        firstView = inflater.inflate(R.layout.bottom_menu, null);
        alphabetical = (Button) firstView.findViewById(R.id.alphabetical_button);
        notBoated = (Button) firstView.findViewById(R.id.not_boated_button);

//        alphabetical.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Log.e("AthleteListActivity", "Sorting alphabetically");
//                alphabetical.setPressed(true);
//                notBoated.setPressed(false);
//            }
//        });
//        notBoated.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Log.e("AthleteListActivity", "Sorting boated");
//                notBoated.setPressed(true);
//                alphabetical.setPressed(false);
//            }
//        });
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.alphabetical_button:
//                Log.e("AthleteListActivity", "Sorting alphabetically");
//                alphabetical.setPressed(true);
//                notBoated.setPressed(false);
//                break;
//            case R.id.not_boated_button:
//                Log.e("AthleteListActivity", "Sorting boated");
//                notBoated.setPressed(true);
//                alphabetical.setPressed(false);
//                break;
//        }
    }
}
