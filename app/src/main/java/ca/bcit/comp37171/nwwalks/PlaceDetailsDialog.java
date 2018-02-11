package ca.bcit.comp37171.nwwalks;

import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by pashan on 2018-01-20.
 */

public class PlaceDetailsDialog extends BottomSheetDialog {

    private String destinationName;
    private String distance;
    private final View view;

    public PlaceDetailsDialog(Context c, View v) {
        super(c);
        this.view = v;
        this.setContentView(view);
    }

    public void setName(String name) {
        this.destinationName = name;
        TextView text = view.findViewById(R.id.destination_name);
        text.setText(this.destinationName);
    }

    public void setDistance(String distance) {
        this.distance = distance;
        TextView text = view.findViewById(R.id.distance);
        text.setText(this.distance);
    }
}
