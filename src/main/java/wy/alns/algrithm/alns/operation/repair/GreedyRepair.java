package wy.alns.algrithm.alns.operation.repair;

import lombok.extern.slf4j.Slf4j;
import wy.alns.vo.Delivery;
import wy.alns.vo.Measure;
import wy.alns.algrithm.alns.ALNSSolution;
import wy.alns.vo.Route;

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
    	
    	int removeNum = sol.removeSet.size();
		for(int k = 0; k < removeNum; k++) {
			Delivery insertDelivery = sol.removeSet.remove(0);
			
			double bestCost = 0;
			int bestInsertPos = -1;
			Route bestRoute = null;
			bestCost = Double.POSITIVE_INFINITY;
        	
			for(int routeIndex = 0; routeIndex < sol.routes.size(); routeIndex++) {
				Route route = sol.routes.get(routeIndex);
				if(route.getServiceList().size() < 1) {
        			continue;
        		}
        		
				// 寻找最优插入位置
            	for (int i = 1; i < route.getServiceList().size() - 1; ++i) {
            		// 评价插入情况
    				Measure evalMeasure = sol.evalInsertStop(route, i, insertDelivery);

            		if(evalMeasure.totalCost > Double.MAX_VALUE) {
            			evalMeasure.totalCost = Double.MAX_VALUE;
            		}
            		
            		// if a better insertion is found, set the position to insert in the move and update the minimum cost found
            		if (evalMeasure.totalCost < bestCost) {
            			//log.info(varCost.checkFeasible());
            			bestInsertPos = i;
            			bestRoute = route;
            			bestCost = evalMeasure.totalCost;
            		}
            	}
        	}
			sol.insertStop(bestRoute, bestInsertPos, insertDelivery);
		}
        return sol;
    }
}