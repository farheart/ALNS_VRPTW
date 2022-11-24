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
    private int id;

    private double demand;

    private Location location;

	private double serviceTime;

	private TimeWindow timeWindow;

    public Order(Order n) {
    	this.location = n.location;
    	this.id = n.id;
    	this.demand = n.demand;
    	this.serviceTime = n.serviceTime;
    	this.timeWindow = n.timeWindow;
    }

    public Order() {
	}

}
