package wy.alns.algrithm;

import wy.alns.vo.Distance;
import wy.alns.vo.Route;
import wy.alns.vo.Instance;



/**
 * SolutionValidator
 *
 * @author Yu Wang
 * @date  2022-11-21
 */
public class SolutionValidator {
	
	private Instance instance;
	
	public SolutionValidator(Instance instance) {
		this.instance = instance;
	}
	
    public String Check(Solution solution) {
        String result = "";
        double totalCost = 0;
		Distance distance = this.instance.getDistance();

        int id = 0;
        for (int i = 0; i < solution.getRoutes().size(); i++) {
        	Route vehicle = solution.getRoutes().get(i);
        	if (vehicle.getNodeList().size() >= 3) {
        		id++;
        		
        		double costInVehicle = 0;
        		double loadInVehicle = 0;
        		double time = 0;
        		
        		boolean checkCost = true;
        		boolean checkLoad = true;
        		boolean checkTime = true;
        		boolean checkTimeWindows = true;

        		for (int j = 1; j < vehicle.getNodeList().size(); j++) {
					double dist = this.instance.getDistance().between(vehicle.getNodeList().get(j - 1), vehicle.getNodeList().get(j));
					time += dist;
					costInVehicle += dist;

					loadInVehicle += vehicle.getNodeList().get(j).getDemand();
        			if (time < vehicle.getNodeList().get(j).getTimeWindow()[0])
        				time = vehicle.getNodeList().get(j).getTimeWindow()[0];
        			else if (time > vehicle.getNodeList().get(j).getTimeWindow()[1])
        				checkTimeWindows = false;
        			
        			time += vehicle.getNodeList().get(j).getServiceTime();
        		}
        		
        		totalCost += costInVehicle;
        		
        		if (Math.abs(vehicle.getMeasure().distance - costInVehicle) > 0.001) checkCost = false;
        		if (Math.abs(vehicle.getMeasure().load - loadInVehicle) > 0.001) checkLoad = false;
        		if (Math.abs(vehicle.getMeasure().time - time) > 0.001) checkTime = false;
        		
        		
        		result += "\n check route " + id + ": "
        				+ "\n check cost = " + costInVehicle + "  " + checkCost
        				+ "\n check demand = " + loadInVehicle + "  " + checkLoad
        				+ "\n check time = " + time + "  " + checkTime
        				+ "\n check time windows = " + checkTimeWindows +"\n";

        	}
        }
        
        boolean checkTotalCost = true;
		if (Math.abs(totalCost - solution.getTotalCost()) > 0.001) checkTotalCost = false;

        result += "\ncheck total cost = " + Math.round(totalCost * 100) / 100.0 + "  " + checkTotalCost;     

        return result;
    }

}