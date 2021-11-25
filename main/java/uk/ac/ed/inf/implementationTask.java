package uk.ac.ed.inf;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class implementationTask {

    public void deliveryDatabase(Database database, Menus menus, String date) throws SQLException {
        database.createDeliveriesTable();
        ArrayList<Order> orderDate = database.getOrdersDate(date);

        for (Order order : orderDate) {
            String orderNo = order.orderNo;
            String deliveredTo = order.deliverTo;
            int cost = menus.getDeliveryCost(database.getDeliveryItems(orderNo));
            Delivery delivery = new Delivery(orderNo, deliveredTo, cost);
            database.addDeliveryDatabase(delivery);
        }
    }

    public void flightpathDatabase(Database database, Menus menus) throws SQLException {

    }
}
