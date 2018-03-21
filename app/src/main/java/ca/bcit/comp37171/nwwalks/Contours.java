package ca.bcit.comp37171.nwwalks;

import java.util.ArrayList;

public class Contours {
    public String name = null;
    public String type = null;
    ArrayList<Features> features = null;

    @Override
    public String toString() {
        String result = "";

        for (Features f : features) {
            result += f.properties.ELEVATION + "\n";
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
        ArrayList<double[]> coordinates = null;
    }

    class Properties {
        public String CONTOUR = null;
        public int OBJECTID;
        public String LAYER = null;
        public int ELEVATION;
    }
}
