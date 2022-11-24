package wy.alns.algrithm.solver;


import wy.alns.algrithm.Solution;
import wy.alns.vo.*;

import java.util.ArrayList;
import java.util.List;


/**
 * GreedyVRP : solve using greedy
 *
 * @author Yu Wang
 * @date  2022-11-17
 */
public class GreedyVRP {
    private Instance instance;
    
    private List<Route> routeList;

    
    /**
     * Constructor
     */
    public GreedyVRP(Instance instance) {
        this.instance = instance;

		this.routeList = new ArrayList<Route>();
        for (Vehicle v : instance.getVehicleList()) {
			Route route = new Route("route_" + v.getId(), v);
			this.routeList.add(route);
        }
    }


    public Solution getInitialSolution() {
        // The final Solution
        Solution solution = new Solution();

        // Fetch the depot node.
        Delivery depot = this.instance.getDeliveryList().remove(0);

        // Fetch the first available vehicle
        Route curRoute = this.routeList.remove(0);

        // Add the depot to the vehicle.
        curRoute.append(depot);

        // Repeat until all customers are routed or if we run out vehicles.
        while (true) {
            // If we served all customers, exit.
            if (this.instance.getDeliveryList().size() == 0) {
                break;
            }

            // Get the last node of the current route. We will try to find the closest node to it that also satisfies the capacity constraint.
            Delivery lastInTheCurrentRoute = curRoute.getLastNodeOfTheRoute();

            // The distance of the closest node, if any, to the last node in the route.
            double smallestDistance = Double.MAX_VALUE;

            // The closest node, if any, to the last node in the route that also satisfies the capacity constraint.
            Delivery closestDelivery = null;

            // Find the nearest neighbor based on distance
            for (Delivery n: this.instance.getDeliveryList()) {

                double dist = this.instance.getDistanceDict().between(lastInTheCurrentRoute, n);

                // If we found a customer with closer that the value of "smallestDistance" ,store him temporarily
                boolean ifDistValid = (dist < smallestDistance);
                boolean ifCapacityValid = (curRoute.getMeasure().amount + n.getAmount()) <= curRoute.getVehicle().getCapacity();
                boolean ifArrTimeValid = (curRoute.getMeasure().time + this.instance.getDistanceDict().between(lastInTheCurrentRoute, n)) < n.getTimeWindow().getEnd();
                boolean ifWithinSchedule = (curRoute.getMeasure().time + this.instance.getDistanceDict().between(lastInTheCurrentRoute, n) + n.getServiceTime() +  this.instance.getDistanceDict().between(n, depot) ) < depot.getTimeWindow().getEnd();

                if (ifDistValid && ifCapacityValid && ifArrTimeValid && ifWithinSchedule) {
                    smallestDistance = dist;
                    closestDelivery = n;
                }
            }
            
            // A node that satisfies the capacity constraint found
            if (closestDelivery != null) {
                // Increase the cost of the current route by the distance of previous last node to the new one
                curRoute.getMeasure().distance += smallestDistance;

                // Increase the time of the current route by the distance of previous last node to the new one and serves time
                curRoute.getMeasure().time += smallestDistance;
                
                // waiting time windows open
                if (curRoute.getMeasure().time < closestDelivery.getTimeWindow().getStart()) curRoute.getMeasure().time = closestDelivery.getTimeWindow().getStart();
                
                curRoute.getMeasure().time += closestDelivery.getServiceTime();
                
                // Increase the load of the vehicle by the demand of the new node-customer
                curRoute.getMeasure().amount += closestDelivery.getAmount();

                // Add the closest node to the route
                curRoute.append(closestDelivery);
                
                // Remove customer from the non-served customers list.
                this.instance.getDeliveryList().remove(closestDelivery);

            // We didn't find any node that satisfies the condition.
            } else {
                // Increase cost by the distance to travel from the last node back to depot
                curRoute.getMeasure().distance += this.instance.getDistanceDict().between(lastInTheCurrentRoute, depot);
                curRoute.getMeasure().time += this.instance.getDistanceDict().between(lastInTheCurrentRoute, depot);

                // Terminate current route by adding the depot as a final destination
                curRoute.append(depot);
                
                curRoute.getMeasure().calculateTotalCost();

                // Add the finalized route to the solution
                solution.addRoute(curRoute);

                // Increase the solution's total cost by the cost of the finalized route
                solution.setTotalCost(solution.getTotalCost() + curRoute.getMeasure().distance);
                
                // If we used all vehicles, exit.
                if ( this.routeList.size()==0 ) {
                	break;
                	
                // if we still have some vehicles, use.
                } else {
                	// Recruit a new vehicle.
                    curRoute = this.routeList.remove(0);

                    // Add the depot as a starting point to the new route
                    curRoute.append(depot);
                }
            }
        }

        // Now add the final route to the solution
        curRoute.getMeasure().distance += this.instance.getDistanceDict().between(curRoute.getLastNodeOfTheRoute(), depot);
        curRoute.getMeasure().time += this.instance.getDistanceDict().between(curRoute.getLastNodeOfTheRoute(), depot);
        curRoute.append(depot);
        curRoute.getMeasure().calculateTotalCost();
        
        solution.addRoute(curRoute);
        solution.setTotalCost(solution.getTotalCost() + curRoute.getMeasure().distance);
        solution.setTotalCost((double)(Math.round(solution.getTotalCost() * 1000) / 1000.0));

        return solution;
    }

}
