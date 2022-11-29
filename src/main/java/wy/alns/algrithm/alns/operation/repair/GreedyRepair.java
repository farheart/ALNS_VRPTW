package wy.alns.algrithm.alns.operation.repair;

import lombok.extern.slf4j.Slf4j;
import wy.alns.vo.Delivery;
import wy.alns.vo.Measure;
import wy.alns.algrithm.alns.ALNSSolution;

/**
 * GreedyRepair
 *
 * @author Yu Wang
 * @date  2022-11-20
 */
@Slf4j
public class GreedyRepair extends ALNSAbstractRepair implements IALNSRepair {

	@Override
	public ALNSSolution repair(ALNSSolution sol) {
		if (!checkSolution(sol)) {
			return sol;
		}
    	
    	int removeNr = sol.removalCustomers.size();
		for(int k = 0; k < removeNr; k++) {
			Delivery insertDelivery = sol.removalCustomers.remove(0);
			
			double bestCost;
			int bestCusP = -1;
			int bestRouteP = -1;
			bestCost = Double.POSITIVE_INFINITY;
        	
			for(int j = 0; j < sol.routes.size(); j++) {
				if(sol.routes.get(j).getServiceList().size() < 1) {
        			continue;
        		}
        		
				// 寻找最优插入位置
            	for (int i = 1; i < sol.routes.get(j).getServiceList().size() - 1; ++i) {
            		// 评价插入情况
    				Measure evalMeasure = new Measure(sol.measure);
    				sol.evaluateInsertCustomer(j, i, insertDelivery, evalMeasure);

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
			sol.insertCustomer(bestRouteP, bestCusP, insertDelivery);
		}
        return sol;
    }
}