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
    private Contours contours;
    private boolean selected = false;
    private MainActivity listner;
    int color;
    public Route(PolylineOptions polyline, Contours contours, MainActivity listner) {
        this.difficulty = -1;
        this.polyline = polyline;
        this.contours = contours;
        this.listner = listner;
        CalculateDifficultyTask calculateDifficultyTask = new CalculateDifficultyTask(contours);
        calculateDifficultyTask.setListener(this);
        calculateDifficultyTask.execute(polyline.getPoints().toArray(new LatLng[]{}));

    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
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

    public void setDifficulty(Contours contours) {
        CalculateDifficultyTask calculateDifficultyTask = new CalculateDifficultyTask(contours);
        calculateDifficultyTask.setListener(this);
        calculateDifficultyTask.execute(polyline.getPoints().toArray(new LatLng[]{}));
    }

    /**
     * runs when difficulty of current route has been calculated
     */
    @Override
    public void processFinish(Double output) {
        Log.v("Route.java", "RESULT OF CALCULATION: " + output);
        this.difficulty = output;
        listner.drawRoutes();
        //TextView distance = findViewById(R.id.distance);
        //String text = "" + output;
        //distance.setText(text);
    }

    @Override
    public void processFinish(Contours output) {
        // nothing
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
