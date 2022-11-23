package wy.alns.algrithm;

import java.util.ArrayList;
import java.util.List;

import wy.alns.vo.*;


/**
 * MyALNSSolution
 *
 * @author Yu Wang
 * @date  2022-11-19
 */
public class MyALNSSolution {
	
    public List<Route> routes;
    public Measure measure;
    public int vehicleNr;
    public Instance instance;
	
	public double alpha;		// α
	public double beta;		// β
	
	public static final double  punish = 1000;
	
	public ArrayList<Node> removalCustomers;

    public MyALNSSolution(Instance instance) {
        this.routes = new ArrayList<>();
        this.measure = new Measure();
        this.vehicleNr = 0;
        this.instance = instance;
        
        this.alpha = punish;
        this.beta = punish;
        
        this.removalCustomers = new ArrayList<Node>();
    }
    
    public MyALNSSolution(Solution sol, Instance instance) {
        this.measure = new Measure();
        measure.distance = sol.getTotalCost();
        measure.calculateTotalCost();
        this.vehicleNr = sol.getVehicleNr();
        this.instance = instance;
        
        this.alpha = punish;
        this.beta = punish;
        
        this.routes = new ArrayList<>();
        for (Route route: sol.getRoutes()) {
            this.routes.add(route.cloneRoute());
        }
        
        this.removalCustomers = new ArrayList<Node>();
    }
    
    public MyALNSSolution(MyALNSSolution sol) {
    	this.measure = new Measure(sol.measure);
        this.vehicleNr = sol.vehicleNr;
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

		Node n0 = route.getRoute().get(cusPosition - 1);
		Node n = route.getRoute().get(cusPosition);
		Node n1 = route.getRoute().get(cusPosition + 1);

		double cost = distance.between(n0, n1) - distance.between(n0, n) - distance.between(n, n1);
		double load = -n.getDemand();

		this.measure.distance += cost;
		this.routes.get(routePosition).getCost().distance += cost;
		this.routes.get(routePosition).getCost().load += load;

		this.measure.loadViolation -= this.routes.get(routePosition).getCost().loadViolation;
		this.measure.timeViolation -= this.routes.get(routePosition).getCost().timeViolation;
		
		removalCustomers.add(route.removeNode(cusPosition));
	}
	
	public void insertCustomer(int routePosition, int insertCusPosition, Node insertCustomer) {
		//TODO : duplicated TW
		Distance distance = instance.getDistance();

		Route route = this.routes.get(routePosition);

		Node n0 = route.getRoute().get(insertCusPosition - 1);
		Node n = insertCustomer;
		Node n1 = route.getRoute().get(insertCusPosition);

		double cost = distance.between(n0, n) + distance.between(n, n1) - distance.between(n0, n1);
		double load = +n.getDemand();


		// 更新当前路径、总路径的cost、load、load violation
		this.measure.distance += cost;
		this.routes.get(routePosition).getCost().distance += cost;
		this.routes.get(routePosition).getCost().load += load;
		if (this.routes.get(routePosition).getCost().load > this.instance.getVehicleCapacity())
			this.measure.loadViolation += this.routes.get(routePosition).getCost().load - this.instance.getVehicleCapacity();
		
		route.addNodeToRouteWithIndex(insertCustomer, insertCusPosition);;
		
		// 计算当前路径的time windows violation、time
		double time = 0;
		double timeWindowViolation = 0;
		for (int i = 1; i < route.getRoute().size(); i++) {
			time += distance.between(route.getRoute().get(i - 1), route.getRoute().get(i));
			if (time < route.getRoute().get(i).getTimeWindow()[0])
				time = route.getRoute().get(i).getTimeWindow()[0];
			else if (time > route.getRoute().get(i).getTimeWindow()[1])
				timeWindowViolation += time - route.getRoute().get(i).getTimeWindow()[1];
			
			time += route.getRoute().get(i).getServiceTime();
		}
		
		// 计算当前路径、总路径的time windows violation、time
		this.routes.get(routePosition).getCost().time = time;
		this.routes.get(routePosition).getCost().timeViolation = timeWindowViolation;
		this.measure.timeViolation += timeWindowViolation;
		
		this.measure.calculateTotalCost(this.alpha, this.beta);
	}
	
	public void evaluateInsertCustomer(int routePosition, int insertCusPosition, Node insertCustomer, Measure newMeasure) {
		//TODO : duplicated TW
		Distance distance = instance.getDistance();

		Route route = this.routes.get(routePosition).cloneRoute();

		Node n0 = route.getRoute().get(insertCusPosition - 1);
		Node n = insertCustomer;
		Node n1 = route.getRoute().get(insertCusPosition);

		double cost = distance.between(n0, n) + distance.between(n, n1) - distance.between(n0, n1);
		double load = +n.getDemand();


		
		newMeasure.load += load;
		newMeasure.distance += cost;
		if (newMeasure.load > this.instance.getVehicleCapacity())
			newMeasure.loadViolation += this.measure.load - this.instance.getVehicleCapacity();
		
		route.addNodeToRouteWithIndex(insertCustomer, insertCusPosition);;
		
		double time = 0;
		double timeWindowViolation = 0;
		for (int i = 1; i < route.getRoute().size(); i++) {
			time += distance.between(route.getRoute().get(i - 1), route.getRoute().get(i));
			if (time < route.getRoute().get(i).getTimeWindow()[0])
				time = route.getRoute().get(i).getTimeWindow()[0];
			else if (time > route.getRoute().get(i).getTimeWindow()[1])
				timeWindowViolation += time - route.getRoute().get(i).getTimeWindow()[1];
			
			time += route.getRoute().get(i).getServiceTime();
		}
		
		newMeasure.time = time;
		newMeasure.timeViolation = timeWindowViolation;
		
		newMeasure.calculateTotalCost(this.alpha, this.beta);
	}
	
	public boolean feasible() {
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
		sol.setVehicleNr(vehicleNr);
		
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
    
	public int compareTo(MyALNSSolution arg0) {
		if (this.measure.totalCost >  arg0.measure.totalCost) {
			return 1;
		} else if (this.measure == arg0.measure) {
			return 0;
		} else {
			return -1;
		}
	}

}
