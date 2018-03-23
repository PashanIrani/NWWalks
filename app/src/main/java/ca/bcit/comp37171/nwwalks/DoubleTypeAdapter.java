package ca.bcit.comp37171.nwwalks;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import static java.lang.Math.floor;

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
        return floor(10000 * in.nextDouble() + 0.5) / 10000;
    }
}

