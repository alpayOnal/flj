package com.muatik.flj.flj.UI.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.adapters.PlacesAutoCompleteAdapter;
import com.muatik.flj.flj.UI.entities.Account;
import com.muatik.flj.flj.UI.entities.AccountManager;
import com.muatik.flj.flj.UI.entities.Job;
import com.muatik.flj.flj.UI.entities.Jobs;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;

public class PostJob extends DetailActivity {

    private PlacePicker.IntentBuilder builder;
    private PlacesAutoCompleteAdapter mPlacesAdapter;
    private Button pickerBtn;
    private AutoCompleteTextView myLocation;
    private static final int PLACE_PICKER_FLAG = 1;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(41.0082376,28.9783589), new LatLng(42.0082376,28.9783589));
    protected GoogleApiClient mGoogleApiClient;

    private ProgressDialog loadingProgress;
    private static Job newJob;

    @BindView(R.id.title) EditText newjob_title;
    @BindView(R.id.description)  EditText newjob_description;
    @BindView(R.id.country)  EditText newjob_country;
    @BindView(R.id.city)  EditText newjob_city;
    @BindView(R.id.employer)  EditText newjob_employer;
    @BindView(R.id.saveJob) Button button_saveJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .build();

        setContentView(R.layout.post_job_activity);
        init();

        getSupportActionBar().setTitle(getResources().getString(R.string.post_job_activity_title));
        loadingProgress = new ProgressDialog(this);



        builder = new PlacePicker.IntentBuilder();
        myLocation = (AutoCompleteTextView) findViewById(R.id.myLocation);
        mPlacesAdapter = new PlacesAutoCompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_GREATER_SYDNEY, null);
        myLocation.setOnItemClickListener(mAutocompleteClickListener);
        myLocation.setAdapter(mPlacesAdapter);
        pickerBtn = (Button) findViewById(R.id.pickerBtn);
        pickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loadingProgress.setMessage(getResources().getString(R.string.post_job_activity_loading_map_message));
                    loadingProgress.show();
                    builder = new PlacePicker.IntentBuilder();
                    Intent intent = builder.build(PostJob.this);
                    // Start the Intent by requesting a result, identified by a request code.
                    startActivityForResult(intent, PLACE_PICKER_FLAG);
                } catch (GooglePlayServicesRepairableException e) {
                    GooglePlayServicesUtil
                            .getErrorDialog(e.getConnectionStatusCode(), PostJob.this, 0);
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(PostJob.this, "Google Play Services is not available.",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        newJob = new Job();
        newJob.setLatLong(0,0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadingProgress.cancel();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PLACE_PICKER_FLAG:
                    Place place = PlacePicker.getPlace(data, this);
                    LatLng latLng = place.getLatLng();
                    newJob.setLatLong(latLng.latitude, latLng.longitude);
                    myLocation.setText(place.getName() + ", " + place.getAddress());
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mPlacesAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e("place", "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
        }
    };


    @OnClick(R.id.saveJob)
    public void saveJob() {
        String title = newjob_title.getText().toString();
        String description = newjob_description.getText().toString();
        String country = newjob_country.getText().toString();
        String city = newjob_city.getText().toString();
        String employer = newjob_employer.getText().toString();

        newJob.setDescription(description);
        newJob.setTitle(description);
        newJob.setEmployer(employer);
        newJob.setCountry(country);
        newJob.setCity(city);
        newJob.setUser(AccountManager.getAuthenticatedAccount().getId());

        if (validateForm()) {
            Jobs jobs = new Jobs();
            jobs.insertJob(newJob);
            loadingProgress.setMessage(getResources().getString(R.string.post_job_saving_message));
            loadingProgress.show();
        }
    }


    public boolean validateForm() {

        String title = newjob_title.getText().toString();
        String description = newjob_description.getText().toString();
        String country = newjob_country.getText().toString();
        String city = newjob_city.getText().toString();
        String employer = newjob_employer.getText().toString();

        if (title.isEmpty() || description.isEmpty() || country.isEmpty()
                || city.isEmpty() || employer.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.post_job_fields_required_message),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Subscribe
    public void jobSuccessfullyInserted(Jobs.EventOnInsert event){
        loadingProgress.cancel();
        Toast.makeText(
                getApplicationContext(),
                getResources().getString(R.string.post_job_added),
                Toast.LENGTH_LONG
        ).show();
        this.finish();
    }

    @Subscribe
    public void jobInsertingFailure(Jobs.EventOnInsertFailure event){
        loadingProgress.cancel();
        Log.d("error", event.error.getMessage());
        Log.d("error", event.message);
        Toast.makeText(
                getApplicationContext(),
                event.message,
                Toast.LENGTH_LONG
        ).show();
    }
}
