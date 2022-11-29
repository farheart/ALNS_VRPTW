package wy.alns.algrithm.solution;

import wy.alns.vo.Delivery;
import wy.alns.vo.Instance;
import wy.alns.vo.Route;
import wy.alns.vo.Service;


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

        int id = 0;
        double totalDistance = 0;
        for (int i = 0; i < solution.getRoutes().size(); i++) {
        	Route route = solution.getRoutes().get(i);
        	if (route.getServiceList().size() >= 3) {
        		id++;
        		
        		double routeDistance = 0;
        		double loadInVehicle = 0;
        		double clockTime = 0;

				boolean ifTWOK = true;

        		for (int j = 1; j < route.getServiceList().size(); j++) {
					double dist = this.instance.getDistanceDict().between(route.getServiceList().get(j - 1), route.getServiceList().get(j));
					clockTime += dist;
					routeDistance += dist;

					Service service = route.getServiceList().get(j);
					if (service instanceof Delivery) {
						Delivery delivery = (Delivery) service;

						loadInVehicle += delivery.getAmount();

						if (clockTime < route.getServiceList().get(j).getTimeWindow().getStart()) {
							clockTime = route.getServiceList().get(j).getTimeWindow().getStart();
						} else if (clockTime > route.getServiceList().get(j).getTimeWindow().getEnd()) {
							ifTWOK = false;
						}
						clockTime += delivery.getServiceTime();
					}
        		}
        		totalDistance += routeDistance;

				boolean ifDistanceOK = (Math.abs(route.getMeasure().distance - routeDistance) <= 0.001);
				boolean ifLoadOK = (Math.abs(route.getMeasure().amount - loadInVehicle) <= 0.001);
				boolean ifTimeOK = (Math.abs(route.getMeasure().time - clockTime) <= 0.001);

        		result += "\nroute " + id + ": "
        				+ "\ndist = " + routeDistance + " \t - " + (ifDistanceOK ? "Pass" : "Fail")
        				+ "\nload = " + loadInVehicle + " \t - " + (ifLoadOK ? "Pass" : "Fail")
        				+ "\ntime = " + clockTime + " \t - " + (ifTimeOK ? "Pass" : "Fail")
        				+ "\nTW : \t\t\t - " + (ifTWOK ? "Pass" : "Fail")
						+ "\n";
        	}
        }
        
        boolean checkTotalCost = (Math.abs(totalDistance - solution.getTotalCost()) <= 0.001);
        result += "\ntotal cost = " + totalDistance + " \t - " + (checkTotalCost ? "Pass" : "Fail");

        return result;
    }

}