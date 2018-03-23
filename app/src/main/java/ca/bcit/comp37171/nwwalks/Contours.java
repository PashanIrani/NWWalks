package ca.bcit.comp37171.nwwalks;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import static java.lang.Math.floor;

public class Contours {
    public String name = null;
    ArrayList<Features> features = null;

    @Override
    public String toString() {
        String result = "";

        for (Features f : features) {

            Iterator<ArrayList<Double>> it = f.geometry.coordinates.iterator();

            while(it.hasNext()) {
                ArrayList<Double> ar = it.next();
                for (int i =0; i < ar.size(); i++) {
                    result += ar.get(i) + ", ";

                }
                result += "\n";
                break;
            }

            result += "\n";
        }
        return result;
    }

    class Features {
        public Geometry geometry = null;
        public Properties properties = null;
    }

    class Geometry {
        HashSet<ArrayList<Double>> coordinates = null;
    }

    class Properties {
        public double ELEVATION;
    }

    public int getElevation(LatLng ll) {
        //new BigDecimal(ll.longitude).setScale(4, RoundingMode.HALF_UP).doubleValue()

        ArrayList<Double> latLng = new ArrayList<>(2);
        latLng.add(0,floor(10000 * ll.longitude + 0.5) / 10000);
        latLng.add(1,       floor(10000 * ll.latitude + 0.5) / 10000);


        Log.v("TRYING TO FIND: ",latLng.get(0) + ", " + latLng.get(1));
        int i = 0;
        int index = -1;
        for (Features f : features) {
            ++i;
            //Log.v("i", "i: " + i);
            if(f.geometry.coordinates.contains(latLng) || f.geometry.coordinates.contains(latLng)) {
                index = i;
                break;
            }
        }

        if (index == -1) return -1;
        return (int)features.get(index).properties.ELEVATION;
    }

    public void removeRes() {
        for (Features f : features) {

            HashSet<ArrayList<Double>> newSet = new HashSet<>();

            for (ArrayList<Double> og : f.geometry.coordinates) {
                ArrayList<Double> ar = new ArrayList<>(2);
                ar.add(0,og.get(0));
                ar.add(1,og.get(1));
                newSet.add(ar);
            }

            f.geometry.coordinates = newSet;

        }
    }


}
