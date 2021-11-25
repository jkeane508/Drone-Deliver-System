package uk.ac.ed.inf;

import java.util.ArrayList;

public class Flightpath {
    String orderNo;
    double fromLongitude;
    double fromLatitude;
    int angle;
    double toLongitude;
    double toLatitude;

    public Flightpath(
            String orderNo1,
            double fromLongitude1,
            double fromLatitude1,
            int angle1,
            double toLongitude1,
            double toLatitude1
    )
    {
        orderNo = orderNo1;
        fromLongitude = fromLongitude1;
        fromLatitude = fromLatitude1;
        angle = angle1;
        toLongitude = toLongitude1;
        toLatitude = toLatitude1;
    }



}
