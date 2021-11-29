package uk.ac.ed.inf;

import java.sql.Connection;

/**
 * Holds Type Delivery
 */
public class Delivery {
    String orderNo;
    String deliveredTo;
    int costInPence;

    /**
     * @param orderNo1 Order Number
     * @param deliveredTo1 what3words location of delivery location
     * @param costInPence1 int of cost in pence of delivery
     */
    public Delivery(String orderNo1, String deliveredTo1, int costInPence1){
        orderNo = orderNo1;
        deliveredTo = deliveredTo1;
        costInPence = costInPence1;
    }


}
