package wy.alns.operation.repair;

import lombok.extern.slf4j.Slf4j;
import wy.alns.vo.Delivery;
import wy.alns.vo.Measure;
import wy.alns.algrithm.ALNSSolution;

/**
 * GreedyRepair
 *
 * @author Yu Wang
 * @date  2022-11-20
 */
@Slf4j
public class GreedyRepair extends ALNSAbstractRepair implements IALNSRepair {

	@Override
	public ALNSSolution repair(ALNSSolution s) {
		if (!checkSolution(s)) {
			return s;
		}
    	
    	int removeNr = s.removalCustomers.size();
    	
		for(int k = 0; k < removeNr; k++) {
			
			Delivery insertDelivery = s.removalCustomers.remove(0);
			
			double bestCost;
			int bestCusP = -1;
			int bestRouteP = -1;
			bestCost = Double.POSITIVE_INFINITY;
        	
			for(int j = 0; j < s.routes.size(); j++) {			
        		
				if(s.routes.get(j).getDeliveryList().size() < 1) {
        			continue;
        		}
        		
				// 寻找最优插入位置
            	for (int i = 1; i < s.routes.get(j).getDeliveryList().size() - 1; ++i) {
            		
            		// 评价插入情况
    				Measure evalMeasure = new Measure(s.measure);
    				s.evaluateInsertCustomer(j, i, insertDelivery, evalMeasure);

            		if(evalMeasure.totalCost > Double.MAX_VALUE) {
            			evalMeasure.totalCost = Double.MAX_VALUE;
            		}
            		
            		// if a better insertion is found, set the position to insert in the move and update the minimum cost found
            		if (evalMeasure.totalCost < bestCost) {
            			//log.info(varCost.checkFeasible());
            			bestCusP = i;
            			bestRouteP = j;
            			bestCost = evalMeasure.totalCost;
            		}
            	}
        	}
			s.insertCustomer(bestRouteP, bestCusP, insertDelivery);
		}
        return s;
    }
}