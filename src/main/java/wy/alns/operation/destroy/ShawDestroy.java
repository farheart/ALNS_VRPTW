package wy.alns.operation.destroy;

import java.util.ArrayList;
import java.util.Collections;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.ALNSSolution;
import wy.alns.vo.Distance;
import wy.alns.vo.Node;
import wy.alns.vo.Route;

/**
 * ShawDestroy
 *
 * @author Yu Wang
 * @date  2022-11-20
 */
@Slf4j
public class ShawDestroy extends ALNSAbstractDestroy implements IALNSDestroy {

	@Override
	public ALNSSolution destroy(ALNSSolution s, int removeNr) throws Exception {
		if (!checkSolution(s)) {
			return s;
		}

		Node lastRemove;
		Route lastRoute;
		int lastRemovePos;
		int lastRoutePos;
		
		ArrayList<Integer> routeList= new ArrayList<Integer>();
        for(int j = 0; j < s.routes.size(); j++)
            routeList.add(j);  
        
        Collections.shuffle(routeList);  
        
		// 选择被移除客户所在的路径
		int removenRoutePosition = routeList.remove(0);
		Route removenRoute = s.routes.get(removenRoutePosition);
		
		while(removenRoute.getNodeList().size() <= 2) {
			removenRoutePosition = routeList.remove(0);
			removenRoute = s.routes.get(removenRoutePosition);
		}
		
		ArrayList<Integer> cusList= new ArrayList<Integer>();
        for(int j = 1; j < removenRoute.getNodeList().size() - 1; j++)
        	cusList.add(j);  
        
        Collections.shuffle(cusList);  
        
		// 选择被移除客户所在的路径
		int removenCusPosition = cusList.remove(0);
		Node removenCus = removenRoute.getNodeList().get(removenCusPosition);
		
		while(removenRoute.getNodeList().size() <= 2) {
			removenCusPosition = cusList.remove(0);
			removenCus = removenRoute.getNodeList().get(removenCusPosition);
		}

		s.removeCustomer(removenRoutePosition, removenCusPosition);
		
		lastRemove = removenCus;
		lastRoute = removenRoute;
		lastRemovePos = -1;
		lastRoutePos = -1;


		Distance distance = s.instance.getDistance();
		// double[][] distance = s.instance.getDistanceMatrix();
		
		while(s.removalCustomers.size() < removeNr ) {
			
			double minRelate = Double.MAX_VALUE;
			
			for(int j = 0; j < s.routes.size(); j++) {			
	        	for (int i = 1; i < s.routes.get(j).getNodeList().size() - 1; ++i) {
	        		
	        		Node relatedNode = s.routes.get(j).getNodeList().get(i);
	        		int l = (lastRoute.getId() == s.routes.get(j).getId())? -1 : 1;
	        		
	        		double fitness = l * 2 + 
	        				3 * distance.between(lastRemove, relatedNode) +
	        				2 * Math.abs(lastRemove.getTimeWindow()[0] - relatedNode.getTimeWindow()[0]) +
	        				2 * Math.abs(lastRemove.getDemand() - relatedNode.getDemand());
	        		
	        		if(minRelate > fitness) {
	        			minRelate = fitness;
	        			lastRemove = relatedNode;
	        			lastRoute = s.routes.get(j);
	        			lastRemovePos = i;
	        			lastRoutePos = j;
	        		}
	        	}
	    	}
			s.removeCustomer(lastRoutePos, lastRemovePos);
		}

        return s;
    }
}
