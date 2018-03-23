package ca.bcit.comp37171.nwwalks;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by pashan on 2018-03-22.
 */

    public class DoubleTypeAdapter extends TypeAdapter<Double> {

    @Override
    public void write(JsonWriter out, Double value) throws IOException {
        out.value(value);
    }

    @Override
    public Double read(JsonReader in) throws IOException {
        return new BigDecimal(in.nextDouble()).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }
}

