package uk.ac.ed.inf;

import java.util.List;

public class Menu {
    String name;
    String location;
    List<Item> menu;

    public Menu (String name1, String location1, List<Item> items){
        name = name1;
        location = location1;
        menu = items;
    }

}

