package wy.alns.operation.destroy;

import java.util.ArrayList;
import java.util.Collections;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.ALNSSolution;
import wy.alns.vo.DistanceDict;
import wy.alns.vo.Order;
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

		Order lastRemove;
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
		
		while(removenRoute.getOrderList().size() <= 2) {
			removenRoutePosition = routeList.remove(0);
			removenRoute = s.routes.get(removenRoutePosition);
		}
		
		ArrayList<Integer> cusList= new ArrayList<Integer>();
        for(int j = 1; j < removenRoute.getOrderList().size() - 1; j++)
        	cusList.add(j);  
        
        Collections.shuffle(cusList);  
        
		// 选择被移除客户所在的路径
		int removenCusPosition = cusList.remove(0);
		Order removenCus = removenRoute.getOrderList().get(removenCusPosition);
		
		while(removenRoute.getOrderList().size() <= 2) {
			removenCusPosition = cusList.remove(0);
			removenCus = removenRoute.getOrderList().get(removenCusPosition);
		}

		s.removeCustomer(removenRoutePosition, removenCusPosition);
		
		lastRemove = removenCus;
		lastRoute = removenRoute;
		lastRemovePos = -1;
		lastRoutePos = -1;


		DistanceDict distanceDict = s.instance.getDistanceDict();
		// double[][] distanceDict = s.instance.getDistanceMatrix();
		
		while(s.removalCustomers.size() < removeNr ) {
			
			double minRelate = Double.MAX_VALUE;
			
			for(int j = 0; j < s.routes.size(); j++) {			
	        	for (int i = 1; i < s.routes.get(j).getOrderList().size() - 1; ++i) {
	        		
	        		Order relatedOrder = s.routes.get(j).getOrderList().get(i);
	        		int l = (lastRoute.getId() == s.routes.get(j).getId())? -1 : 1;
	        		
	        		double fitness = l * 2 + 
	        				3 * distanceDict.between(lastRemove, relatedOrder) +
	        				2 * Math.abs(lastRemove.getTimeWindow()[0] - relatedOrder.getTimeWindow()[0]) +
	        				2 * Math.abs(lastRemove.getDemand() - relatedOrder.getDemand());
	        		
	        		if(minRelate > fitness) {
	        			minRelate = fitness;
	        			lastRemove = relatedOrder;
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
