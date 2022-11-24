package wy.alns.algrithm;

import java.util.ArrayList;
import java.util.List;

import wy.alns.vo.*;


/**
 * ALNSSolution
 *
 * @author Yu Wang
 * @date  2022-11-19
 */
public class ALNSSolution {
    public List<Route> routes;

    public Measure measure;

	public int vehicleNr;

    public Instance instance;
	
	public double alpha;		// α
	public double beta;		// β
	
	public static final double  punish = 1000;
	
	public ArrayList<Node> removalCustomers;

    public ALNSSolution(Instance instance) {
        this.routes = new ArrayList<>();
        this.measure = new Measure();
        this.vehicleNr = 0;
        this.instance = instance;
        
        this.alpha = punish;
        this.beta = punish;
        
        this.removalCustomers = new ArrayList<Node>();
    }
    
    public ALNSSolution(Solution sol, Instance instance) {
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
    
    public ALNSSolution(ALNSSolution sol) {
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

		Node n0 = route.getNodeList().get(cusPosition - 1);
		Node n = route.getNodeList().get(cusPosition);
		Node n1 = route.getNodeList().get(cusPosition + 1);

		double cost = distance.between(n0, n1) - distance.between(n0, n) - distance.between(n, n1);
		double load = -n.getDemand();

		this.measure.distance += cost;
		this.routes.get(routePosition).getMeasure().distance += cost;
		this.routes.get(routePosition).getMeasure().load += load;

		this.measure.loadViolation -= this.routes.get(routePosition).getMeasure().loadViolation;
		this.measure.timeViolation -= this.routes.get(routePosition).getMeasure().timeViolation;
		
		removalCustomers.add(route.removeNode(cusPosition));
	}
	
	public void insertCustomer(int routePosition, int insertCusPosition, Node insertCustomer) {
		//TODO : duplicated TW
		Distance distance = instance.getDistance();

		Route curRoute = this.routes.get(routePosition);
		Route route = curRoute;

		Node n0 = route.getNodeList().get(insertCusPosition - 1);
		Node n = insertCustomer;
		Node n1 = route.getNodeList().get(insertCusPosition);

		double cost = distance.between(n0, n) + distance.between(n, n1) - distance.between(n0, n1);
		double load = +n.getDemand();


		// 更新当前路径、总路径的cost、load、load violation
		this.measure.distance += cost;
		curRoute.getMeasure().distance += cost;
		curRoute.getMeasure().load += load;

		if (curRoute.getMeasure().load > curRoute.getVehicle().getCapacity()) {
			this.measure.loadViolation += curRoute.getMeasure().load - curRoute.getVehicle().getCapacity();
		}

		route.addNode(insertCustomer, insertCusPosition);;
		
		// 计算当前路径的time windows violation、time
		double time = 0;
		double timeWindowViolation = 0;
		for (int i = 1; i < route.getNodeList().size(); i++) {
			time += distance.between(route.getNodeList().get(i - 1), route.getNodeList().get(i));
			if (time < route.getNodeList().get(i).getTimeWindow()[0])
				time = route.getNodeList().get(i).getTimeWindow()[0];
			else if (time > route.getNodeList().get(i).getTimeWindow()[1])
				timeWindowViolation += time - route.getNodeList().get(i).getTimeWindow()[1];
			
			time += route.getNodeList().get(i).getServiceTime();
		}
		
		// 计算当前路径、总路径的time windows violation、time
		curRoute.getMeasure().time = time;
		curRoute.getMeasure().timeViolation = timeWindowViolation;
		this.measure.timeViolation += timeWindowViolation;
		
		this.measure.calculateTotalCost(this.alpha, this.beta);
	}
	
	public void evaluateInsertCustomer(int routePosition, int insertCusPosition, Node insertCustomer, Measure newMeasure) {
		//TODO : duplicated TW
		Distance distance = instance.getDistance();

		Route route = this.routes.get(routePosition).cloneRoute();

		Node n0 = route.getNodeList().get(insertCusPosition - 1);
		Node n = insertCustomer;
		Node n1 = route.getNodeList().get(insertCusPosition);

		double cost = distance.between(n0, n) + distance.between(n, n1) - distance.between(n0, n1);
		double load = +n.getDemand();


		
		newMeasure.load += load;
		newMeasure.distance += cost;
//		if (newMeasure.load > this.instance.getVehicleCapacity())
		if (newMeasure.load > route.getVehicle().getCapacity()) {
			newMeasure.loadViolation += this.measure.load - route.getVehicle().getCapacity();
		}
		
		route.addNode(insertCustomer, insertCusPosition);;
		
		double time = 0;
		double timeWindowViolation = 0;
		for (int i = 1; i < route.getNodeList().size(); i++) {
			time += distance.between(route.getNodeList().get(i - 1), route.getNodeList().get(i));
			if (time < route.getNodeList().get(i).getTimeWindow()[0])
				time = route.getNodeList().get(i).getTimeWindow()[0];
			else if (time > route.getNodeList().get(i).getTimeWindow()[1])
				timeWindowViolation += time - route.getNodeList().get(i).getTimeWindow()[1];
			
			time += route.getNodeList().get(i).getServiceTime();
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
