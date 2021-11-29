package uk.ac.ed.inf;

import com.mapbox.geojson.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Class Map holds methods for processing Json and GeoJson Objects
 */
public class Map {
    Database database;
    Webserver webserver;
    FeatureCollection scene;

    /**
     * @param database1 Database
     * @param webserver1 Webserver
     * @throws SQLException
     */
    public Map(Database database1, Webserver webserver1) throws SQLException {
        database = database1;
        webserver = webserver1;
        scene = new Buildings(webserver, database).createScene();
    }


    /**
     * @return Array List of Type Flightpath, Retrieves all Flightpaths from database
     * @throws SQLException
     */
    public ArrayList<Flightpath> getFlightpaths() throws SQLException {
        Connection conn = database.databaseCommunication();

        final String flightpathQuery =
                "select * from flightpath";
        PreparedStatement psFlightpathQuery = conn.prepareStatement(flightpathQuery);

        ArrayList<Flightpath> flightpaths = new ArrayList<>();
        ResultSet rs = psFlightpathQuery.executeQuery();
        while (rs.next()) {
            String orderNo = rs.getString("orderNo");
            double fromLongitude = rs.getDouble("fromLongitude");
            double fromLatitude = rs.getDouble("fromLatitude");
            int angle = rs.getInt("angle");
            double toLongitude = rs.getDouble("toLongitude");
            double toLatitude = rs.getDouble("toLatitude");
            Flightpath x = new Flightpath(orderNo, fromLongitude, fromLatitude, angle, toLongitude, toLatitude);
            flightpaths.add(x);
        }

        return flightpaths;
    }


    /**
     * @param x Flightpath
     * @return Flightpath as type Feature (LineString)
     */
    public Feature createFeature(Flightpath x){
        Point start = Point.fromLngLat(x.fromLongitude, x.fromLatitude);
        Point end = Point.fromLngLat(x.toLongitude, x.toLatitude);
        ArrayList<Point> geom = new ArrayList<Point>();
        geom.add(start);
        geom.add(end);
        Geometry g = LineString.fromLngLats(geom);
        Feature feature = Feature.fromGeometry(g);
        return feature;
    }

    /**
     * @return JsonString of all Flightpaths as Features
     * @throws SQLException
     */
    public String createJsonFlightpath() throws SQLException {
        ArrayList<Flightpath> flightpaths = this.getFlightpaths();

        for (Flightpath x : flightpaths){
            Feature feature = createFeature(x);
            this.scene.features().add(feature);
        }

        String json = this.scene.toJson();
        return json;
    }


}
