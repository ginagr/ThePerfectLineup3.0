package ggr.tpl7;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.sheets.v4.SheetsScopes;

import com.google.api.services.sheets.v4.model.*;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ggr.tpl7.model.Athlete;
import ggr.tpl7.model.AthleteLab;
import ggr.tpl7.model.Position;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ReadExcel  extends Activity implements EasyPermissions.PermissionCallbacks {

    private static final String EXTRA_PORTS = "ggr.tpl17.ports";
    private static final String EXTRA_STARBOARDS = "ggr.tpl17.starboards";
    private static final String EXTRA_COXS = "ggr.tpl17.coxs";


    GoogleAccountCredential mCredential;
    private Button callApiButton;
    private Button toRosterButton;
    private TextView outputText;
    private EditText urlText;
    ProgressDialog mProgress;

    private String spreadsheetId = "";

    private ArrayList<String> ports = new ArrayList<>();
    private ArrayList<String> starboards = new ArrayList<>();
    private ArrayList<String> coxs = new ArrayList<>();

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Update Roster";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_roster);

        urlText = (EditText) findViewById(R.id.google_spreadsheet_url);
        urlText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                spreadsheetId = s.toString();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        outputText = (TextView) findViewById(R.id.notification_text_view);

        callApiButton = (Button) findViewById(R.id.update_roster_button);
        callApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spreadsheetId.equals("")){
                    outputText.setText("Please input the spreadsheet id.");
                } else {
                    callApiButton.setEnabled(false);
                    getResultsFromApi();
                    callApiButton.setEnabled(true);
                }
            }
        });

        final Intent i = new Intent(this, AthleteListActivity.class);
        toRosterButton = (Button) findViewById(R.id.to_roster_button);
        toRosterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i);
            }
        });

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Updating Roster...");

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            outputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    outputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                ReadExcel.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("The Perfect Lineup")
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                if (getDataFromApi()){
                    ArrayList<String> temp = new ArrayList<String>();
                    temp.add("Succesfully updated roster");
                    return temp;
                };
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
            return null;
        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         * @return List of names and majors
         * @throws IOException
         */
        private boolean getDataFromApi() throws IOException {
            boolean bool = false;
            String range = "Sheet1!A2:C";
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();

            if (values != null) {
                bool = true;
                for (List row : values) {
                    if(row.size() > 2) {
                        if (!row.get(0).toString().isEmpty()) {
                            ports.add(row.get(0) + "");
                            Log.e("ReadExcel", "Adding port : " + row.get(0).toString());
                        }
                        if (!row.get(1).toString().isEmpty()) {
                            starboards.add(row.get(1) + "");
                            Log.e("ReadExcel", "Adding starboard : " + row.get(1).toString());
                        }
                        if (!row.get(2).toString().isEmpty()) {
                            coxs.add(row.get(2) + "");
                            Log.e("ReadExcel", "Adding cox : " + row.get(2).toString());
                        }
                    } else if(row.size() > 1){
                        if (!row.get(0).toString().isEmpty()) {
                            ports.add(row.get(0) + "");
                            Log.e("ReadExcel", "Adding port : " + row.get(0).toString());
                        }
                        if (!row.get(1).toString().isEmpty()) {
                            starboards.add(row.get(1) + "");
                            Log.e("ReadExcel", "Adding starboard : " + row.get(1).toString());
                        }
                    } else {
                        if (!row.get(0).toString().isEmpty()) {
                            ports.add(row.get(0) + "");
                            Log.e("ReadExcel", "Adding port : " + row.get(0).toString());
                        }
                    }
                }
            }
            return bool;
        }

        @Override
        protected void onPreExecute() {
            outputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                outputText.setText("No results returned.");
            } else {
                List<Athlete> tempAthlete =  AthleteLab.get(getApplicationContext()).getAthletes();
                AthleteLab.get(getApplicationContext()).deleteAthletes();
                for(int i = 0; i < ports.size(); i++){
                    Athlete port = new Athlete();
                    port.setPosition(Position.PORT);
                    port.setFirstName(ports.get(i));
                    AthleteLab.get(getApplicationContext()).addAthlete(port);
                }
                for(int i = 0; i < starboards.size(); i++){
                    Athlete star = new Athlete();
                    star.setPosition(Position.STARBOARD);
                    star.setFirstName(starboards.get(i));
                    AthleteLab.get(getApplicationContext()).addAthlete(star);
                }
                for(int i = 0; i < coxs.size(); i++){
                    Athlete cox = new Athlete();
                    cox.setPosition(Position.COXSWAIN);
                    cox.setFirstName(coxs.get(i));
                    AthleteLab.get(getApplicationContext()).addAthlete(cox);
                }
                if(AthleteLab.get(getApplicationContext()).getAthletes().isEmpty()){
                    for(int i = 0; i < tempAthlete.size(); i++){
                        AthleteLab.get(getApplicationContext()).addAthlete(tempAthlete.get(i));
                    }
                    outputText.setText("Could not update roster");
                } else {
                    outputText.setText(output.get(0));
                }
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            ReadExcel.REQUEST_AUTHORIZATION);
                } else {
                    outputText.setText("The following error occurred:\n" + mLastError.getMessage());
                }
            } else {
                outputText.setText("Request cancelled.");
            }
        }
    }
}