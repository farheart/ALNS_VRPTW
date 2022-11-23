package wy.alns.algrithm.solver;


import java.util.ArrayList;
import java.util.List;

import wy.alns.algrithm.Solution;
import wy.alns.vo.Distance;
import wy.alns.vo.Node;
import wy.alns.vo.Route;
import wy.alns.vo.Instance;


/**
 * GreedyVRP : solve using greedy
 *
 * @author Yu Wang
 * @date  2022-11-17
 */
public class GreedyVRP {

    /**
     * All the customers
     */
    private List<Node> customers;

    /**
     * All the vehicles.
     */
    private List<Route> vehicles;

    /**
     * distance map of every node to each other.
     */
    private Distance distance;
    
    private int vehicleCapacity;
    private int initialCustomerNr;

    public int getCustomerNr() {
        return this.initialCustomerNr;
    }

    public Distance getDistance() {
        return this.distance;
    }


    /**
     * Constructor
     */
    public GreedyVRP(Instance instance) { 	
		this.customers = instance.getCustomers();
		this.initialCustomerNr = instance.getCustomerNumber();
		this.distance = instance.getDistance();
		this.vehicleCapacity = instance.getVehicleCapacity();
		
		int vehicleNr = instance.getNumVehicle();
		this.vehicles = new ArrayList<Route>();
		for(int i = 0; i < vehicleNr; ++i) {
			Route route = new Route(i);
			this.vehicles.add(route);
        }
    }


    public Solution getInitialSolution() {
        // The final Solution
        Solution solution = new Solution();

        // Fetch the depot node.
        Node depot = this.customers.remove(0);

        // Fetch the first available vehicle
        Route currentVehicle = this.vehicles.remove(0);

        // Add the depot to the vehicle.
        currentVehicle.addNodeToRoute(depot);

        // Repeat until all customers are routed or if we run out vehicles.
        while (true) {
            // If we served all customers, exit.
            if (this.customers.size() == 0) {
                break;
            }

            // Get the last node of the current route. We will try to find the closest node to it that also satisfies the capacity constraint.
            Node lastInTheCurrentRoute = currentVehicle.getLastNodeOfTheRoute();

            // The distance of the closest node, if any, to the last node in the route.
            double smallestDistance = Double.MAX_VALUE;

            // The closest node, if any, to the last node in the route that also satisfies the capacity constraint.
            Node closestNode = null;

            // Find the nearest neighbor based on distance
            for (Node n: this.customers) {

                double dist = this.distance.between(lastInTheCurrentRoute, n);

                // If we found a customer with closer that the value of "smallestDistance" ,store him temporarily
                boolean ifDistValid = (dist < smallestDistance);
                boolean ifCapacityValid = (currentVehicle.getCost().load + n.getDemand()) <= vehicleCapacity;
                boolean ifArrTimeValid = (currentVehicle.getCost().time + distance.between(lastInTheCurrentRoute, n)) < n.getTimeWindow()[1];
                boolean ifWithinSchedule = (currentVehicle.getCost().time + distance.between(lastInTheCurrentRoute, n) + n.getServiceTime() +  distance.between(n, depot) ) < depot.getTimeWindow()[1];

                if (ifDistValid && ifCapacityValid && ifArrTimeValid && ifWithinSchedule) {
                    smallestDistance = dist;
                    closestNode = n;
                }
            }
            
            // A node that satisfies the capacity constraint found
            if (closestNode != null) {
                // Increase the cost of the current route by the distance of the previous final node to the new one
                currentVehicle.getCost().cost += smallestDistance;

                // Increase the time of the current route by the distance of the previous final node to the new one and serves time
                currentVehicle.getCost().time += smallestDistance;
                
                // waiting time windows open
                if (currentVehicle.getCost().time < closestNode.getTimeWindow()[0]) currentVehicle.getCost().time = closestNode.getTimeWindow()[0];
                
                currentVehicle.getCost().time += closestNode.getServiceTime();
                
                // Increase the load of the vehicle by the demand of the new node-customer
                currentVehicle.getCost().load += closestNode.getDemand();

                // Add the closest node to the route
                currentVehicle.addNodeToRoute(closestNode);
                
                // Remove customer from the non-served customers list.
                this.customers.remove(closestNode);

            // We didn't find any node that satisfies the condition.
            } else {
                // Increase cost by the distance to travel from the last node back to depot
                currentVehicle.getCost().cost += distance.between(lastInTheCurrentRoute, depot);
                currentVehicle.getCost().time += distance.between(lastInTheCurrentRoute, depot);

                // Terminate current route by adding the depot as a final destination
                currentVehicle.addNodeToRoute(depot);
                
                currentVehicle.getCost().calculateTotalCost();

                // Add the finalized route to the solution
                solution.addRoute(currentVehicle);

                // Increase the solution's total cost by the cost of the finalized route
                solution.setTotalCost(solution.getTotalCost() + currentVehicle.getCost().cost);
                
                // If we used all vehicles, exit.
                if ( this.vehicles.size()==0 ) {
                	break;
                	
                // if we still have some vehicles, use.
                } else {
                	// Recruit a new vehicle.
                    currentVehicle = this.vehicles.remove(0);

                    // Add the depot as a starting point to the new route
                    currentVehicle.addNodeToRoute(depot);
                }
            }
        }

        // Now add the final route to the solution
        currentVehicle.getCost().cost += distance.between(currentVehicle.getLastNodeOfTheRoute(), depot);
        currentVehicle.getCost().time += distance.between(currentVehicle.getLastNodeOfTheRoute(), depot);
        currentVehicle.addNodeToRoute(depot);
        currentVehicle.getCost().calculateTotalCost();
        
        solution.addRoute(currentVehicle);
        solution.setTotalCost(solution.getTotalCost() + currentVehicle.getCost().cost);
        solution.setTotalCost((double)(Math.round(solution.getTotalCost() * 1000) / 1000.0));

        return solution;
    }

}
