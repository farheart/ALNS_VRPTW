package wy.alns.operation.repair;

import lombok.extern.slf4j.Slf4j;
import wy.alns.vo.Cost;
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
		// ���û���Ƴ��Ŀͻ�����һ������
    	if(s.removalCustomers.size() == 0) {
			log.error("removalCustomers is empty!");
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
        		
				if(s.routes.get(j).getRoute().size() < 1) {
        			continue;
        		}
        		
				// Ѱ�����Ų���λ��
            	for (int i = 1; i < s.routes.get(j).getRoute().size() - 1; ++i) {
            		
            		// ���۲������
    				Cost newCost = new Cost(s.cost);
    				s.evaluateInsertCustomer(j, i, insertNode, newCost);

            		if(newCost.total > Double.MAX_VALUE) {
            			newCost.total = Double.MAX_VALUE;
            		}
            		
            		// if a better insertion is found, set the position to insert in the move and update the minimum cost found
            		if (newCost.total < bestCost) {
            			//log.info(varCost.checkFeasible());
            			bestCusP = i;
            			bestRouteP = j;
            			bestCost = newCost.total;	
            		}
            	}
        	}
			s.insertCustomer(bestRouteP, bestCusP, insertNode);
		}
        return s;
    }
}