package wy.alns.algrithm.alns.operation.repair;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.ALNSSolution;
import wy.alns.vo.Delivery;
import wy.alns.vo.Measure;

import java.util.ArrayList;
import java.util.Collections;


/**
 * RegretRepair
 *
 * @author Yu Wang
 * @date  2022-11-20
 */
@Slf4j
public class RegretRepair extends ALNSAbstractRepair implements IALNSRepair {

	@Override
	public ALNSSolution repair(ALNSSolution sol) {
		if (!checkSolution(sol)) {
			return sol;
		}
    	
    	ArrayList<BestPos> bestPoses = new ArrayList<BestPos>();
    	
    	int removeNr = sol.removalCustomers.size();
    	
		for(int k = 0; k < removeNr; k++) {
			
			Delivery insertDelivery = sol.removalCustomers.remove(0);
			
			double first,second;
			int bestCusP = -1;
			int bestRouteP = -1;
			first = second = Double.POSITIVE_INFINITY;
        	
			for(int j = 0; j < sol.routes.size(); j++) {
        		
				if(sol.routes.get(j).getDeliveryList().size() < 1) {
        			continue;
        		}
        		
				// 寻找最优插入位置
            	for (int i = 1; i < sol.routes.get(j).getDeliveryList().size() - 1; ++i) {
            		
            		// 评价插入情况
    				Measure evalMeasure = new Measure(sol.measure);
    				sol.evaluateInsertCustomer(j, i, insertDelivery, evalMeasure);

            		if(evalMeasure.totalCost > Double.MAX_VALUE) {
            			evalMeasure.totalCost = Double.MAX_VALUE;
            		}
            		
            		// if a better insertion is found, set the position to insert in the move and update the minimum cost found
            		if (evalMeasure.totalCost < first) {
            			//log.info(varCost.checkFeasible());
            			bestCusP = i;
            			bestRouteP = j;
            			second = first;
            			first = evalMeasure.totalCost;
            		}else if(evalMeasure.totalCost < second && evalMeasure.totalCost != first) {
            			second = evalMeasure.totalCost;
            		}
            	}
        	}
        	bestPoses.add(new BestPos(insertDelivery, bestCusP, bestRouteP, second - first));
		}
		Collections.sort(bestPoses);
		
		for(BestPos bp : bestPoses) {
			sol.insertCustomer(bp.bestCustomerPos, bp.bestRoutePos, bp.insertDelivery);
		}
        return sol;
    }
}

class BestPos implements Comparable<BestPos>{
	public int bestRoutePos;
	public int bestCustomerPos;
	public Delivery insertDelivery;
	public double deltaCost;

	
	public BestPos(Delivery insertDelivery, int customer, int route, double f) {
		this.insertDelivery = insertDelivery;
		this.bestRoutePos = customer;
		this.bestCustomerPos = route;
		this.deltaCost = f;
	}
	
	@Override
	public int compareTo(BestPos o) {
		BestPos s = (BestPos) o;
		if (s.deltaCost > this.deltaCost) {
			return 1;
		} else if (this.deltaCost == s.deltaCost) {
			return 0;
		} else {
			return -1;
		}
	}
}
