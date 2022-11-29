package wy.alns.vo;


import lombok.*;

/**
 * Cost
 *
 * @author Yu Wang
 * @date  2022-11-15
 */
@Data
public class Measure {
	public double totalCost;
	public double distance;
	public double time; 
	public double amount;
	
	public double loadViolation;
	public double timeViolation; 
	
	public Measure(){
		this.totalCost = 0;
		this.distance = 0;
		this.amount = 0;
		this.time = 0;

		this.loadViolation = 0;
		this.timeViolation = 0;
	}
	
	public Measure(Measure measure) {
		this.totalCost = measure.totalCost;
		this.distance = measure.distance;
		this.amount = measure.amount;
		this.time = measure.time;
		
		this.loadViolation = measure.loadViolation;
		this.timeViolation = measure.timeViolation;
	}


	/**
	 * calculate the total cost based on alpha, beta
	 */
	public void calculateTotalCost(double alpha, double beta) {
		totalCost = distance + alpha * loadViolation  + beta * timeViolation;
	}
	
	public void calculateTotalCost() {
		totalCost = distance + loadViolation  + timeViolation;
	}

}
