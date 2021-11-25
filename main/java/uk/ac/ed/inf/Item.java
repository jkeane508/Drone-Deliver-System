package uk.ac.ed.inf;


/**
 * represents item on menu for a given restaurant
 */
public class Item {
    String item; //item name
    int pence; //item cost in pence

    /**
     * @param name1 represents the name of the item on a given menu
     * @param price1 represents the price of the item in pence
     */
    public Item (String name1, int price1){
        item = name1;
        pence = price1;
    }
}
