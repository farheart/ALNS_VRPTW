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
	public double load;
	
	public double loadViolation;
	public double timeViolation; 
	
	public Measure(){
		totalCost = 0;
		distance = 0;
		load = 0;
		time = 0;
		
		loadViolation = 0;
		timeViolation = 0;
	}
	
	public Measure(Measure measure) {
		this.totalCost = measure.totalCost;
		this.distance = measure.distance;
		this.load = measure.load;
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
