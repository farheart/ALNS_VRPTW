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

    private double amount;

    private Location location;

	private double serviceTime;

	private TimeWindow timeWindow;

    public Order(Order n) {
    	this.id = n.id;
    	this.location = n.location;
    	this.amount = n.amount;
    	this.serviceTime = n.serviceTime;
    	this.timeWindow = n.timeWindow;
    }

    public Order() {
	}

}
