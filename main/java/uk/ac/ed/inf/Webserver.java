package uk.ac.ed.inf;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Holds most methods dealign with webserver IO
 */
public class Webserver {
        String machineID;
        String serverPort;

        /**
         * @param name represents name/ID of machine
         * @param port represents the port number that the web server is running on eg. 80, 9898
         */
        public Webserver(String name, String port) {
            machineID = name;
            serverPort = port;

        }

        //http client is resource heavy so we only use one instance
        private static final HttpClient client = HttpClient.newHttpClient();

    /**
     * @param w3w what3words String
     * @return Type LongLat of derived from what3word string
     */
        public LongLat w3wToLongLat(String w3w){

            w3w = w3w.replaceAll("^\"|\"$", "" );
            String[] split = w3w.split("\\.");


            //creates url for web server running on port number specified
            String urlRequest = String.format("http://%s:%s/words/%s/%s/%s/details.json",
                    this.machineID, this.serverPort, split[0], split[1], split[2] );

            //forms http request from webserver
            HttpRequest request =
                    HttpRequest.newBuilder().uri(URI.create(urlRequest)).build();
            HttpResponse<String> response = null;

            //catches error responses
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                System.err.println("Fatal error: Unable to connect to " + machineID +" at port "+ serverPort + ".");
                System.exit(1); // Exit the application
            } catch (InterruptedException e) {
                System.err.println("Fatal error: Unable to connect to " + machineID +" at port "+ serverPort + ".");
                System.exit(1); // Exit the application
            }

            //details.json as JSON string
            String jsonString = response.body();
            JsonParser parser = new JsonParser();
            JsonObject json = (JsonObject) parser.parse(jsonString);
            JsonObject coords = (JsonObject) parser.parse( json.get("coordinates").toString() );
            Double asLong = coords.get("lng").getAsDouble();
            Double asLat = coords.get("lat").getAsDouble();


            return new LongLat(asLong, asLat);
        };


    }
