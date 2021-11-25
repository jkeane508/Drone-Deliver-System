package uk.ac.ed.inf;

import java.sql.Connection;

public class Delivery {
    String orderNo;
    String deliveredTo;
    int costInPence;

    public Delivery(String orderNo1, String deliveredTo1, int costInPence1){
        orderNo = orderNo1;
        deliveredTo = deliveredTo1;
        costInPence = costInPence1;
    }


}
