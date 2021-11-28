package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    String machineID;
    String serverPort;


    /**
     * @param name represents name/ID of machine
     * @param port represents the port number that the web server is running on eg. 80, 9898
     */
    public Database(String name, String port) {
        machineID = name;
        serverPort = port;

    }

    public Connection databaseCommunication() throws SQLException {
        String jdbcString = String.format("jdbc:derby://%s:%s/derbyDB", this.machineID, this.serverPort);
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(jdbcString);
        } catch (SQLException e) {
            System.out.print(e);
            System.out.println("Fatal error: Unable to connect to " + machineID + " at port " + serverPort + ".");
            System.exit(1); // Exit the application
        }


        return conn;
    }

    public void createDeliveriesTable() throws SQLException {
        Connection conn = databaseCommunication();
        Statement statement = conn.createStatement();

        DatabaseMetaData databaseMetadata = conn.getMetaData();
        ResultSet resultSet =
                databaseMetadata.getTables(null, null, "DELIVERIES", null);
        if (resultSet.next()) {
            statement.execute("drop table deliveries");
        }

        statement.execute(
                "create table deliveries(" +
                        "orderNo char(8), " + "deliveredTo varchar(19), " + "costInPence int)");

    }

    public void createFlightpathTable() throws SQLException {
        Connection conn = databaseCommunication();
        Statement statement = conn.createStatement();

        DatabaseMetaData databaseMetadata = conn.getMetaData();
        ResultSet resultSet =
                databaseMetadata.getTables(null, null, "FLIGHTPATH", null);
        if (resultSet.next()) {
            statement.execute("drop table flightpath");
        }

        statement.execute(
                "create table flightpath(" +
                        "orderNo char(8), " + "fromLongitude double, " + "fromLatitude double, " + "angle integer, " + "toLongitude double, " + "toLatitude double)");

    }

    public void addDeliveryDatabase (Delivery x) throws SQLException {
        Connection conn = databaseCommunication();

        PreparedStatement psDelivery = conn.prepareStatement("insert into deliveries values (?, ?, ?)");
        psDelivery.setString(1, x.orderNo);
        psDelivery.setString(2, x.deliveredTo);
        psDelivery.setInt(3, x.costInPence);

        psDelivery.execute();
    }

    public void addFlightpathDatabase (Flightpath x) throws SQLException {
        Connection conn = databaseCommunication();

        PreparedStatement psFlightpath = conn.prepareStatement("insert into flightpath values (?, ?, ?, ?, ?, ?)");
        psFlightpath.setString(1, x.orderNo);
        psFlightpath.setDouble(2, x.fromLongitude);
        psFlightpath.setDouble(3, x.fromLatitude);
        psFlightpath.setInt(4, x.angle);
        psFlightpath.setDouble(5, x.toLongitude);
        psFlightpath.setDouble(6, x.toLatitude);

        psFlightpath.execute();
    }

    public ArrayList<Order> getOrdersDate(String date) throws SQLException {
        Connection conn = databaseCommunication();
        final String orderQuery = "select * from orders where deliveryDate = (?)";

        PreparedStatement orderQuerySQL = conn.prepareStatement(orderQuery);
        orderQuerySQL.setString(1, date);

        ArrayList<Order> orders = new ArrayList<Order>();
        ResultSet rs = orderQuerySQL.executeQuery();

        while (rs.next()) {
            String orderNumber = rs.getString("orderNo");
            String deliveryDate = rs.getString("deliveryDate");
            String customer = rs.getString("customer");
            String deliverTo = rs.getString("deliverTo");
            Order currentOrder = new Order(orderNumber, deliveryDate, customer, deliverTo);
            orders.add(currentOrder);
        }

        return orders;
    }

    public String[] getDeliveryItems(String orderNo) throws SQLException {
        Connection conn = databaseCommunication();
        final String orderQuery = "select * from orderDetails where orderNO = (?)";

        PreparedStatement orderQuerySQL = conn.prepareStatement(orderQuery);
        orderQuerySQL.setString(1, orderNo);

        ArrayList<Order> orders = new ArrayList<Order>();
        ResultSet rs = orderQuerySQL.executeQuery();
        ArrayList<String> itemList = new ArrayList<String>();

        while (rs.next()) {
            itemList.add( rs.getString("item") );
        }

        return itemList.toArray(new String[0]);
    }

    public ArrayList<Delivery> getDeliveries() throws SQLException {
        Connection conn = databaseCommunication();
        final String orderQuery = "select * from deliveries";
        PreparedStatement orderQuerySQL = conn.prepareStatement(orderQuery);

        ArrayList<Delivery> deliveries = new ArrayList<Delivery>();
        ResultSet rs = orderQuerySQL.executeQuery();

        while (rs.next()) {
            String orderNumber = rs.getString("orderNo");
            String deliveredTo = rs.getString("deliveredTo");
            int costInPence = Integer.parseInt(rs.getString("costInPence"));
            Delivery currentDelivery = new Delivery(orderNumber, deliveredTo, costInPence);
            deliveries.add(currentDelivery);
        }

        return deliveries;
    }


}
