package ggr.tpl7;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

import ggr.tpl7.model.Athlete;
import ggr.tpl7.model.AthleteLab;
import ggr.tpl7.model.Boat;
import ggr.tpl7.model.BoatLab;
import ggr.tpl7.model.BoatSize;

public class BoatFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String ARG_BOAT_ID = "boat_id";
    private static final String EXTRA_CURRENT_BOAT = "ggr.tpl17.current_boat";

    private Boat boat;

    public static BoatFragment newInstance(UUID boatId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOAT_ID, boatId);
        BoatFragment fragment = new BoatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID boatId = (UUID) getArguments().getSerializable(ARG_BOAT_ID);
        boat = BoatLab.get(getActivity()).getBoat(boatId);
    }

    @Override
    public void onPause() {
        super.onPause();
        super.onPause();

        BoatLab.get(getActivity())
                .updateBoat(boat);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_boat, container, false);

        EditText nameField = (EditText) v.findViewById(R.id.boat_name_field);
        nameField.setText(boat.getName());
        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boat.setName(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        Spinner boatSizeSpinner = (Spinner) v.findViewById(R.id.boat_size_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.boat_size_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boatSizeSpinner.setAdapter(adapter);

        if(!boat.getBoatSize().toString().equals(" ")){
            int index = 0;

            for (int i=0;i<boatSizeSpinner.getCount();i++){
                if (boatSizeSpinner.getItemAtPosition(i).equals(boat.getBoatSize().toString())){
                    index = i;
                }
            }
            boatSizeSpinner.setSelection(index);
        }

        boatSizeSpinner.setOnItemSelectedListener(this);


        Button deleteButton = (Button) v.findViewById(R.id.delete_boat_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Are you sure you want to delete this boat?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent i = new Intent(getActivity(), LineupActivity.class);
                                startActivity(i);
                                BoatLab.get(getActivity()).deleteBoat(boat.getId());
                                Toast.makeText(getActivity(),"Deleted Boat",Toast.LENGTH_LONG).show();
                            }
                        });
                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_boat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_to_boats:
                Intent intent = new Intent(getActivity(), LineupActivity.class);
               // intent.putExtra(EXTRA_CURRENT_BOAT, boat.getId());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0: //8+
                boat.setCox(true);
                boat.setBoatSize(BoatSize.EIGHT);
                Log.d("BoatFragment", "selected 8+");
                break;
            case 1: //4+
                boat.setCox(true);
                boat.setBoatSize(BoatSize.FOUR);
                Log.d("BoatFragment", "selected 4+");
                break;
            case 2: //4x
                boat.setCox(false);
                boat.setBoatSize(BoatSize.QUAD);
                Log.d("BoatFragment", "selected 4x");
                break;
            case 3: //2+
                boat.setCox(false);
                boat.setBoatSize(BoatSize.PAIR);
                Log.d("BoatFragment", "selected 2+");
                break;
            case 4: //2x
                boat.setCox(false);
                boat.setBoatSize(BoatSize.DOUBLE);
                Log.d("BoatFragment", "selected 2x");
                break;
            case 5: //1x
                boat.setCox(false);
                boat.setBoatSize(BoatSize.SINGLE);
                Log.d("BoatFragment", "selected 1x");
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
