package ggr.tpl7;

import android.app.Activity;
import android.content.ContentResolver;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

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
        UUID athleteId = (UUID) getArguments().getSerializable(ARG_ATHLETE_ID);
        try {
            athlete = AthleteLab.get(getActivity()).getAthlete(athleteId);
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
        twokField.setText(athlete.getTwokMin() + ":" + athlete.getTwokSec());
        twokField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //TODO:set 2k also 6k
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        EditText weightField = (EditText) v.findViewById(R.id.weight_fragment_edit_text);
        weightField.setText(athlete.getWeight());
        weightField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                athlete.setWeight(Integer.parseInt(s.toString()));
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        EditText heightField = (EditText) v.findViewById(R.id.height_fragment_edit_text);
        heightField.setText(athlete.getFeet() +  "' " + athlete.getInches());
        heightField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                athlete.setInches(Integer.parseInt(s.toString()));
                //TODO: change to one height
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radio_fragment);
        if(athlete.getPosition() < 1){
            radioGroup.clearCheck();
        } else {
            switch (athlete.getPosition()){
                case 4 :
                    RadioButton starboard = (RadioButton)v.findViewById(R.id.starboard_radio);
                    starboard.setChecked(true);
                    break;
                case 3:
                    RadioButton port = (RadioButton)v.findViewById(R.id.port_radio);
                    port.setChecked(true);
                    break;
                case 2:
                    RadioButton both = (RadioButton)v.findViewById(R.id.both_radio);
                    both.setChecked(true);
                    break;
                case 1:
                    RadioButton cox = (RadioButton)v.findViewById(R.id.cox_radio);
                    cox.setChecked(true);
                    break;
            }
        }

        //TODO:finish contact athlete
//        Button contactButton = (Button) v.findViewById(R.id.contact_athlete_button);
//        contactButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("text/plain");
//                i.putExtra(Intent.EXTRA_TEXT, getAthleteInfo());
//                i.putExtra(Intent.EXTRA_SUBJECT,
//                        getString(R.string.crime_report_subject));
//                i = Intent.createChooser(i, getString(R.string.send_report));
//
//                startActivity(i);
//            }
//        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        linkButton = (Button)v.findViewById(R.id.link_athlete_contact_button);
        linkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (athlete.getLinkContact() != null) {
            linkButton.setText(athlete.getLinkContact());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            linkButton.setEnabled(false);
        }


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

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.starboard_radio:
                if (checked)
                    athlete.setPosition(4);
                break;
            case R.id.port_radio:
                if (checked)
                    athlete.setPosition(3);
                break;
            case R.id.both_radio:
                if (checked)
                    athlete.setPosition(2);
                break;
            case R.id.cox_radio:
                if (checked)
                    athlete.setPosition(1);
                break;
        }
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

//    private String getAthleteInfo() {
//        String solvedString = null;
//        if (athlete.isSolved()) {
//            solvedString = getString(R.string.crime_report_solved);
//        } else {
//            solvedString = getString(R.string.crime_report_unsolved);
//        }
//        String dateFormat = "EEE, MMM dd";
//        String dateString = DateFormat.format(dateFormat, athlete.getDate()).toString();
//        String suspect = athlete.getSuspect();
//        if (suspect == null) {
//            suspect = getString(R.string.crime_report_no_suspect);
//        } else {
//            suspect = getString(R.string.crime_report_suspect, suspect);
//        }
//        String report = getString(R.string.crime_report, athlete.getTitle(), dateString, solvedString, suspect);
//        return report;
//    }

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
    public void onClick(View v) {

    }
}
