package uk.ac.ed.inf;
import com.mapbox.geojson.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Holds methods for getting GeoJson points for map attributes
 */
public class Buildings {
    Webserver webserver;
    Database database;

    /**
     * @param webserver1 Webserver
     * @param database1 Database
     */
    public Buildings(Webserver webserver1, Database database1) {
        webserver = webserver1;
        database = database1;

    }

    //http client is resource heavy so we only use one instance
    private static final HttpClient client = HttpClient.newHttpClient();


    /**
     * Used to parse no-fly-zones.geojson into java classes
     * @return List of class Menu, data is retrieved from web server.
     */

    private FeatureCollection getGSON(String filename) {

        //creates url for web server running on port number specified
        String urlRequest = String.format("http://%s:%s/buildings/%s.geojson", this.webserver.machineID, this.webserver.serverPort, filename);

        //forms http request from webserver
        HttpRequest request =
                HttpRequest.newBuilder().uri(URI.create(urlRequest)).build();
        HttpResponse<String> response = null;

        //catches error responses
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            System.err.println("Fatal error: Unable to connect to " + webserver.machineID +" at port "+ webserver.serverPort + ".");
            System.exit(1); // Exit the application
        } catch (InterruptedException e) {
            System.err.println("Fatal error: Unable to connect to " + webserver.machineID +" at port "+ webserver.serverPort + ".");
            System.exit(1); // Exit the application
        }

        //menus.json as String
        String jsonString = response.body();

        //parsing jsonString into list of Menu
        FeatureCollection GSON = FeatureCollection.fromJson(jsonString);
        return GSON;
    }


    /**
     * @return GeoJson Feature Collection of all map features
     * @throws SQLException
     */
    public FeatureCollection createScene() throws SQLException {
        FeatureCollection noFly = getGSON("no-fly-zones");
        FeatureCollection landmarks = getGSON("landmarks");
        FeatureCollection restaurants = getRestaurants();
        FeatureCollection dropOff = getDropOff();
        Feature start = getStart();

        for ( Feature x : landmarks.features()){
            noFly.features().add(x);

        }

        for ( Feature x : restaurants.features()){
            noFly.features().add(x);

        }

        for ( Feature x : getDropOff().features()){
            noFly.features().add(x);

        }

        noFly.features().add(start);


        return noFly;

    }

    /**
     * @return ArrayList of landmarks as type Location
     */
    public ArrayList<Location> landmarksToLocations() {
        FeatureCollection landmarks = getGSON("landmarks");
        ArrayList<Location> landmarkLocations = new ArrayList<Location>();
        for (Feature feature : landmarks.features()) {
            String name = (feature.properties().get("name")).toString();
            LongLat point = this.webserver.w3wToLongLat(feature.properties().get("location").toString());
            Location currentlandmark = new Location(name, point);
            landmarkLocations.add(currentlandmark);

        }
        return landmarkLocations;
    }

    /**
     * @return Array List of No-Fly-Zones as type NoFlyZone
     */
    public ArrayList<NoFlyZone> getNoFlyZones() {
        FeatureCollection noFlyZones = getGSON("no-fly-zones");
        ArrayList<NoFlyZone> zones = new ArrayList<NoFlyZone>();
        for (Feature x : noFlyZones.features()){
            String name = x.properties().get("name").toString();
            Polygon polygon =  Polygon.fromJson( x.geometry().toJson() );
            NoFlyZone noFlyZone = new NoFlyZone(name, polygon);
            zones.add(noFlyZone);
        }
        return zones;
    }

    /**
     * @return GeoJson Feature Collection of Restaurants
     */
    public FeatureCollection getRestaurants(){
        Menus menu = new Menus(webserver);
        ArrayList<Restaurant> restaurants = menu.getMenu();
        ArrayList<Feature> feautures = new ArrayList<Feature>();

        for (Restaurant x : restaurants){
            LongLat coord = webserver.w3wToLongLat(x.location);
            Point start = Point.fromLngLat(coord.longitude, coord.latitude);
            Geometry g = start;
            Feature f = Feature.fromGeometry(g);
            f.addStringProperty("name", x.name);
            f.addStringProperty("location", x.location);
            f.addStringProperty("marker-symbol", "cafe");
            f.addStringProperty("marker-color", "#ff2600");
            feautures.add(f);
        }
        return FeatureCollection.fromFeatures(feautures);
    }

    /**
     * @return GeoJson Feature Collection of Delivery Locations
     * @throws SQLException
     */
    public FeatureCollection getDropOff() throws SQLException {
        ArrayList<Delivery> deliveries = database.getDeliveries();
        ArrayList<Feature> feautures = new ArrayList<Feature>();
        for ( Delivery x : deliveries){
            LongLat coord = webserver.w3wToLongLat(x.deliveredTo);
            Point start = Point.fromLngLat(coord.longitude, coord.latitude);
            Geometry g = start;
            Feature f = Feature.fromGeometry(g);
            f.addStringProperty("name", x.orderNo);
            f.addStringProperty("location", x.deliveredTo);
            f.addStringProperty("marker-symbol", "cross");
            f.addStringProperty("marker-color", "#00f900");
            feautures.add(f);
        }
        return FeatureCollection.fromFeatures(feautures);
    }

    /**
     * @return GeoJson Feature of Appleton Tower (Start Location)
     */
    public Feature getStart(){
        LongLat appleton = new LongLat(-3.186874, 55.944494);
        Point start = Point.fromLngLat(appleton.longitude, appleton.latitude);
        Geometry g = start;
        Feature f = Feature.fromGeometry(g);
        f.addStringProperty("name", "appleton");
        f.addStringProperty("marker-symbol", "building");
        f.addStringProperty("marker-color", "#fffb00");
        return f;
    }

}
