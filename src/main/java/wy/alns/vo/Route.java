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
    private List<Delivery> deliveryList;


    /**
     * The cost of the current Route. It is calculated as the sum of the distances of every next node from the previous one.
     */   
	private Measure measure;


    public Route(String id, Vehicle v) {
        this.id = id;
        this.vehicle = v;
        this.deliveryList = new ArrayList<>();
        this.measure = new Measure();
    }


    public Route cloneRoute() {
        Route result = new Route(this.id, this.vehicle);
        result.measure = new Measure(this.measure);
        result.deliveryList = new ArrayList<>(this.deliveryList);
        return result;
    }

    /**
     * Returns the last node in the route
     */
    public Delivery getLastNodeOfTheRoute() {
        return this.deliveryList.get(this.deliveryList.size() - 1);
    }

    /**
     * Adds a customer in the end of the route.
     *
     * @param delivery The new customer to be inserted.
     */
    public void append(Delivery delivery) {
        this.deliveryList.add(delivery);
    }

    /**
     * Adds a customer in the route in a specific position in the sequence.
     *
     * @param delivery The new customer to be inserted
     * @param index The position in which the customer will be inserted.
     */
    public void addNode(Delivery delivery, int index) {
        this.deliveryList.add(index, delivery);
    }

    /**
     * Removes a customer from a specific position in the route.
     *
     * @param index The index from which the customer will be removed
     * @return The removed customer.
     */
    public Delivery removeNode(int index) {
        return this.deliveryList.remove(index);
    }

    @Override
    public String toString() {
        String nodeListStr = "deliveryList : [";
        for (Delivery customer: this.deliveryList) {
            nodeListStr += "\n\t\t" + customer;
        }
        nodeListStr += "\n\t]";

        String result =  "Route[" + this.id + "] : {\n\t" + nodeListStr + ",\n\tmeasure = " + this.measure.toString() + "\n}\n";
        return result;
    }

}
