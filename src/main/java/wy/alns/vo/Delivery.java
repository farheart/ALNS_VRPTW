package wy.alns.vo;

import lombok.*;

/**
 * Delivery: represent a customer order
 *
 * @author Yu Wang
 * @date  2022-11-15
 */
@Data
public class Delivery {
    private int id;

    private double amount;

    private Location location;

	private double serviceTime;

	private TimeWindow timeWindow;

    public Delivery(Delivery n) {
    	this.id = n.id;
    	this.location = n.location;
    	this.amount = n.amount;
    	this.serviceTime = n.serviceTime;
    	this.timeWindow = n.timeWindow;
    }

    public Delivery() {
	}

}
