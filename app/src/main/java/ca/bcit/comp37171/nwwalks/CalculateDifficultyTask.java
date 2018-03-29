package ca.bcit.comp37171.nwwalks;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.List;

/**
 * Created by pashan on 2018-03-22.
 */


public class CalculateDifficultyTask extends AsyncTask<String[], Void, Double> {
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


    protected Double doInBackground(String[]... polyCodes) {
        double result = 0.0;
        for (String polyCode[] : polyCodes) {
            result += performCalc(polyCode);
        }
        return result;
    }


    protected void onPostExecute(Double result) {
        listener.processFinish(result);
    }

    /**
     * Performs calculation for polyCodes
     */
    private double performCalc(String[] polyCodes) {
        int total_diff_in_elevation = 0;
        for (String polyCode : polyCodes) {

            List<LatLng> al = PolyUtil.decode(polyCode);

            int prev_elevation = 0;

            // loopp through turn points adding absolute diffs up

            for (LatLng latLng : al) {

                int current_el = contours.getElevation(latLng);

                if (current_el != -1) {
                    total_diff_in_elevation += Math.abs(current_el - prev_elevation);
                    prev_elevation = current_el;
                }
                //Log.v(TAG, "FOUND: " +current_el + ", total diff: " + total_diff_in_elevation);

            }
        }
        return total_diff_in_elevation;
    }

}
