package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.ArrayList;

public class Buildings {
    Webserver webserver;
    Database database;

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
            System.out.println("Fatal error: Unable to connect to " + webserver.machineID +" at port "+ webserver.serverPort + ".");
            System.exit(1); // Exit the application
        } catch (InterruptedException e) {
            System.out.println("Fatal error: Unable to connect to " + webserver.machineID +" at port "+ webserver.serverPort + ".");
            System.exit(1); // Exit the application
        }

        //menus.json as String
        String jsonString = response.body();

        //parsing jsonString into list of Menu
        FeatureCollection GSON = FeatureCollection.fromJson(jsonString);
        return GSON;
    }


    public FeatureCollection createScene() throws SQLException {
        FeatureCollection noFly = getGSON("no-fly-zones");
        FeatureCollection landmarks = getGSON("landmarks");
        FeatureCollection restaurants = getRestaurants();
        FeatureCollection dropOff = getDropOff();

        for ( Feature x : landmarks.features()){
            noFly.features().add(x);

        }

        for ( Feature x : restaurants.features()){
            noFly.features().add(x);

        }

        for ( Feature x : getDropOff().features()){
            noFly.features().add(x);

        }

        return noFly;

    }

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

    public Location containsName(final ArrayList<Location> list, final String name){
        return list.stream().filter(o -> o.getName().equals(name)).findAny().orElseThrow();
    }

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

}
