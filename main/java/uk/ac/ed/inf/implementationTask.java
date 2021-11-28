package uk.ac.ed.inf;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class implementationTask {
    Database database;
    Webserver webserver;

    public implementationTask(Database database1, Webserver webserver1){
        database = database1;
        webserver = webserver1;
    }

    public void deliveryDatabase(String date) throws SQLException {
        Menus menus = new Menus(webserver);
        this.database.createDeliveriesTable();
        ArrayList<Order> orderDate = this.database.getOrdersDate(date);

        for (Order order : orderDate) {
            String orderNo = order.orderNo;
            String deliveredTo = order.deliverTo;
            int cost = menus.getDeliveryCost(this.database.getDeliveryItems(orderNo));
            Delivery delivery = new Delivery(orderNo, deliveredTo, cost);
            this.database.addDeliveryDatabase(delivery);
        }
    }

    public boolean isBiggerOrEqual(Double d1, Double d2){

        if ( (d1 - d2) > 0.0 ) {
           return true;
        }
        else{
            return false;
        }
    }

    public ArrayList<Flightpath> createFlightPathOrder(LongLat start, Delivery delivery) throws SQLException {
        Buildings buildings = new Buildings(this.webserver, this.database);
        Menus menus = new Menus(this.webserver);
        ArrayList<Location> landmarks = buildings.landmarksToLocations();
        ArrayList<NoFlyZone> noFlyZones = buildings.getNoFlyZones();
        ArrayList<Flightpath> flightpath = new ArrayList<Flightpath>();
        String orderNo = delivery.orderNo;
        ArrayList<String> items = new ArrayList<String>(Arrays.asList(this.database.getDeliveryItems(orderNo)));
        Restaurant pickup = menus.findRestaurant(items);
        ArrayList<LongLat> journey = new ArrayList<LongLat>();


        //----------------------------start----------------------------
        journey.add(start);
        journey.add(this.webserver.w3wToLongLat(pickup.location));
        journey.add(this.webserver.w3wToLongLat(delivery.deliveredTo));

        ArrayList<Flightpath> orderFlightpath = new ArrayList<Flightpath>();

        for (int i = 1; i < journey.size(); i++){
            System.out.println(i);
            LongLat startPos = journey.get(i);
            LongLat endPos;

            try{
                endPos = journey.get(i + 1); }
            catch (IndexOutOfBoundsException e) {
                endPos = startPos;
            }

            LongLat currentPos = new LongLat(startPos);
            int angle = 0;
            ArrayList<Flightpath> xFlightpath = new ArrayList<>();

            while (currentPos.closeTo(endPos) == false) {

                    if (this.isBiggerOrEqual(
                            currentPos.distanceTo(endPos),
                            currentPos.nextPosition(angle).distanceTo(endPos)
                    )){

                        System.out.println("YES"); //and not in polygon

                        Flightpath temp = new Flightpath(
                                orderNo,
                                currentPos.longitude,
                                currentPos.latitude,
                                angle,
                                currentPos.nextPosition(angle).longitude,
                                currentPos.nextPosition(angle).latitude);

                        xFlightpath.add(temp);
                        currentPos = new LongLat(currentPos.nextPosition(angle));
                        System.out.println(currentPos.distanceTo(endPos));
                    }

                    else{
                        Random rand = new Random();
                        angle = rand.nextInt(35) * 10;
                        System.out.println(angle);
                    }
                }

            if (currentPos.closeTo(endPos)){
                Flightpath temp = new Flightpath(
                        orderNo,
                        currentPos.longitude,
                        currentPos.latitude,
                        -999,
                        endPos.longitude,
                        endPos.latitude);
            }

            orderFlightpath.addAll(xFlightpath);

        }
        System.out.println(orderFlightpath);
        return orderFlightpath;
    }

}
