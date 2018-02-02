package ca.bcit.comp37171.nwwalks;

import android.Manifest;
import android.content.pm.PackageManager;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        ConnectionCallbacks, OnConnectionFailedListener, FinderListener, OnMarkerClickListener {

    private static final String TAG = "MainActivity.java";
    private static final int LOCATION_REQUEST_CODE = 1;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng currentLatLng;
    private GoogleMap map;
    private static ArrayList<Place> places;
    private static Finder finder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //listeners for the widgets in the layout (activity_main.xml)
        addListners();

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

        finder = new Finder(this.getApplicationContext(), this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(this);
        enableCurrentLocation();
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
    private void addListners() {
        EditText search_field = (EditText) findViewById(R.id.search_field);

        // adding listener
        search_field.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                String text = v.getText().toString(); //contains the value of the text field

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.v(TAG, text);
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

    /*
    since we have to wait for the response in the 'Finder' object to complete, this will run when the response is complete. #TODO: rephrase this w/ the help of the white man #justin
     */
    @Override
    public void placesFound(ArrayList<Place> places) {
        Log.v(TAG, "placesFound");
        this.places = places;
        addPlaceMarkers();
    }

    @Override
    public void directionsFound(String p) {
        PolyUtil.decode(p);
    }

    /*
    adds markers from the places array list
     */
    private void addPlaceMarkers() {
        map.clear();
        Iterator<Place> i = this.places.iterator();
        Log.v(TAG, "" + i.hasNext());
        while (i.hasNext()) {
            Place p = i.next();
            Log.v(TAG, p.toString());
            map.addMarker(new MarkerOptions()
                    .position(p.getLocation())
                    .title(p.getName()));
        }

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Place clickedPlace = getPlace(marker.getPosition());
        if (clickedPlace != null) showLocationDetails(clickedPlace);
        return true;
    }

    private void showLocationDetails(Place p) {
        //builds view
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.place_details, null);

        PlaceDetailsDialog mapDetailsFramgment = new PlaceDetailsDialog(this, v);
        mapDetailsFramgment.setName(p.getName());
        mapDetailsFramgment.setDistance(p.getDistanceFromCurrentLocation());
        mapDetailsFramgment.show();

        finder.getDirections(p, currentLatLng);
    }

    private Place getPlace(LatLng latLng) {
        for (Place p : this.places) {
            if (p.getLocation().equals(latLng))
                return p;
        }
        return null;
    }
}
