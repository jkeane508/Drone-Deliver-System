package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class Buildings {
    Webserver webserver;

    public Buildings(Webserver webserver1) {
        webserver = webserver1;

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


    public FeatureCollection createScene(){
        FeatureCollection noFly = getGSON("no-fly-zones");
        FeatureCollection landmarks = getGSON("landmarks");

        for ( Feature x : landmarks.features()){
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


}
