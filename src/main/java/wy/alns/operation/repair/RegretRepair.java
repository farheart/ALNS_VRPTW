package wy.alns.operation.repair;

import java.util.ArrayList;
import java.util.Collections;

import lombok.extern.slf4j.Slf4j;
import wy.alns.vo.Measure;
import wy.alns.algrithm.ALNSSolution;
import wy.alns.vo.Node;


/**
 * RegretRepair
 *
 * @author Yu Wang
 * @date  2022-11-20
 */
@Slf4j
public class RegretRepair extends ALNSAbstractRepair implements IALNSRepair {

	@Override
	public ALNSSolution repair(ALNSSolution s) {
		if (!checkSolution(s)) {
			return s;
		}
    	
    	ArrayList<BestPos> bestPoses = new ArrayList<BestPos>();
    	
    	int removeNr = s.removalCustomers.size();
    	
		for(int k = 0; k < removeNr; k++) {
			
			Node insertNode = s.removalCustomers.remove(0);
			
			double first,second;
			int bestCusP = -1;
			int bestRouteP = -1;
			first = second = Double.POSITIVE_INFINITY;
        	
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
            		if (newMeasure.totalCost < first) {
            			//log.info(varCost.checkFeasible());
            			bestCusP = i;
            			bestRouteP = j;
            			second = first;
            			first = newMeasure.totalCost;
            		}else if(newMeasure.totalCost < second && newMeasure.totalCost != first) {
            			second = newMeasure.totalCost;
            		}
            	}
        	}
        	bestPoses.add(new BestPos(insertNode, bestCusP, bestRouteP, second - first));
		}
		Collections.sort(bestPoses);
		
		for(BestPos bp : bestPoses) {
			s.insertCustomer(bp.bestCustomerPosition, bp.bestRroutePosition, bp.insertNode);
		}

        return s;
    }
}

class BestPos implements Comparable<BestPos>{

	public int bestRroutePosition;
	public int bestCustomerPosition;
	public Node insertNode;
	public double deltaCost;
	
	public BestPos() {}
	
	public BestPos(Node insertNode, int customer, int route, double f) {
		this.insertNode = insertNode;
		this.bestRroutePosition = customer;
		this.bestCustomerPosition = route;
		this.deltaCost = f;
	}
	
	@Override
	public int compareTo(BestPos o) {
		BestPos s = (BestPos) o;
		if (s.deltaCost > this.deltaCost  ) {
			return 1;
		} else if (this.deltaCost == s.deltaCost) {
			return 0;
		} else {
			return -1;
		}
	}
}
