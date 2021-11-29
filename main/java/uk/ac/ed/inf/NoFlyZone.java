package uk.ac.ed.inf;

import com.mapbox.geojson.Polygon;

/**
 * Contains Type NoFlyZone
 */
public class NoFlyZone {
    String name;
    Polygon zone;

    /**
     * @param name1 Name of NoFlyZone
     * @param noflyzone1 GeoJson Polygon Feature
     */
    public NoFlyZone(String name1, Polygon noflyzone1){
        name = name1;
        zone = noflyzone1;
    }

}
