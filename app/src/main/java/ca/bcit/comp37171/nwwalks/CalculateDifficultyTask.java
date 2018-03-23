package ca.bcit.comp37171.nwwalks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.Iterator;
import java.util.List;

/**
 * Created by pashan on 2018-03-22.
 */


public class CalculateDifficultyTask extends AsyncTask<String[], Void, Double> {
    String TAG = CalculateDifficultyTask.class.getName();
    Contours contours;
    public AsyncResponse delegate = null;

    public CalculateDifficultyTask(Contours contours) {
        super();
        this.contours = contours;
    }
    double el_change = 0;
    protected Double doInBackground(String[]... polycodes) {
        for (String polycode[] : polycodes) {
            performCalc(polycode);
        }
        return 0.0;
    }


    protected void onPostExecute(Double result) {
        delegate.processFinish(result);
        Log.v(TAG,"Downloaded " + result + " bytes");
    }

    private double performCalc(String[] polycodes) {
        for (String polycode : polycodes) {
            Log.v(TAG, polycode);
            List<LatLng> al;
            al = PolyUtil.decode(polycode);
            Log.v(TAG, al.toString());

            Iterator<LatLng> it = al.iterator();

            while (it.hasNext()) {
                LatLng latLng = it.next();
                Log.v(TAG, "FOUND: " + contours.getElevation(latLng));
            }
        }
        return 0.0;
    }

}
