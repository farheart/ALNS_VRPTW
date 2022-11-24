package wy.alns.operation.destroy;

import java.util.ArrayList;
import java.util.Collections;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.ALNSSolution;
import wy.alns.vo.DistanceDict;
import wy.alns.vo.Order;
import wy.alns.vo.Route;
import wy.alns.vo.Instance;

/**
 * WorstCostDestroy
 *
 * @author Yu Wang
 * @date  2022-11-20
 */
@Slf4j
public class WorstCostDestroy extends ALNSAbstractDestroy implements IALNSDestroy {

	@Override
	public ALNSSolution destroy(ALNSSolution s, int removeNr) throws Exception {
		if (!checkSolution(s)) {
			return s;
		}
        
		// 计算fitness值，对客户进行评估。
		ArrayList<Fitness> customerFitness = new  ArrayList<Fitness>();
        for(Route route : s.routes) {
        	for (Order customer : route.getOrderList()) {
        		double fitness = Fitness.calculateFitness(s.instance, customer, route);
        		customerFitness.add(new Fitness(customer.getId(), fitness));
        	}
    	}
        Collections.sort(customerFitness);

        ArrayList<Integer> removal = new ArrayList<Integer>();
        for(int i = 0; i < removeNr; ++i) removal.add(customerFitness.get(i).customerNo);
        
        for(int j = 0; j < s.routes.size(); j++) {
        	for (int i = 0; i < s.routes.get(j).getOrderList().size(); ++i) {
        		Order customer = s.routes.get(j).getOrderList().get(i);
        		if(removal.contains(customer.getId())) {
        			s.removeCustomer(j, i);
        		}	
        	} 
    	}
    	
        return s;
    }
}

class Fitness implements Comparable<Fitness>{
	public int customerNo;
	public double fitness;
	
	public Fitness() {}
	
	public Fitness(int cNo, double f) {
		customerNo = cNo;
		fitness = f;
	}
	
	public static double calculateFitness(Instance instance, Order customer, Route route) {
		DistanceDict distanceDict = instance.getDistanceDict();

		Order n0 = route.getOrderList().get(0);
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
