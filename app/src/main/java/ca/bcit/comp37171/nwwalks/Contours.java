package ca.bcit.comp37171.nwwalks;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class Contours {
    public String name = null;
    public String type = null;
    ArrayList<Features> features = null;

    @Override
    public String toString() {
        String result = "";

        for (Features f : features) {

            Iterator<Double[]> it = f.geometry.coordinates.iterator();

            while(it.hasNext()) {
                Double[] ar = it.next();
                for (int i =0; i < ar.length; i++) {
                    result += ar[i] + ", ";

                }
                result += "\n";
                break;
            }

            result += "\n";
        }
        return result;
    }

    class Features {
        public String type = null;
        public Geometry geometry = null;
        public Properties properties = null;
    }

    class Geometry {
        public String type = null;
        HashSet<Double[]> coordinates = null;
        public Geometry() {

        }
    }

    class Properties {
        public String CONTOUR = null;
        public int OBJECTID;
        public String LAYER = null;
        public double ELEVATION;
    }

    public int getElevation(LatLng ll) {
        double tolerance = 0.005;
        //new BigDecimal(ll.longitude).setScale(4, RoundingMode.HALF_UP).doubleValue()
        Double[] latLng = {new BigDecimal(ll.longitude).setScale(4, RoundingMode.HALF_UP).doubleValue(), new BigDecimal(ll.latitude).setScale(4, RoundingMode.HALF_UP).doubleValue()};
        Log.v("TRYING TO FIND: ",latLng[0] + ", " + latLng[1]);
        int i = 0;
        int index = -1;
        for (Features f : features) {
            ++i;
            if(f.geometry.coordinates.contains(latLng)) {
                index = i;
                break;
            }
        }

        if (index == -1) return -1;
        return (int)features.get(index).properties.ELEVATION;
    }

    public void removeRes() {
        for (Features f : features) {
            Iterator<Double[]> it = f.geometry.coordinates.iterator();
            HashSet<Double[]> newSet = new HashSet<>();

            for (Double[] og : f.geometry.coordinates) {

                //Log.v("THITS", "this is taking a long time");
                Double[] ar = new Double[]{og[0], og[1]};
                //Log.v("ADDING: ",ar[0] + ", " + ar[1]);
                newSet.add(ar);
            }

            f.geometry.coordinates = newSet;


        }
    }


}
