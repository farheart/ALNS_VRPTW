package wy.alns.algrithm.solver;


import wy.alns.algrithm.solution.Solution;
import wy.alns.vo.*;

import java.util.ArrayList;
import java.util.List;


/**
 * GreedySolver : solve using greedy
 *
 * @author Yu Wang
 * @date  2022-11-17
 */
public class GreedySolver {
    private Instance instance;
    
    private List<Route> routeList;

    
    /**
     * Constructor
     */
    public GreedySolver(Instance instance) {
        this.instance = instance;

		this.routeList = new ArrayList<Route>();
        for (Vehicle v : instance.getVehicleList()) {
			Route route = new Route("route_" + v.getId(), v);
			this.routeList.add(route);
        }
    }


    public Solution getInitialSolution() {
        Solution solution = new Solution();

        // Create a route using the first available vehicle
        Route curRoute = this.routeList.remove(0);

        // Add the depot as the 1st stop of the route
        curRoute.append(this.instance.getDepot());

        // Loop until all delivery are batched or no available vehicles.
        while (this.instance.getDeliveryList().size() > 0) {

            // Find the nearest delivery to the last node, which also meets all constraints
            Delivery nearestDelivery = findNearestDelivery(curRoute);

            // Find a node meeting the capacity constraint
            if (nearestDelivery != null) {
                this.addToRoute(curRoute, nearestDelivery);

                // Remove delivery from the non-served list.
                this.instance.getDeliveryList().remove(nearestDelivery);

            } else {
                // Close the route and add to solution
                this.closeRoute(curRoute);
                this.addRoute(solution, curRoute);

                // Create a new route if still have available vehicle
                if (this.routeList.size() > 0) {
                    curRoute = this.routeList.remove(0);
                    curRoute.append(this.instance.getDepot());
                } else {
                    // no vehicles, break loop
                    break;
                }
            }
        }

        // Close and add the final route to solution
        this.closeRoute(curRoute);
        this.addRoute(solution, curRoute);

        return solution;
    }

    private void addToRoute(Route curRoute, Delivery newDelivery) {
        Delivery lastDelivery = curRoute.getLastNodeOfTheRoute();

        double d = this.instance.getDistanceDict().between(lastDelivery, newDelivery);

        // Increase total distance of the route by adding the distance of previous last node to the new one
        curRoute.getMeasure().distance += d;

        // Increase the time by (distance of previous last node to the new one + waiting time + service time)
        curRoute.getMeasure().time += d;

        // waiting until TW open
        if (curRoute.getMeasure().time < newDelivery.getTimeWindow().getStart()) {
            curRoute.getMeasure().time = newDelivery.getTimeWindow().getStart();
        }

        curRoute.getMeasure().time += newDelivery.getServiceTime();

        // Increase the load of vehicle by the amount of the new delivery
        curRoute.getMeasure().amount += newDelivery.getAmount();

        // Add the nearest delivery / node to the route
        curRoute.append(newDelivery);
    }


    private static void addRoute(Solution solution, Route curRoute) {
        // Add route to the solution
        solution.addRoute(curRoute);
        solution.setTotalCost(solution.getTotalCost() + curRoute.getMeasure().distance);
    }


    private void closeRoute(Route curRoute) {
        Delivery lastDelivery = curRoute.getLastNodeOfTheRoute();
        Delivery depot = this.instance.getDepot();

        // Send back to depot to close the trip
        curRoute.getMeasure().distance += this.instance.getDistanceDict().between(lastDelivery, depot);
        curRoute.getMeasure().time += this.instance.getDistanceDict().between(lastDelivery, depot);
        curRoute.getMeasure().calculateTotalCost();
        curRoute.append(depot);
    }


    private Delivery findNearestDelivery(Route curRoute) {
        Delivery lastDelivery = curRoute.getLastNodeOfTheRoute();

        // 1st stop is the depot
        Delivery depot = curRoute.getDeliveryList().get(0);

        // The distance of the nearest node to the last node in the route.
        double minDist = Double.MAX_VALUE;

        // The nearest node, if any, to the last node in the route that also satisfies the capacity constraint.
        Delivery nearestDelivery = null;

        // Find the nearest neighbor based on distance
        for (Delivery n: this.instance.getDeliveryList()) {
            double dist = this.instance.getDistanceDict().between(lastDelivery, n);

            // If find closer delivery, save it
            boolean ifCloser = (dist < minDist);
            boolean ifCapacityOK = (curRoute.getMeasure().amount + n.getAmount() <= curRoute.getVehicle().getCapacity());
            boolean ifArrTimeOK = (curRoute.getMeasure().time + dist < n.getTimeWindow().getEnd());
            boolean ifDepotOK = (curRoute.getMeasure().time + dist + n.getServiceTime() +  this.instance.getDistanceDict().between(n, depot) < depot.getTimeWindow().getEnd());

            if (ifCloser && ifCapacityOK && ifArrTimeOK && ifDepotOK) {
                minDist = dist;
                nearestDelivery = n;
            }
        }
        return nearestDelivery;
    }

}
