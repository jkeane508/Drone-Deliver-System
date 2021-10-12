package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class Menus {
    String machineID;
    String serverPort;

    public Menus(String name, String port) {
        machineID = name;
        serverPort = port;

    }

    private static final HttpClient client = HttpClient.newHttpClient();

    private static ArrayList<Menu> getMenu() {
        HttpRequest request =
                HttpRequest.newBuilder().uri(URI.create("http://localhost:80/menus/menus.json")).build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String jsonString = response.body();
        Type listType = new TypeToken<ArrayList<Menu>>() {
        }.getType();
        ArrayList<Menu> menus = new Gson().fromJson(jsonString, listType);
        return menus;
    }

    public static int getDeliveryCost(String... x) {
        ArrayList<Menu> menus = getMenu();

        int cost = 0;
        boolean found;
        for (String i : x) {
            System.out.println(i);
            found = false;
            for (Menu j : menus) {
                for (Item k : j.menu) {
                    if (k.item.equals(i)) {
                        found = true;
                        System.out.println(k.item);
                        cost = cost + k.pence;
                        break;

                    }
                }
                if (found == true){
                    break;
                }
            }
        }

        cost = cost + 50;
        System.out.println(cost);

        return cost;
    }
};
