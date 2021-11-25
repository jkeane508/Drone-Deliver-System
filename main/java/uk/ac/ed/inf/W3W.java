package uk.ac.ed.inf;


public class W3W {
    String country;
    Square square;
    String nearestPlace;
    Location coordinates;
    String words;
    String language;
    String map;



    public W3W(String country1,
               Square square1,
               String nearestPlace1,
               Location coords2,
               String words1,
               String language1,
               String map1){

        country = country1;
        square = square1;
        nearestPlace = nearestPlace1;
        coordinates = coords2;
        words = words1;
        language = language1;
        map = map1;
    }
}
