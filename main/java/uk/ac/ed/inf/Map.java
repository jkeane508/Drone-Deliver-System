package uk.ac.ed.inf;

import com.mapbox.geojson.FeatureCollection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Map {
    Database database;
    Webserver webserver;
    FeatureCollection scene;

    public Map(Database database1, Webserver webserver1){
        database = database1;
        webserver = webserver1;
        scene = new Buildings(webserver).createScene();
    }

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



}
