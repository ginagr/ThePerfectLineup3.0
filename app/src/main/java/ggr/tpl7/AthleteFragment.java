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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import ggr.tpl7.model.Athlete;
import ggr.tpl7.model.AthleteLab;
import ggr.tpl7.model.Position;

public class AthleteFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_ATHLETE_ID = "athlete_id";

    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO= 2;

    private Athlete athlete;
    private File[] photo;
    private Button linkButton;
    private ImageView[] photoView = new ImageView[1];

    public static AthleteFragment newInstance(UUID athleteId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ATHLETE_ID, athleteId);
        AthleteFragment fragment = new AthleteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID athleteId = (UUID) getArguments().getSerializable(ARG_ATHLETE_ID);

        athlete = AthleteLab.get(getActivity()).getAthlete(athleteId);

        photo = AthleteLab.get(getActivity()).getPhotoFiles(athlete);
    }

    @Override
    public void onPause() {
        super.onPause();
        super.onPause();

        AthleteLab.get(getActivity())
                .updateAthlete(athlete);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_athlete, container, false);

        EditText firstNameField = (EditText) v.findViewById(R.id.athlete_first_name);
        firstNameField.setText(athlete.getFirstName());
        firstNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                athlete.setFirstName(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        EditText lastNameField = (EditText) v.findViewById(R.id.athlete_last_name);
        lastNameField.setText(athlete.getLastName());
        lastNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                athlete.setLastName(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        EditText twokField = (EditText) v.findViewById(R.id.twok_fragment_edit_text);
        SimpleDateFormat sdf = new SimpleDateFormat( "HH:mm.ss" );
        if(athlete.getTwok() != null){
            String time = sdf.format(athlete.getTwok().getTime());
            twokField.setText(time);
        }
        twokField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    Date date =new SimpleDateFormat("H:mm.ss").parse(s.toString());
                    athlete.setTwok(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //TODO:set 2k also 6k
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

//        EditText weightField = (EditText) v.findViewById(R.id.weight_fragment_edit_text);
//        try {
//            weightField.setText(athlete.getWeight());
//        }catch (Exception e) {
//            Log.e("AthleteFragment", "Could not find athlete weight: " + athlete.getWeight());
//        }
//        weightField.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                athlete.setWeight(Integer.parseInt(s.toString()));
//            }
//            @Override
//            public void afterTextChanged(Editable s) { }
//        });

//        EditText heightField = (EditText) v.findViewById(R.id.height_fragment_edit_text);
//        if(athlete.getFeet() != 0) { heightField.setText(athlete.getFeet() +  "' " + athlete.getInches()); }
//        heightField.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                athlete.setInches(Integer.parseInt(s.toString()));
//                //TODO: change to one height
//            }
//            @Override
//            public void afterTextChanged(Editable s) { }
//        });

        RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radio_fragment);
        if(athlete.getPosition() == Position.NONE){
            radioGroup.clearCheck();
        } else {
            switch (athlete.getPosition()){
                case STARBOARD:
                    RadioButton starboard = (RadioButton)v.findViewById(R.id.starboard_radio);
                    starboard.setChecked(true);
                    break;
                case PORT:
                    RadioButton port = (RadioButton)v.findViewById(R.id.port_radio);
                    port.setChecked(true);
                    break;
                case BOTH:
                    RadioButton both = (RadioButton)v.findViewById(R.id.both_radio);
                    both.setChecked(true);
                    break;
                case COXSWAIN:
                    RadioButton cox = (RadioButton)v.findViewById(R.id.cox_radio);
                    cox.setChecked(true);
                    break;
            }
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.starboard_radio:
                            athlete.setPosition(Position.STARBOARD);
                        break;
                    case R.id.port_radio:
                            athlete.setPosition(Position.PORT);
                        break;
                    case R.id.both_radio:
                            athlete.setPosition(Position.BOTH);
                        break;
                    case R.id.cox_radio:
                            athlete.setPosition(Position.COXSWAIN);
                        break;
                }
            }
        });


        Button deleteButton = (Button) v.findViewById(R.id.delete_athlete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Are you sure you want to delete this athlete?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent i = new Intent(getActivity(), AthleteListActivity.class);
                                startActivity(i);
                                AthleteLab.get(getActivity()).deleteAthlete(athlete.getId());
                                Toast.makeText(getActivity(),"Deleted Athlete",Toast.LENGTH_LONG).show();
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

//        final Intent pickContact = new Intent(Intent.ACTION_PICK,
//                ContactsContract.Contacts.CONTENT_URI);
//        linkButton = (Button)v.findViewById(R.id.link_athlete_contact_button);
//        linkButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                startActivityForResult(pickContact, REQUEST_CONTACT);
//            }
//        });

//        if (athlete.getLinkContact() != null) {
//            linkButton.setText(athlete.getLinkContact());
//        }

        PackageManager packageManager = getActivity().getPackageManager();
//        if (packageManager.resolveActivity(pickContact,
//                PackageManager.MATCH_DEFAULT_ONLY) == null) {
//            linkButton.setEnabled(false);
//        }


        ImageButton mPhotoButton = (ImageButton) v.findViewById(R.id.athlete_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = photo != null && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = null;

                uri = Uri.fromFile(photo[0]);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        photoView[0] = (ImageView) v.findViewById(R.id.athlete_photo);

        updatePhotoView();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();

            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME,
            };

            ContentResolver resolver = getActivity().getContentResolver();
            Cursor c = resolver
                    .query(contactUri, queryFields, null, null, null);

            try {
                if (c.getCount() == 0) {
                    return;
                }
                c.moveToFirst();

                String con = c.getString(0);
                athlete.setLinkContact(con);
                linkButton.setText(con);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            updatePhotoView();
        }
    }

    private void updatePhotoView() {
        if (photo[0] == null || !photo[0].exists()) {
            photoView[0].setImageDrawable(null);
        } else {

            Bitmap bitmap = getLowQualityBitmapFromFile(photo[0]);

            bitmap = adjustBitmapToCorrectOrientation(photo[0].getPath(), bitmap);

            photoView[0].setImageBitmap(bitmap);
        }
    }

    private Bitmap getLowQualityBitmapFromFile(File file) {
        // Update Photo View With images of a lower quality to save ram
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();

        // Set extra perameters about requested bitmap
        options.inDensity = DisplayMetrics.DENSITY_DEFAULT;
        options.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
        options.inScreenDensity = DisplayMetrics.DENSITY_DEFAULT;
        options.inSampleSize = 2;
        options.inScaled = false;
        options.inMutable=true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bitmap = new BitmapFactory().decodeFile(file.getPath(), options);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);

        try {
            byteArrayOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

        return bitmap;
    }

    private Bitmap adjustBitmapToCorrectOrientation(String photoPath, Bitmap bitmap) {
        ExifInterface ei = null;

        try {
            ei = new ExifInterface(photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(bitmap, 270);

            case ExifInterface.ORIENTATION_NORMAL:

            default:
                break;
        }

        return bitmap;
    }

    /// Rotate and return bitmap by given degree
    private Bitmap rotateImage(Bitmap bitmap, int degree) {
        if (degree > 360) {
            return null;
        }

        // Rotate bitmap by 'degree'
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (ImageView iv : photoView) {
            unbindDrawables(iv);
            iv.setImageDrawable(null);
        }

        System.gc();
    }

    private boolean isPhotoEmpty(File file) {
        return file == null || !file.exists();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_athlete, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_to_roster:
                Intent intent = new Intent(getActivity(), AthleteListActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {

    }
}
