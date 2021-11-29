package uk.ac.ed.inf;

/**
 * Holds Location Object
 */
public class Location {
    String name;
    LongLat point;

    /**
     * @param name1 Name of Location
     * @param lng Longitude of Location
     * @param lat Latitude of Location
     */
    public Location(String name1, String lng, String lat){
        name = name1;
        double longs = Double.parseDouble(lng);
        double lats = Double.parseDouble(lat);
        point = new LongLat(longs, lats);
    }

    /**
     * @param name1 Name of Location
     * @param lnglat LongLat of Location
     */
    public Location(String name1, LongLat lnglat){
        name = name1;
        point = lnglat;
    }

    /**
     * @return Name of Location
     */
    public String getName(){
        return this.name;
    }
}
