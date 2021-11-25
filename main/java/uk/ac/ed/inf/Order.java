package uk.ac.ed.inf;

public class Order {
    String orderNo;
    String deliveryDate;
    String customer;
    String deliverTo;

    public Order(String orderNo1, String deliveryDate1, String customer1, String deliverTo1){
        orderNo = orderNo1;
        deliveryDate = deliveryDate1;
        customer = customer1;
        deliverTo = deliverTo1;
    }
}
