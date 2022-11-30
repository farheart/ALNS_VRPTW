package wy.alns.algrithm.alns;

import wy.alns.algrithm.solution.Solution;
import wy.alns.vo.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * ALNSResult
 *
 * @author Yu Wang
 * @date  2022-11-19
 */
public class ALNSResult {
    public Instance instance;

    public List<Route> routes;

	public List<Delivery> removeSet;

	public Measure measure;

	public int numVehicle;

	public double alpha;
	public double beta;
	public static final double penalty = 1000;


    private ALNSResult(Instance instance) {
        this.instance = instance;

        this.measure = new Measure();
        this.numVehicle = 0;

        this.routes = new LinkedList<>();

        this.alpha = penalty;
        this.beta = penalty;
        
        this.removeSet = new LinkedList<>();
    }
    
    public ALNSResult(Solution sol, Instance instance) {
		this(instance);

        measure.distance = sol.getTotalCost();
        measure.calculateTotalCost();
        this.numVehicle = sol.getNumVehicle();

        for (Route route: sol.getRoutes()) {
            this.routes.add(route.cloneRoute());
        }
    }
    
    public ALNSResult(ALNSResult sol) {
        this.instance = sol.instance;

    	this.measure = new Measure(sol.measure);
        this.numVehicle = sol.numVehicle;


        this.routes = new ArrayList<>();
        for (Route route: sol.routes) {
            this.routes.add(route.cloneRoute());
        }

        this.alpha = sol.alpha;
        this.beta = sol.beta;

		this.removeSet = new LinkedList<>();
    }


	public void removeStop(Route route, int removePos) {
		//TODO : duplicated TW
		DistanceDict distanceDict = instance.getDistanceDict();

		Service n0 = route.getServiceList().get(removePos - 1);
		Delivery n = (Delivery) route.getServiceList().get(removePos);
		Service n1 = route.getServiceList().get(removePos + 1);

		double dist = distanceDict.between(n0, n1) - distanceDict.between(n0, n) - distanceDict.between(n, n1);
		double amount = -n.getAmount();

		route.getMeasure().distance += dist;
		route.getMeasure().amount += amount;

		this.measure.distance += dist;
		this.measure.loadViolation -= route.getMeasure().loadViolation;
		this.measure.timeViolation -= route.getMeasure().timeViolation;
		
		removeSet.add((Delivery) route.removeNode(removePos));
	}


	public void insertStop(Route route, int insertPos, Delivery stop) {
		Measure tmp = doInsert(stop, insertPos, route);

		route.getMeasure().distance += tmp.getDistance();
		route.getMeasure().amount += tmp.getAmount();
		route.getMeasure().time = tmp.getTime();
		route.getMeasure().timeViolation = tmp.getTimeViolation();

		this.measure.distance += tmp.getDistance();
		this.measure.loadViolation += tmp.getLoadViolation();
		this.measure.timeViolation += tmp.getTimeViolation();
		this.measure.calculateTotalCost(this.alpha, this.beta);
	}


	public Measure evalInsertStop(Route route, int insertPos, Delivery stop) {
		Route tmpRoute = route.cloneRoute();
		Measure tmp = doInsert(stop, insertPos, tmpRoute);

		Measure evalMeasure = new Measure(this.measure);
		evalMeasure.distance += tmp.getDistance();
		evalMeasure.amount += tmp.getAmount();
		evalMeasure.time = tmp.getTime();
		evalMeasure.loadViolation += tmp.getLoadViolation();
		evalMeasure.timeViolation = tmp.getTimeViolation();
		evalMeasure.calculateTotalCost(this.alpha, this.beta);

		return evalMeasure;
	}


	private Measure doInsert(Delivery stop, int insertPos, Route route) {
		//TODO : duplicated TW
		Measure tmp = new Measure();

		Service n0 = route.getServiceList().get(insertPos - 1);
		Delivery n = stop;
		Service n1 = route.getServiceList().get(insertPos);

		// calculate dist, load, load violation
		DistanceDict distanceDict = instance.getDistanceDict();
		double dist = distanceDict.between(n0, n) + distanceDict.between(n, n1) - distanceDict.between(n0, n1);
		double amount = +n.getAmount();

		tmp.setDistance(dist);
		tmp.setAmount(amount);

		double load = route.getMeasure().getAmount() + tmp.getAmount();
		if (load > route.getVehicle().getCapacity()) {
			tmp.setLoadViolation(load - route.getVehicle().getCapacity());
		}
		route.addNode(stop, insertPos);

		// calculate TW violation, time
		double time = 0;
		double timeWindowViolation = 0;
		for (int i = 1; i < route.getServiceList().size()-1; i++) {
			Service s = route.getServiceList().get(i);
			time += distanceDict.between(route.getServiceList().get(i - 1), s);
			if (time < s.getTimeWindow().getStart()) {
				time = s.getTimeWindow().getStart();
			} else if (time > s.getTimeWindow().getEnd()) {
				timeWindowViolation += time - s.getTimeWindow().getEnd();
			}

			if (s instanceof Delivery) {
				Delivery delivery = (Delivery) s;
				time += delivery.getServiceTime();
			}
		}
		tmp.setTime(time);
		tmp.setTimeViolation(timeWindowViolation);

		return tmp;
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


}
