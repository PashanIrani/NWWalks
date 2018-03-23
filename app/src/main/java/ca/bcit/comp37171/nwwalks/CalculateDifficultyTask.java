package ca.bcit.comp37171.nwwalks;

import android.os.AsyncTask;
import android.util.Log;

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
        for (String polyCode[] : polyCodes) {
            performCalc(polyCode);
        }
        return 0.0;
    }


    protected void onPostExecute(Double result) {
        listener.processFinish(result);
    }

    /**
     * Performs calculation for polyCodes
     */
    private double performCalc(String[] polyCodes) {
        for (String polyCode : polyCodes) {

            List<LatLng> al = PolyUtil.decode(polyCode);

            for (LatLng latLng : al) {
                Log.v(TAG, "FOUND: " + contours.getElevation(latLng));
            }
        }
        return 0.0;
    }

}
