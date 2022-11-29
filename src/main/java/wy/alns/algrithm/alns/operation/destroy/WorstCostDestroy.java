package wy.alns.algrithm.alns.operation.destroy;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.ALNSSolution;
import wy.alns.vo.*;

import java.util.ArrayList;
import java.util.Collections;

/**
 * WorstCostDestroy
 *
 * @author Yu Wang
 * @date  2022-11-20
 */
@Slf4j
public class WorstCostDestroy extends ALNSAbstractDestroy implements IALNSDestroy {
	@Override
	public ALNSSolution destroy(ALNSSolution sol, int removeNr) {
		if (!checkSolution(sol)) {
			return sol;
		}
        
		// 计算fitness值，对客户进行评估。
		ArrayList<Fitness> customerFitness = new  ArrayList<Fitness>();
        for(Route route : sol.routes) {
			for (int i = 1; i < route.getServiceList().size() - 1; ++i) {
				Delivery customer = (Delivery) route.getServiceList().get(i);
				double fitness = Fitness.calculateFitness(sol.instance, customer, route);
				customerFitness.add(new Fitness(customer.getId(), fitness));
			}
    	}
        Collections.sort(customerFitness);

        ArrayList<Integer> removal = new ArrayList<Integer>();
        for(int i = 0; i < removeNr; ++i) removal.add(customerFitness.get(i).customerNo);
        
        for(int j = 0; j < sol.routes.size(); j++) {
        	for (int i = 0; i < sol.routes.get(j).getServiceList().size(); ++i) {
        		Service customer = sol.routes.get(j).getServiceList().get(i);
        		if(removal.contains(customer.getId())) {
        			sol.removeStop(sol.routes.get(j), i);
        		}	
        	} 
    	}
        return sol;
    }
}

class Fitness implements Comparable<Fitness>{
	public int customerNo;
	public double fitness;
	
	public Fitness(int cNo, double f) {
		customerNo = cNo;
		fitness = f;
	}
	
	public static double calculateFitness(Instance instance, Delivery customer, Route route) {
		DistanceDict distanceDict = instance.getDistanceDict();

		Service n0 = route.getServiceList().get(0);
		double v = route.getMeasure().getTimeViolation() + route.getMeasure().getLoadViolation() + customer.getAmount();
		double distFactor = distanceDict.between(customer, n0) + distanceDict.between(n0, customer);
		double fitness = v * distFactor;

		return fitness;
	}
	
	@Override
	public int compareTo(Fitness o) {
		Fitness s = (Fitness) o;
		if (s.fitness > this.fitness  ) {
			return 1;
		} else if (this.fitness == s.fitness) {
			return 0;
		} else {
			return -1;
		}
	}

}
