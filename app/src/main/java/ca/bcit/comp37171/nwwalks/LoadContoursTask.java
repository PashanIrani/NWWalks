package ca.bcit.comp37171.nwwalks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
/**
 * Created by pashan on 2018-03-22.
 */


public class LoadContoursTask extends AsyncTask<Void, Void, Contours> {
    String TAG = LoadContoursTask.class.getName();
    public AsyncResponse delegate = null;
    private MainActivity activity;

    public LoadContoursTask(MainActivity a) {
        super();
        activity = a;
    }


    protected Contours doInBackground(Void... params) {

        String json = "";
        Contours contours = null;

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Double.class, new DoubleTypeAdapter())
                .create();
        try
        {
            InputStream is = activity.getAssets().open("CONTOURS.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");

        }catch(IOException e){
            e.printStackTrace();
        }

        try {
            // convert json in an User object
            contours = gson.fromJson(json, Contours.class);
        }
        catch (Exception e) {
            // we never know :)
            Log.e("error parsing", e.toString());
        }

        Log.v(TAG, contours.toString());
        contours.removeRes();
        return contours;
    }


    protected void onPostExecute(Contours result) {
        activity.processFinish(result);
    }


}
