package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

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

        public LongLat w3wToLongLat(String w3w){

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
                System.out.println("Fatal error: Unable to connect to " + machineID +" at port "+ serverPort + ".");
                System.exit(1); // Exit the application
            } catch (InterruptedException e) {
                System.out.println("Fatal error: Unable to connect to " + machineID +" at port "+ serverPort + ".");
                System.exit(1); // Exit the application
            }

            //details.json as JSON string
            String jsonString = response.body();
            System.out.println(jsonString);

            Type Object = new TypeToken<W3W>() {
            }.getType();
            W3W details = new Gson().fromJson(jsonString, Object);

            LongLat point = details.coordinates.point;


            return point;
        };


    }
