package wy.alns.algrithm.solution;

import lombok.Data;
import wy.alns.vo.Route;

import java.util.ArrayList;
import java.util.List;


/**
 * Solution
 *
 * @author Yu Wang
 * @date  2022-11-19
 */
@Data
public class Solution {
	public double solveTime;

    /**
     * All the routes of the current solution.
     */
    private List<Route> routes;

    /**
     * The total cost of the solution. It is calculated as the sum of the costs of all routes.
     */
    private double totalCost;
    
    /**
     * The number of the vehicles.
     */
    private int numVehicle;

    /**
     * Default constructor
     */
    public Solution() {
        this.routes = new ArrayList<>();
        this.totalCost = 0;
        this.numVehicle = 0;
    }

    public void addRoute(Route route) {
        this.routes.add(route);
    }


    /**
     * Return an exact copy of the current solution
     */
    public Solution clone() {
        Solution clone = new Solution();

        clone.totalCost = this.totalCost;
        clone.numVehicle = this.numVehicle;

        for (Route route: this.routes) {
            clone.routes.add(route.cloneRoute());
        }
        return clone;
    }

}