package wy.alns.algrithm.alns.operation.repair;

import lombok.extern.slf4j.Slf4j;
import wy.alns.vo.Delivery;
import wy.alns.vo.Measure;
import wy.alns.algrithm.alns.ALNSResult;
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
	public ALNSResult repair(ALNSResult result) {
		if (!isRepairReady(result)) {
			return result;
		}
    	
    	int stopNum = result.removeSet.size();
		for(int k = 0; k < stopNum; k++) {
			Delivery insertDelivery = result.removeSet.remove(0);
			
			double bestCost = Double.POSITIVE_INFINITY;
			int bestInsertPos = -1;
			Route bestRoute = null;
        	
			for(int routeIndex = 0; routeIndex < result.routes.size(); routeIndex++) {
				Route route = result.routes.get(routeIndex);
				if(route.getServiceList().size() == 0) {
        			continue;
        		}
        		
				// Find best position to insert
            	for (int i = 1; i < route.getServiceList().size() - 1; ++i) {
            		// Evaluate
    				Measure evalMeasure = result.evalInsertStop(route, i, insertDelivery);

//            		if(evalMeasure.totalCost > Double.MAX_VALUE) {
//            			evalMeasure.totalCost = Double.MAX_VALUE;
//            		}
            		
            		// if found a better insertion, save it
            		if (evalMeasure.totalCost < bestCost) {
            			bestInsertPos = i;
            			bestRoute = route;
            			bestCost = evalMeasure.totalCost;
            		}
            	}
        	}
			result.insertStop(bestRoute, bestInsertPos, insertDelivery);
		}
        return result;
    }
}