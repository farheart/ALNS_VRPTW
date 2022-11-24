package wy.alns.algrithm;

import wy.alns.vo.Instance;
import wy.alns.vo.Route;


/**
 * SolutionValidator
 *
 * @author Yu Wang
 * @date  2022-11-22
*/
public class SolutionValidator {
	private Instance instance;


	public SolutionValidator(Instance instance) {
		this.instance = instance;
	}


    public String Check(Solution solution) {
        String result = "";
        double totalCost = 0;

        int id = 0;
        for (int i = 0; i < solution.getRoutes().size(); i++) {
        	Route route = solution.getRoutes().get(i);
        	if (route.getDeliveryList().size() >= 3) {
        		id++;
        		
        		double distanceByVehicle = 0;
        		double loadInVehicle = 0;
        		double time = 0;
				boolean checkTimeWindows = true;

        		for (int j = 1; j < route.getDeliveryList().size(); j++) {
					double dist = this.instance.getDistanceDict().between(route.getDeliveryList().get(j - 1), route.getDeliveryList().get(j));
					time += dist;
					distanceByVehicle += dist;

					loadInVehicle += route.getDeliveryList().get(j).getAmount();
        			if (time < route.getDeliveryList().get(j).getTimeWindow().getStart())
        				time = route.getDeliveryList().get(j).getTimeWindow().getStart();
        			else if (time > route.getDeliveryList().get(j).getTimeWindow().getEnd())
        				checkTimeWindows = false;
        			
        			time += route.getDeliveryList().get(j).getServiceTime();
        		}
        		totalCost += distanceByVehicle;

				boolean checkCost = (Math.abs(route.getMeasure().distance - distanceByVehicle) <= 0.001);
				boolean checkLoad = (Math.abs(route.getMeasure().amount - loadInVehicle) <= 0.001);
				boolean checkTime = (Math.abs(route.getMeasure().time - time) <= 0.001);

        		result += "\nroute " + id + ": "
        				+ "\ncost = " + distanceByVehicle + " \t - " + (checkCost ? "Pass" : "Fail")
        				+ "\ndemand = " + loadInVehicle + " \t - " + (checkLoad ? "Pass" : "Fail")
        				+ "\ntime = " + Math.round(time * 100) / 100.0 + " \t - " + (checkTime ? "Pass" : "Fail")
        				+ "\nTW = \t\t " + (checkTimeWindows ? "Pass" : "Fail")
						+ "\n";
        	}
        }
        
        boolean checkTotalCost = (Math.abs(totalCost - solution.getTotalCost()) <= 0.001);
        result += "\ntotal cost = " + Math.round(totalCost * 100) / 100.0 + " \t - " + (checkTotalCost ? "Pass" : "Fail");

        return result;
    }

}