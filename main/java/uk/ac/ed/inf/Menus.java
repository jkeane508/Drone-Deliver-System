package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

/**
 * Class used to retrieve menu data from webserver and calculate delivery costs
 */
public class Menus{
    Webserver webserver;

    public Menus(Webserver webserver1) {
        webserver = webserver1;

    }

    //http client is resource heavy so we only use one instance
    private static final HttpClient client = HttpClient.newHttpClient();


    /**
     * Used to parse menus.json into java classes
     * @return List of class Menu, data is retrieved from web server.
     */

    public ArrayList<Restaurant> getMenu() {

        //creates url for web server running on port number specified
        String urlRequest = String.format("http://%s:%s/menus/menus.json", this.webserver.machineID, this.webserver.serverPort);

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

        //parsing jsonString into list of Restaurant
        Type listType = new TypeToken<ArrayList<Restaurant>>() {
        }.getType();
        ArrayList<Restaurant> menus = new Gson().fromJson(jsonString, listType);

        return menus;
    }

    /**
     * Calculate cost of delivery
     * @param x variable number of strings as parameter
     * @return cost of items in x + cost of delivery
     */
    public int getDeliveryCost(String... x) {

        //list of class Menu
        ArrayList<Restaurant> menus = getMenu();

        //initial cost is 50 - delivery fee
        int cost = 50;
        //boolean used to indicate if item is found
        boolean found;

        //each string in parameter x
        for (String i : x) {
            found = false;
            //each restaurant menu in menus
            for (Restaurant j : menus) {
                //each item in restaurant menu
                for (Item k : j.menu) {
                    if (k.item.equals(i)) {
                        //breaks if item is found
                        found = true;
                        cost = cost + k.pence;
                        break;

                    }
                }
                //breaks if item is found
                if (found == true){
                    break;
                }
            }
            //if item is not found it is output as an error
            if (found == false){
                System.err.println(i);
                System.err.println("^^ was not found in Menus");
            }
        }
        return cost;
    }

    public Restaurant findRestaurant(ArrayList<String> itemNameList){
        ArrayList<Restaurant> restaurants = getMenu();
        Restaurant target = null;
        for(Restaurant x : restaurants){
            ArrayList<String> itemNameMenu = new ArrayList<String>();
            for (Item xs : x.menu){
                itemNameMenu.add(xs.item);
            }
            if (itemNameMenu.containsAll(itemNameList) ){
                target = x;
            }
        }
        return target;
    }

};
