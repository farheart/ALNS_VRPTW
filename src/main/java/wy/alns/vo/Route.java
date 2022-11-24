package wy.alns.vo;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Route
 *
 * @author Yu Wang
 * @date  2022-11-15
 */
@Getter
public class Route {
	private String id;

    private Vehicle vehicle;


    /**
     * A sequence of Customers, that will be served from the current Vehicle.
     */
    private List<Order> orderList;


    /**
     * The cost of the current Route. It is calculated as the sum of the distances of every next node from the previous one.
     */   
	private Measure measure;


    public Route(String id, Vehicle v) {
        this.id = id;
        this.vehicle = v;
        this.orderList = new ArrayList<>();
        this.measure = new Measure();
    }


    public Route cloneRoute() {
        Route result = new Route(this.id, this.vehicle);
        result.measure = new Measure(this.measure);
        result.orderList = new ArrayList<>(this.orderList);
        return result;
    }

    /**
     * Returns the last node in the route
     */
    public Order getLastNodeOfTheRoute() {
        return this.orderList.get(this.orderList.size() - 1);
    }

    /**
     * Adds a customer in the end of the route.
     *
     * @param order The new customer to be inserted.
     */
    public void append(Order order) {
        this.orderList.add(order);
    }

    /**
     * Adds a customer in the route in a specific position in the sequence.
     *
     * @param order The new customer to be inserted
     * @param index The position in which the customer will be inserted.
     */
    public void addNode(Order order, int index) {
        this.orderList.add(index, order);
    }

    /**
     * Removes a customer from a specific position in the route.
     *
     * @param index The index from which the customer will be removed
     * @return The removed customer.
     */
    public Order removeNode(int index) {
        return this.orderList.remove(index);
    }

    @Override
    public String toString() {
        String nodeListStr = "orderList : [";
        for (Order customer: this.orderList) {
            nodeListStr += "\n\t\t" + customer;
        }
        nodeListStr += "\n\t]";

        String result =  "Route[" + this.id + "] : {\n\t" + nodeListStr + ",\n\tmeasure = " + this.measure.toString() + "\n}\n";
        return result;
    }

}
