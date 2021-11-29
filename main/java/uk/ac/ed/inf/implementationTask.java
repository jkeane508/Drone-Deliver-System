package uk.ac.ed.inf;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Class Holds Main Methods for Implementation Tasks for Coursework 2
 */
public class implementationTask {
    Database database;
    Webserver webserver;

    /**
     * @param database1 Database
     * @param webserver1 Webserver
     */
    public implementationTask(Database database1, Webserver webserver1){
        database = database1;
        webserver = webserver1;
    }

    /**
     * @param date Date to Calculate Orders, FlightPaths and GeoJson File
     * @throws SQLException
     */
    public void fullImplementaion(String date) throws SQLException {
        database.createFlightpathTable();
        database.createDeliveriesTable();
        LongLat appleton = new LongLat(-3.186874, 55.944494);
        deliveryDatabase(date);
        ArrayList<Flightpath> flightpaths = createFlightpathDeliveries(database.getDeliveries(), appleton);
        LongLat start = new LongLat(flightpaths.get(flightpaths.size()-1).toLongitude, flightpaths.get(flightpaths.size()-1).toLatitude);
        this.createFlightLongLat(start, appleton);
        flightpaths.addAll(this.createFlightLongLat(start, appleton));

        for (Flightpath flight : flightpaths){
            database.addFlightpathDatabase(flight);
        }

        this.flightPathsJson(date);
    }

    /**
     * Create and append delivery objects from orders for a given date.
     * @param date Date of orders
     * @throws SQLException
     */
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

    /**
     *
     * @param d1 Double
     * @param d2 Double
     * @return whether d1 is bigger or equal to d2
     */
    public boolean isBiggerOrEqual(Double d1, Double d2){

        if ( (d1 - d2) > 0.0 ) {
           return true;
        }
        else{
            return false;
        }
    }

    /**
     * @param start Starting Position of Drone
     * @param delivery Array List of type Delivery
     * @return Array List of type Flightpath, containing Flightpath for Delivery
     * @throws SQLException
     */
    public ArrayList<Flightpath> createFlightPathOrder(LongLat start, Delivery delivery) throws SQLException {

        Menus menus = new Menus(this.webserver);
        String orderNo = delivery.orderNo;
        ArrayList<String> items = new ArrayList<String>(Arrays.asList(this.database.getDeliveryItems(orderNo)));
        ArrayList<Restaurant> pickups = menus.findRestaurant(items);


        ArrayList<LongLat> trip = new ArrayList<>();
        trip.add(start);

        for (Restaurant pickup : pickups) {
            trip.add(this.webserver.w3wToLongLat(pickup.location));
        }

        LongLat coordDropoff = webserver.w3wToLongLat(delivery.deliveredTo);
        trip.add(coordDropoff);

        ArrayList<Flightpath> orderFlightpath = new ArrayList<Flightpath>();
        int i = 0;

        while (i < trip.size()) {
            LongLat startPos = trip.get(i);
            LongLat endPos;
            int angle;

            if (i == trip.size() - 1) {
                endPos = startPos;
            } else {
                endPos = trip.get(i + 1);
            }

            LongLat currentPos = new LongLat(startPos);
            ArrayList<Flightpath> xFlightpath = new ArrayList<>();

            while (currentPos.closeTo(endPos) == false) {
                angle = currentPos.angleBetweenRounded(endPos);
                LongLat prev = new LongLat(currentPos);
                LongLat next = new LongLat(currentPos.nextPosition(angle));
                Flightpath temp = new Flightpath(
                        orderNo,
                        prev.longitude,
                        prev.latitude,
                        angle,
                        next.longitude,
                        next.latitude);
                xFlightpath.add(temp);
            }

            if (currentPos.closeTo(endPos)){
                Flightpath temp = new Flightpath(
                        orderNo,
                        currentPos.longitude,
                        currentPos.latitude,
                        -999,
                        endPos.longitude,
                        endPos.latitude);
                xFlightpath.add(temp);
            }
            orderFlightpath.addAll(xFlightpath);
            i++;
        }
        return orderFlightpath;
    }

    /**
     * @param start Start Position
     * @param end End Position
     * @return Array List of type Flightpath, Flightpath of Drone from Start to End
     * @throws SQLException
     */
    public ArrayList<Flightpath> createFlightLongLat(LongLat start, LongLat end) throws SQLException {
        ArrayList<LongLat> trip = new ArrayList<>();
        trip.add(start);
        trip.add(end);
        String orderNo = "000000aa";

        ArrayList<Flightpath> orderFlightpath = new ArrayList<Flightpath>();
        int i = 0;

        while (i < trip.size()) {
            LongLat startPos = trip.get(i);
            LongLat endPos;
            int angle;

            if (i == trip.size() - 1) {
                endPos = startPos;
            } else {
                endPos = trip.get(i + 1);
            }

            LongLat currentPos = new LongLat(startPos);
            ArrayList<Flightpath> xFlightpath = new ArrayList<>();

            while (currentPos.closeTo(endPos) == false) {
                angle = currentPos.angleBetweenRounded(endPos);
                LongLat prev = new LongLat(currentPos);
                LongLat next = new LongLat(currentPos.nextPosition(angle));
                Flightpath temp = new Flightpath(
                        orderNo,
                        prev.longitude,
                        prev.latitude,
                        angle,
                        next.longitude,
                        next.latitude);
                xFlightpath.add(temp);
            }

            if (currentPos.closeTo(endPos)){
                Flightpath temp = new Flightpath(
                        orderNo,
                        currentPos.longitude,
                        currentPos.latitude,
                        -999,
                        endPos.longitude,
                        endPos.latitude);
                xFlightpath.add(temp);
            }
            orderFlightpath.addAll(xFlightpath);
            i++;
        }
        return orderFlightpath;
    }

    /**
     * @param deliveries Array List of type Delivery
     * @param start Starting Position of Drone
     * @return Array List of type Flightpath, Contains Flightpath of drone for all in Deliveries
     * @throws SQLException
     */
    public ArrayList<Flightpath> createFlightpathDeliveries(ArrayList<Delivery> deliveries, LongLat start) throws SQLException {
        ArrayList<Flightpath> flightpaths = new ArrayList<Flightpath>();
        flightpaths.addAll(this.createFlightPathOrder(start, deliveries.get(0)));

        for (Delivery x : deliveries){
            LongLat newstart = new LongLat (flightpaths.get(flightpaths.size()-1).toLongitude, flightpaths.get(flightpaths.size()-1).toLatitude);
            flightpaths.addAll(this.createFlightPathOrder(newstart, x));
        }
        return flightpaths;
    }


    /**
     * Outputs GeoJson file for all Flightpath in Flightpath table
     * @param date String Date
     * @throws SQLException
     */
    public void flightPathsJson(String date) throws SQLException {
        Map map = new Map(database, webserver);
        String json = map.createJsonFlightpath();

        String[] split = date.split("\\-");
        String dateFormat = String.format("%s-%s-%s", split[2], split[1], split[0]);

        String filename = String.format("drone-%s.geojson", dateFormat);

        try (PrintWriter out = new PrintWriter(filename)) {
            out.println(json);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("File Out");
    }

    /*public ArrayList<Flightpath> createFlightPathOrder3(LongLat start, Delivery delivery) throws SQLException {
        Buildings buildings = new Buildings(this.webserver, this.database);
        Menus menus = new Menus(this.webserver);

        ArrayList<Location> landmarks = buildings.landmarksToLocations();
        ArrayList<NoFlyZone> noFlyZones = buildings.getNoFlyZones();
        ArrayList<Flightpath> flightpath = new ArrayList<Flightpath>();
        String orderNo = delivery.orderNo;
        ArrayList<String> items = new ArrayList<String>(Arrays.asList(this.database.getDeliveryItems(orderNo)));
        ArrayList<Restaurant> pickups = menus.findRestaurant(items);


        ArrayList<LongLat> trip = new ArrayList<>();
        trip.add(start);

        for (Restaurant pickup : pickups) {
            System.out.println(pickup.name);
            trip.add(this.webserver.w3wToLongLat(pickup.location));
        }

        System.out.println(delivery.deliveredTo);
        LongLat coordDropoff = webserver.w3wToLongLat(delivery.deliveredTo);
        trip.add(coordDropoff);

        ArrayList<Flightpath> orderFlightpath = new ArrayList<Flightpath>();
        int i = 0;

        while (i < trip.size()) {
            System.out.println(i);
            LongLat startPos = trip.get(i);
            LongLat endPos;
            int angle;

            if (i == trip.size() - 1) {
                endPos = startPos;
            } else {
                endPos = trip.get(i + 1);
            }

            System.out.println("startPos");
            System.out.println(startPos.longitude);
            System.out.println(startPos.latitude);
            System.out.println("endPos");
            System.out.println(endPos.longitude);
            System.out.println(startPos.latitude);

            LongLat currentPos = new LongLat(startPos);
            ArrayList<Flightpath> xFlightpath = new ArrayList<>();
            boolean correctPath = true;

            while (currentPos.closeTo(endPos) == false) {
                angle = currentPos.angleBetweenRounded(endPos);
                LongLat prev = new LongLat(currentPos);
                LongLat next = new LongLat(currentPos.nextPosition(angle));

                if (next.isConfined() && !next.closeToPolygon(noFlyZones)) {
                    System.out.println(angle);
                    Flightpath temp = new Flightpath(
                            orderNo,
                            prev.longitude,
                            prev.latitude,
                            angle,
                            next.longitude,
                            next.latitude);
                    xFlightpath.add(temp);
                }
                if (prev.closeToPolygon(noFlyZones)) {
                    correctPath = false;
                    System.out.println("close to no fly");
                    System.out.println(trip.size());
                    System.out.println(i);
                    if (startPos.distanceTo(landmarks.get(0).point) <= startPos.distanceTo(landmarks.get(1).point)) {
                        trip.add(i + 1, landmarks.get(0).point);
                        System.out.println(landmarks.get(0).name);
                        System.out.println(trip.size());
                    } else {
                        trip.add(i + 1, landmarks.get(1).point);
                        System.out.println(landmarks.get(1).name);
                        System.out.println(trip.size());
                    }
                }
                if (!correctPath) {
                    System.out.println("path cleared");
                    xFlightpath.clear();
                    currentPos = new LongLat(endPos);
                }
            }
            if (correctPath) {
                orderFlightpath.addAll(xFlightpath);
                i++;
            }
        }
        return orderFlightpath;
    } */


}
