package uk.ac.ed.inf;


/**
 * Flightpath class holds type flightpath
 */
public class Flightpath {
    String orderNo;
    double fromLongitude;
    double fromLatitude;
    int angle;
    double toLongitude;
    double toLatitude;

    /**
     * @param orderNo1 Order Number
     * @param fromLongitude1 From Longitude
     * @param fromLatitude1 From Latitude
     * @param angle1 Bearing
     * @param toLongitude1 To Longitude
     * @param toLatitude1 To Latitude
     */
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
