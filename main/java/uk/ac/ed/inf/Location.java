package uk.ac.ed.inf;

public class Location {
    String name;
    LongLat point;
    public Location(String name1, String lng, String lat){
        name = name1;
        double longs = Double.parseDouble(lng);
        double lats = Double.parseDouble(lat);
        point = new LongLat(longs, lats);
    }
    public Location(String name1, LongLat lnglat){
        name = name1;
        point = lnglat;
    }
}
