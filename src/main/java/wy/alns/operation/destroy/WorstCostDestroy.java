package wy.alns.operation.destroy;

import java.util.ArrayList;
import java.util.Collections;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.MyALNSSolution;
import wy.alns.operation.ALNSAbstractOperation;
import wy.alns.vo.Distance;
import wy.alns.vo.Node;
import wy.alns.vo.Route;
import wy.alns.vo.Instance;

/**
 * WorstCostDestroy
 *
 * @author Yu Wang
 * @date  2022-11-20
 */
@Slf4j
public class WorstCostDestroy extends ALNSAbstractOperation implements IALNSDestroy {
    /*
	@Override
	public ALNSStrategieVisualizationManager getVisualizationManager() {
		// TODO Auto-generated method stub
		return null;
	}
	*/
	@Override
	public MyALNSSolution destroy(MyALNSSolution s, int removeNr) throws Exception {
		
		if(s.removalCustomers.size() != 0) {
			log.error("removalCustomers is not empty.");
			return s;
		}
        
		// 计算fitness值，对客户进行评估。
		ArrayList<Fitness> customerFitness = new  ArrayList<Fitness>();
        for(Route route : s.routes) {
        	for (Node customer : route.getNodeList()) {
        		double fitness = Fitness.calculateFitness(s.instance, customer, route);
        		customerFitness.add(new Fitness(customer.getId(), fitness));
        	}
    	}
        Collections.sort(customerFitness);

        ArrayList<Integer> removal = new ArrayList<Integer>();
        for(int i = 0; i < removeNr; ++i) removal.add(customerFitness.get(i).customerNo);
        
        for(int j = 0; j < s.routes.size(); j++) {
        	for (int i = 0; i < s.routes.get(j).getNodeList().size(); ++i) {
        		Node customer = s.routes.get(j).getNodeList().get(i);
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
	
	public static double calculateFitness(Instance instance, Node customer, Route route) {
		Distance distance = instance.getDistance();

		Node n0 = route.getNodeList().get(0);
		double v = route.getMeasure().getTimeViolation() + route.getMeasure().getLoadViolation() + customer.getDemand();
		double distFactor = distance.between(customer, n0) + distance.between(n0, customer);
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
