package wy.alns.vo;

import lombok.*;

/**
 * Order: represent a customer order
 *
 * @author Yu Wang
 * @date  2022-11-15
 */
@Data
public class Order {
	private double[] timeWindow;
	private double serviceTime;

    /**
     * The X-axis coordinate in a theoretical 2-D space for the specific customer.
     */
    private double x;

    /**
     * The Y-axis coordinate in a theoretical 2-D space for the specific customer.
     */
    private double y;

    /**
     * A unique identifier for the customer
     */
    private int id;

    /**
     * The current customer's demand.
     */
    private double demand;


    public Order(Order n) {
    	this.x = n.x;
    	this.y = n.y;
    	this.id = n.id;
    	this.demand = n.demand;
    	this.serviceTime = n.serviceTime;
    	this.timeWindow = new double[] { n.timeWindow[0], n.timeWindow[1] };
    }

    public Order() {
	}

    public double[] getTimeWindow() {
        return this.timeWindow;
    }

    public void setTimeWindow(double start, double end) {
        this.timeWindow = new double[] { start, end };
    }

}
