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
     * A sequence of service that need to be performed by the vehicle.
     */
    private List<Service> serviceList;


    /**
     * Measure of Route, e.g., total distances
     */   
	private Measure measure;


    public Route(String id, Vehicle v) {
        this.id = id;
        this.vehicle = v;
        this.serviceList = new ArrayList<>();
        this.measure = new Measure();
    }


    public Route cloneRoute() {
        Route result = new Route(this.id, this.vehicle);
        result.measure = new Measure(this.measure);
        result.serviceList = new ArrayList<>(this.serviceList);
        return result;
    }

    /**
     * Returns the last stop of the route
     */
    public Service getLastStop() {
        return this.serviceList.get(this.serviceList.size() - 1);
    }

    /**
     * Adds a customer to the end of the route.
     */
    public void append(Service service) {
        this.serviceList.add(service);
    }


    /**
     * Adds a customer in the route in a specific position in the sequence.
     *
     * @param service The new customer to be inserted
     * @param index The position in which the customer will be inserted.
     */
    public void addNode(Service service, int index) {
        this.serviceList.add(index, service);
    }

    /**
     * Removes a customer from a specific position in the route.
     *
     * @param index The index from which the customer will be removed
     * @return The removed customer.
     */
    public Service removeNode(int index) {
        return this.serviceList.remove(index);
    }

    @Override
    public String toString() {
        String nodeListStr = "serviceList : [";
        for (Service customer: this.serviceList) {
            nodeListStr += "\n\t\t" + customer;
        }
        nodeListStr += "\n\t]";

        String result =  "Route[" + this.id + "] : {\n\t" + nodeListStr + ",\n\tmeasure = " + this.measure.toString() + "\n}\n";
        return result;
    }

}
