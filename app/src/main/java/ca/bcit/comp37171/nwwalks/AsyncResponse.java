package ca.bcit.comp37171.nwwalks;

/**
 * Created by pashan on 2018-03-22.
 */


public interface AsyncResponse {
    void processFinish(Double output);
    void processFinish(Contours output);
}