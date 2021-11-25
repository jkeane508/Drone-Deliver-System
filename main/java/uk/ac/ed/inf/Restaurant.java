package uk.ac.ed.inf;

import java.util.List;

/**
 * Class used to represent Restaurants that the drone is delivering from
 */
public class Restaurant {
    String name; //restaurant name
    String location; //restaurant location (lat,long)
    List<Item> menu; //list of class Item

    /**
     * @param name1 name of restaurant
     * @param location1 location of restaurant
     * @param items list of available items on menu, item class contains name and price in pence
     */
    public Restaurant (String name1, String location1, List<Item> items){
        name = name1;
        location = location1;
        menu = items;
    }

}

