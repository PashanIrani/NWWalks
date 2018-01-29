package ca.bcit.comp37171.nwwalks;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Iterator;
/**
 * Created by pashan on 2018-01-17.
 */

public class Finder {

    private static final String TAG = "Finder.java";
    private static String key;
    private static final int RADIUS = 20000;
    private Context context;
    private ArrayList<Place> places = new ArrayList<>();
    private FinderListener listener;
    private static RequestQueue queue;
    public Finder(Context c, FinderListener finderListener) {
        context = c;
        listener = finderListener;
        key = context.getString(R.string.google_maps_key); //getting the google API key
    }

    public void search(LatLng searchAround, String keyword) {
        String currentLocation = searchAround.latitude + "," + searchAround.longitude;
        String param = "location=" + currentLocation + "&radius=" + RADIUS + "&keyword=" + keyword + " &key=" + key;

        places.clear(); //getting ready to store for a new search

        queue = Volley.newRequestQueue(context);

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + param;

        //req desc, "not actually being called at this instance"
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handleRes(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley Error:", error);
                handleError();
            }
        });

        //added to queue, will run when the previous requests are complete.
        queue.add(stringRequest);
    }

    void handleRes(String response) {
        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
        JsonElement res = jsonObject.get("results");
        Iterator<JsonElement> i;

        if (res instanceof JsonArray) {
            i = ((JsonArray) res).iterator();
        } else {
            handleError();
            return;
        }


        while (i.hasNext()) {
            JsonObject j = new JsonParser().parse(i.next().toString()).getAsJsonObject();
            JsonObject placeLocation = j.get("geometry").getAsJsonObject().get("location").getAsJsonObject();
            String placeName = j.get("name").getAsString();

            Place p = new Place();

            p.setLocation(placeLocation.get("lat").getAsDouble(), placeLocation.get("lng").getAsDouble());
            p.setName(placeName);
            p.setDistanceFromCurrentLocation("test");
            places.add(p);
        }

        listener.placesFound(places); //runs the placesFound method in the listener object
    }

    private void handleError() {
        Log.d(TAG, "an error occurred");
    }

    void getDirections(Place p, LatLng origin){
        queue = Volley.newRequestQueue(context);

        String params = "origin=" + origin.latitude + "," + origin.longitude
                +"&destination=" + p.getLocation().latitude + "," + p.getLocation().longitude
                +"&mode=walking";

        String url = "https://maps.googleapis.com/maps/api/directions/json?" + params;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dealWithDirections(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley Error:", error);
                handleError();
            }
        });

        queue.add(stringRequest);
    }

    void dealWithDirections(String response) {

        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
        Log.v(TAG, jsonObject.toString());
        JsonArray routes = jsonObject.get("routes").getAsJsonArray();
        String p = "";
        for (int i = 0; i < routes.size(); i++) {
            p = routes.get(i).getAsJsonObject().get("overview_polyline").getAsJsonObject().get("points").toString();
            Log.v(TAG, p);
        }


        listener.directionsFound(p);
    }
}

