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
			int bestInsertPos = -1;
			int bestRouteIndex = -1;
			first = second = Double.POSITIVE_INFINITY;
        	
			for(int routeIndex = 0; routeIndex < sol.routes.size(); routeIndex++) {
        		
				if(sol.routes.get(routeIndex).getServiceList().size() < 1) {
        			continue;
        		}
        		
				// 寻找最优插入位置
            	for (int i = 1; i < sol.routes.get(routeIndex).getServiceList().size() - 1; ++i) {
            		// 评价插入情况
    				Measure evalMeasure =  sol.evaluateInsertCustomer(routeIndex, i, insertDelivery);

            		if(evalMeasure.totalCost > Double.MAX_VALUE) {
            			evalMeasure.totalCost = Double.MAX_VALUE;
            		}
            		
            		// if a better insertion is found, set the position to insert in the move and update the minimum cost found
            		if (evalMeasure.totalCost < first) {
            			//log.info(varCost.checkFeasible());
            			bestInsertPos = i;
            			bestRouteIndex = routeIndex;
            			second = first;
            			first = evalMeasure.totalCost;
            		} else if(evalMeasure.totalCost < second && evalMeasure.totalCost != first) {
            			second = evalMeasure.totalCost;
            		}
            	}
        	}
        	bestPoses.add(new BestPos(insertDelivery, bestInsertPos, bestRouteIndex, second - first));
		}
		Collections.sort(bestPoses);
		
		for(BestPos bp : bestPoses) {
			sol.insertStop(bp.bestRouteIndex, bp.bestInsertPos, bp.insertDelivery);
		}
        return sol;
    }
}

class BestPos implements Comparable<BestPos>{
	public int bestRouteIndex;
	public int bestInsertPos;
	public Delivery insertDelivery;
	public double deltaCost;

	
	public BestPos(Delivery insertDelivery, int bestInsertPos, int bestRouteIndex, double f) {
		this.insertDelivery = insertDelivery;
		this.bestRouteIndex = bestRouteIndex;
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
