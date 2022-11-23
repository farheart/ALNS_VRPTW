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
    private List<Node> nodeList;


    /**
     * The cost of the current Route. It is calculated as the sum of the distances of every next node from the previous one.
     */   
	private Measure measure;


    public Route(String id, Vehicle v) {
        this.id = id;
        this.vehicle = v;
        this.nodeList = new ArrayList<>();
        this.measure = new Measure();
    }


    public Route cloneRoute() {
        Route result = new Route(this.id, this.vehicle);
        result.measure = new Measure(this.measure);
        result.nodeList = new ArrayList<>(this.nodeList);
        return result;
    }

    /**
     * Returns the last node in the route
     */
    public Node getLastNodeOfTheRoute() {
        return this.nodeList.get(this.nodeList.size() - 1);
    }

    /**
     * Adds a customer in the end of the route.
     *
     * @param node The new customer to be inserted.
     */
    public void append(Node node) {
        this.nodeList.add(node);
    }

    /**
     * Adds a customer in the route in a specific position in the sequence.
     *
     * @param node The new customer to be inserted
     * @param index The position in which the customer will be inserted.
     */
    public void addNode(Node node, int index) {
        this.nodeList.add(index, node);
    }

    /**
     * Removes a customer from a specific position in the route.
     *
     * @param index The index from which the customer will be removed
     * @return The removed customer.
     */
    public Node removeNode(int index) {
        return this.nodeList.remove(index);
    }

    @Override
    public String toString() {
        String nodeListStr = "nodeList : [";
        for (Node customer: this.nodeList) {
            nodeListStr += "\n\t\t" + customer;
        }
        nodeListStr += "\n]";

        String result =  "Route[" + this.id + "] : {\n\t" + nodeListStr + ",\n\tmeasure = " + this.measure.toString() + "\n}";
        return result;
    }

}
