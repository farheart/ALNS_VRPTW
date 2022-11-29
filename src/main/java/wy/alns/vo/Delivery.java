package wy.alns.vo;

import lombok.*;

/**
 * Delivery: represent a customer order delivery
 *
 * @author Yu Wang
 * @date  2022-11-15
 */
@Data
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=true)
public class Delivery extends Service {
    private double amount;

	private double serviceTime;

//    public Delivery(Delivery n) {
//    	this.id = n.id;
//    	this.location = n.location;
//    	this.amount = n.amount;
//    	this.serviceTime = n.serviceTime;
//    	this.timeWindow = n.timeWindow;
//    }

    public Delivery() {
	}

}
