package ca.bcit.comp37171.nwwalks;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by pashan on 2018-04-05.
 */

public class Route implements AsyncResponse{
    private PolylineOptions polyline;
    private double difficulty;
    private static final String TAG = "Route.java";

    public Route(PolylineOptions polyline, Contours contours) {
        this.polyline = polyline;
        CalculateDifficultyTask calculateDifficultyTask = new CalculateDifficultyTask(contours);
        calculateDifficultyTask.setListener(this);
        calculateDifficultyTask.execute(polyline.getPoints().toArray(new LatLng[]{}));

    }

    public PolylineOptions getPolyline() {
        return polyline;
    }

    public void setPolyline(PolylineOptions polyline) {
        this.polyline = polyline;
    }

    public double getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(double difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * runs when difficulty of current route has been calculated
     */
    @Override
    public void processFinish(Double output) {
        Log.v("Route.java", "RESULT OF CALCULATION: " + output);
        this.difficulty = output;
        //TextView distance = findViewById(R.id.distance);
        //String text = "" + output;
        //distance.setText(text);
    }

    @Override
    public void processFinish(Contours output) {
        // nothing
    }
}
