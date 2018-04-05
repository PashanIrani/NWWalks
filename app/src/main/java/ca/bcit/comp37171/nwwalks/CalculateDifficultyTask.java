package ca.bcit.comp37171.nwwalks;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by pashan on 2018-03-22.
 */


public class CalculateDifficultyTask extends AsyncTask<LatLng, Void, Double> {
    String TAG = CalculateDifficultyTask.class.getName();
    Contours contours;
    private AsyncResponse listener = null; //

    public void setListener(AsyncResponse listener) {
        this.listener = listener;
    }

    public CalculateDifficultyTask(Contours contours) {
        super();
        this.contours = contours;
    }


    protected Double doInBackground(LatLng... polyCodes) {
        double result = 0.0;
        //for (LatLng polyCode[] : polyCodes) {
            result += performCalc(polyCodes);
        //}
        return result;
    }


    protected void onPostExecute(Double result) {
        listener.processFinish(result);
    }

    /**
     * Performs calculation for polyCodes
     */
    private double performCalc(LatLng[] polyCodes) {
        int total_diff_in_elevation = 0;
        for (LatLng latLng : polyCodes) {



            int prev_elevation = 0;

            // loop through turn points adding absolute diffs up

           // for (LatLng latLng : al) {

                int current_el = contours.getElevation(latLng);

                if (current_el != -1) {
                    total_diff_in_elevation += Math.abs(current_el - prev_elevation);
                    prev_elevation = current_el;
                }
                //Log.v(TAG, "FOUND: " +current_el + ", total diff: " + total_diff_in_elevation);

            //}
        }
        return total_diff_in_elevation;
    }

}
