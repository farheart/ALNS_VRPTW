package wy.alns.algrithm.alns.operation.repair;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.ALNSResult;
import wy.alns.vo.Delivery;
import wy.alns.vo.Measure;
import wy.alns.vo.Route;

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
	public ALNSResult repair(ALNSResult result) {
		if (!isRepairReady(result)) {
			return result;
		}
    	
    	ArrayList<BestPos> posList = new ArrayList<BestPos>();
    	
    	int insertNum = result.removeSet.size();
    	
		for(int k = 0; k < insertNum; k++) {
			Delivery insertDelivery = result.removeSet.remove(0);
			
			double first, second;
			int bestInsertPos = -1;
			Route bestRoute = null;
			first = second = Double.POSITIVE_INFINITY;
        	
			for(int routeIndex = 0; routeIndex < result.routes.size(); routeIndex++) {
				Route route = result.routes.get(routeIndex);
				if(route.getServiceList().size() < 1) {
        			continue;
        		}
        		
				// Find best insert position
            	for (int i = 1; i < route.getServiceList().size() - 1; ++i) {
            		// evaluate
    				Measure evalMeasure =  result.evalInsertStop(route, i, insertDelivery);

//            		if(evalMeasure.totalCost > Double.MAX_VALUE) {
//            			evalMeasure.totalCost = Double.MAX_VALUE;
//            		}
            		
            		// if a better insertion is found, save it
            		if (evalMeasure.totalCost < first) {
            			//log.info(varCost.checkFeasible());
            			bestInsertPos = i;
            			bestRoute = route;
            			second = first;
            			first = evalMeasure.totalCost;
            		} else if(evalMeasure.totalCost < second && evalMeasure.totalCost != first) {
            			second = evalMeasure.totalCost;
            		}
            	}
        	}
        	posList.add(new BestPos(insertDelivery, bestInsertPos, bestRoute, second - first));
		}
		Collections.sort(posList);
		
		for(BestPos bp : posList) {
			result.insertStop(bp.bestRoute, bp.bestInsertPos, bp.insertDelivery);
		}
        return result;
    }
}

class BestPos implements Comparable<BestPos>{
	public Route bestRoute;
	public int bestInsertPos;
	public Delivery insertDelivery;
	public double deltaCost;

	
	public BestPos(Delivery insertDelivery, int bestInsertPos, Route bestRoute, double f) {
		this.insertDelivery = insertDelivery;
		this.bestRoute = bestRoute;
		this.bestInsertPos = bestInsertPos;
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
