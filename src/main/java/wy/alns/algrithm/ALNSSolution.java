package wy.alns.algrithm;

import wy.alns.vo.*;

import java.util.ArrayList;
import java.util.List;


/**
 * ALNSSolution
 *
 * @author Yu Wang
 * @date  2022-11-19
 */
public class ALNSSolution {
    public List<Route> routes;
    public Measure measure;

	public int numVehicle;

    public Instance instance;

	public double alpha;
	public double beta;
	
	public static final double penalty = 1000;
	
	public ArrayList<Node> removalCustomers;

    public ALNSSolution(Instance instance) {
        this.routes = new ArrayList<>();
        this.measure = new Measure();
        this.numVehicle = 0;
        this.instance = instance;
        
        this.alpha = penalty;
        this.beta = penalty;
        
        this.removalCustomers = new ArrayList<Node>();
    }
    
    public ALNSSolution(Solution sol, Instance instance) {
		this(instance);
        this.numVehicle = sol.getNumVehicle();
        measure.distance = sol.getTotalCost();
        measure.calculateTotalCost();
        for (Route route: sol.getRoutes()) {
            this.routes.add(route.cloneRoute());
        }
    }
    
    public ALNSSolution(ALNSSolution sol) {
    	this.measure = new Measure(sol.measure);
        this.numVehicle = sol.numVehicle;
        this.instance = sol.instance;
        
        this.alpha = sol.alpha;
        this.beta = sol.beta;

        this.routes = new ArrayList<>();
        for (Route route: sol.routes) {
            this.routes.add(route.cloneRoute());
        }
        
        this.removalCustomers = new ArrayList<Node>();
    }
    
	public void removeCustomer(int routePosition, int cusPosition) {
		//TODO : duplicated TW
		Distance distance = instance.getDistance();

		Route route = this.routes.get(routePosition);

		Node n0 = route.getNodeList().get(cusPosition - 1);
		Node n = route.getNodeList().get(cusPosition);
		Node n1 = route.getNodeList().get(cusPosition + 1);

		double dist = distance.between(n0, n1) - distance.between(n0, n) - distance.between(n, n1);
		double load = -n.getDemand();

		this.measure.distance += dist;
		route.getMeasure().distance += dist;
		route.getMeasure().load += load;

		this.measure.loadViolation -= route.getMeasure().loadViolation;
		this.measure.timeViolation -= route.getMeasure().timeViolation;
		
		removalCustomers.add(route.removeNode(cusPosition));
	}


	public void insertCustomer(int routePosition, int insertCusPosition, Node insertCustomer) {
		//TODO : duplicated TW
		Distance distance = instance.getDistance();

		Route route = this.routes.get(routePosition);

		Node n0 = route.getNodeList().get(insertCusPosition - 1);
		Node n = insertCustomer;
		Node n1 = route.getNodeList().get(insertCusPosition);

		double dist = distance.between(n0, n) + distance.between(n, n1) - distance.between(n0, n1);
		double load = +n.getDemand();

		// update dist, load,load violation of current route and total
		this.measure.distance += dist;
		route.getMeasure().distance += dist;
		route.getMeasure().load += load;

		if (route.getMeasure().load > route.getVehicle().getCapacity()) {
			this.measure.loadViolation += route.getMeasure().load - route.getVehicle().getCapacity();
		}
		route.addNode(insertCustomer, insertCusPosition);;
		
		// calculate TW violation, time
		double time = 0;
		double timeWindowViolation = 0;
		for (int i = 1; i < route.getNodeList().size(); i++) {
			time += distance.between(route.getNodeList().get(i - 1), route.getNodeList().get(i));
			if (time < route.getNodeList().get(i).getTimeWindow()[0]) {
				time = route.getNodeList().get(i).getTimeWindow()[0];
			} else if (time > route.getNodeList().get(i).getTimeWindow()[1]) {
				timeWindowViolation += time - route.getNodeList().get(i).getTimeWindow()[1];
			}
			time += route.getNodeList().get(i).getServiceTime();
		}

		route.getMeasure().time = time;
		route.getMeasure().timeViolation = timeWindowViolation;

		this.measure.timeViolation += timeWindowViolation;
		this.measure.calculateTotalCost(this.alpha, this.beta);
	}


	public void evaluateInsertCustomer(int routePosition, int insertCusPosition, Node insertCustomer, Measure evalMeasure) {
		//TODO : duplicated TW
		Distance distance = instance.getDistance();

		Route route = this.routes.get(routePosition).cloneRoute();

		Node n0 = route.getNodeList().get(insertCusPosition - 1);
		Node n = insertCustomer;
		Node n1 = route.getNodeList().get(insertCusPosition);

		double cost = distance.between(n0, n) + distance.between(n, n1) - distance.between(n0, n1);
		double load = +n.getDemand();

		evalMeasure.load += load;
		evalMeasure.distance += cost;

		if (evalMeasure.load > route.getVehicle().getCapacity()) {
			evalMeasure.loadViolation += this.measure.load - route.getVehicle().getCapacity();
		}
		
		route.addNode(insertCustomer, insertCusPosition);;
		
		double time = 0;
		double timeWindowViolation = 0;
		for (int i = 1; i < route.getNodeList().size(); i++) {
			time += distance.between(route.getNodeList().get(i - 1), route.getNodeList().get(i));
			if (time < route.getNodeList().get(i).getTimeWindow()[0]) {
				time = route.getNodeList().get(i).getTimeWindow()[0];
			} else if (time > route.getNodeList().get(i).getTimeWindow()[1]) {
				timeWindowViolation += time - route.getNodeList().get(i).getTimeWindow()[1];
			}
			time += route.getNodeList().get(i).getServiceTime();
		}
		
		evalMeasure.time = time;
		evalMeasure.timeViolation = timeWindowViolation;
		
		evalMeasure.calculateTotalCost(this.alpha, this.beta);
	}


	public boolean isFeasible() {
		return (measure.timeViolation < 0.01 && measure.loadViolation < 0.01);
	}


	public Solution toSolution() {
		Solution sol = new Solution();
		
		List<Route> solutionRoutes = new ArrayList<>();
        for (Route route: this.routes) {
        	solutionRoutes.add(route.cloneRoute());
        }
		
		sol.setRoutes(solutionRoutes);
		sol.setTotalCost(measure.distance);
		sol.setNumVehicle(numVehicle);
		
		return sol;
	}
        
    @Override
    public String toString() {
        String result = "Solution{" +
                "Cost = " + measure +
                ", routes = [";

        for (Route vehicle: this.routes) {
        	result += "\n\t" + vehicle;
        }

        return result + "]}";
    }
    
	public int compareTo(ALNSSolution arg0) {
		if (this.measure.totalCost >  arg0.measure.totalCost) {
			return 1;
		} else if (this.measure == arg0.measure) {
			return 0;
		} else {
			return -1;
		}
	}

}
