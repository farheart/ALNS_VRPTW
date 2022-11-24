package wy.alns.operation.repair;

import lombok.extern.slf4j.Slf4j;
import wy.alns.vo.Measure;
import wy.alns.algrithm.MyALNSSolution;
import wy.alns.vo.Node;

/**
 * GreedyRepair
 *
 * @author Yu Wang
 * @date  2022-11-20
 */
@Slf4j
public class GreedyRepair extends ALNSAbstractRepair implements IALNSRepair {

	@Override
	public MyALNSSolution repair(MyALNSSolution s) {
		if (!checkSolution(s)) {
			return s;
		}
    	
    	int removeNr = s.removalCustomers.size();
    	
		for(int k = 0; k < removeNr; k++) {
			
			Node insertNode = s.removalCustomers.remove(0);
			
			double bestCost;
			int bestCusP = -1;
			int bestRouteP = -1;
			bestCost = Double.POSITIVE_INFINITY;
        	
			for(int j = 0; j < s.routes.size(); j++) {			
        		
				if(s.routes.get(j).getNodeList().size() < 1) {
        			continue;
        		}
        		
				// 寻找最优插入位置
            	for (int i = 1; i < s.routes.get(j).getNodeList().size() - 1; ++i) {
            		
            		// 评价插入情况
    				Measure newMeasure = new Measure(s.measure);
    				s.evaluateInsertCustomer(j, i, insertNode, newMeasure);

            		if(newMeasure.totalCost > Double.MAX_VALUE) {
            			newMeasure.totalCost = Double.MAX_VALUE;
            		}
            		
            		// if a better insertion is found, set the position to insert in the move and update the minimum cost found
            		if (newMeasure.totalCost < bestCost) {
            			//log.info(varCost.checkFeasible());
            			bestCusP = i;
            			bestRouteP = j;
            			bestCost = newMeasure.totalCost;
            		}
            	}
        	}
			s.insertCustomer(bestRouteP, bestCusP, insertNode);
		}
        return s;
    }
}