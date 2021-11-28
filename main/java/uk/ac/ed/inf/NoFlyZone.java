package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.Polygon;

public class NoFlyZone {
    String name;
    Polygon zone;

    public NoFlyZone(String name1, Polygon noflyzone1){
        name = name1;
        zone = noflyzone1;
    }

}
