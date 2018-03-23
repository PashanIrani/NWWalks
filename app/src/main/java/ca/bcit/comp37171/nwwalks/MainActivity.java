package ca.bcit.comp37171.nwwalks;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        ConnectionCallbacks, OnConnectionFailedListener, FinderListener, OnMarkerClickListener, AsyncResponse {

    private static final String TAG = "MainActivity.java";

    private static final int LOCATION_REQUEST_CODE = 1;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng currentLatLng;
    private GoogleMap map;
    private static ArrayList<Place> places;
    private static Finder finder;

    //private ArrayList<String> numberList = new ArrayList<>();
    Contours contours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //listeners for the widgets in the layout (activity_main.xml)
        addListeners();

        // Obtain the SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        //get notified when the map is ready to be used.
        mapFragment.getMapAsync(this);

        // Create an instance of GoogleAPIClient.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //initializing finder
        finder = new Finder(this.getApplicationContext(), this);

        //loads contours
        new LoadContoursTask(this).execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(this);

        enableCurrentLocation();

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }


    /*
    runs when google play services is connected
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //check permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //gets current location (more like last, technically)
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {

                //captures position in a fancy LatLng obj
                currentLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                //moves Map
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //TODO: get this sorted out
    }

    @Override
    public void onConnectionSuspended(int i) {
        //TODO: get this sorted out
    }

    /*
    enables current location with permission from the user
     */
    private void enableCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "permission not granted");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            Log.v(TAG, "permission granted");
            map.setMyLocationEnabled(true);
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /*
    listeners for the widgets in the current context (activity_main.xml)
     */
    private void addListeners() {
        EditText search_field = findViewById(R.id.search_field);

        // adding listener
        search_field.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                String text = v.getText().toString(); //contains the value of the text field

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    findPlaces(text);
                    handled = true;
                }

                return !handled; //have to return the opposite, because if it's successful the keyboard should hide. But for some strange reason, it hides the keyboard if false is returned (which is what we want if our action works)
            }
        });

    }

    /*
    searches with the use fo the keyword
     */
    private void findPlaces(String keyword) {
        finder.search(currentLatLng, keyword);
    }

    /**
    This will run when we receive a response from the finder object above.
     */
    @Override
    public void placesFound(ArrayList<Place> places) {
        this.places = places;
        addPlaceMarkers();
    }

    /**
     * Adds polyline to the map
     */
    @Override
    public void directionsFound(String[] p) {
        PolylineOptions polylineOptions = new PolylineOptions();
        List<LatLng> al;

        //start calculation of path
        CalculateDifficultyTask calculateDifficultyTask = new CalculateDifficultyTask(contours);
        calculateDifficultyTask.setListener(this);
        calculateDifficultyTask.execute(p);

        //add polycodes to polyline
        for (int i = 0; i < p.length; i++) {
            al = PolyUtil.decode(p[i]);
            polylineOptions.addAll(al);
        }

        polylineOptions.width(16); //just a random number...

        polylineOptions.color(getResources().getColor(R.color.colorPrimary));

        //adds polyline to map
        map.addPolyline(polylineOptions);
    }

    /**
     * adds markers from the places array list
     */
    private void addPlaceMarkers() {
        map.clear();

        for (Place p : this.places) {

            //adds marker with position, and title
            map.addMarker(new MarkerOptions()
                    .position(p.getLocation())
                    .title(p.getName()));
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        //don't do anything if marker is null :P
        if (marker ==  null) return false;

        Place clickedPlace = getPlace(marker.getPosition()); //gets place

        if (clickedPlace != null) showLocationDetails(clickedPlace);  //shows location details, "the popup"

        //sets bounds to include location and end point in view
        LatLngBounds bounds = new LatLngBounds.Builder()
                                .include(marker.getPosition())
                                .include(currentLatLng)
                                .build();

        //properties for CameraUpdateFactory
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (height * 0.10); // offset from edges of the map 20% of screen

        //sets new camera
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        //updates camera
        map.animateCamera(cameraUpdate);
        return true;
    }

    private void showLocationDetails(Place p) {
        //TODO: re-make this so there is no black overlay
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.place_details, null);

        PlaceDetailsDialog mapDetailsFragment = new PlaceDetailsDialog(this, v);

        mapDetailsFragment.setName(p.getName());
        mapDetailsFragment.setDistance(p.getDistanceFromCurrentLocation());
        mapDetailsFragment.show();

        finder.getDirections(p, currentLatLng);
    }

    /**
     * Gets place at LatLng
     */
    private Place getPlace(LatLng latLng) {

        for (Place p : this.places)
            if (p.getLocation().equals(latLng))
                return p;

        return null;
    }

    /**
     * runs when difficulty of current route has been calculated
     */
    @Override
    public void processFinish(Double output) {
        Log.v(TAG, "RESULT OF CALCULATION: " + output);
    }

    /**
     * runs counters are loaded
     */
    @Override
    public void processFinish(Contours output) {
        this.contours = output;
        Log.v(TAG, "Contours Loading Finished");
    }
}
