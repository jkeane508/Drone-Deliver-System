package uk.ac.ed.inf;

/**
 * Holds Type Order
 */
public class Order {
    String orderNo;
    String deliveryDate;
    String customer;
    String deliverTo;

    /**
     * @param orderNo1 Order Number
     * @param deliveryDate1 Date of Delivery
     * @param customer1 Student Number
     * @param deliverTo1 what3words delivery location
     */
    public Order(String orderNo1, String deliveryDate1, String customer1, String deliverTo1){
        orderNo = orderNo1;
        deliveryDate = deliveryDate1;
        customer = customer1;
        deliverTo = deliverTo1;
    }
}
