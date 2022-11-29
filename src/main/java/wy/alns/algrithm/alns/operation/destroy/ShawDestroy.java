package wy.alns.algrithm.alns.operation.destroy;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.ALNSSolution;
import wy.alns.vo.Delivery;
import wy.alns.vo.DistanceDict;
import wy.alns.vo.Route;

import java.util.ArrayList;
import java.util.Collections;

/**
 * ShawDestroy
 *
 * @author Yu Wang
 * @date  2022-11-20
 */
@Slf4j
public class ShawDestroy extends ALNSAbstractDestroy implements IALNSDestroy {
	@Override
	public ALNSSolution destroy(ALNSSolution sol, int removeNr) {
		if (!checkSolution(sol)) {
			return sol;
		}

		Delivery lastRemove;
		Route lastRoute;
		int lastRemovePos;
		int lastRoutePos;
		
		ArrayList<Integer> routeList= new ArrayList<Integer>();
        for(int j = 0; j < sol.routes.size(); j++)
            routeList.add(j);  
        
        Collections.shuffle(routeList);  
        
		// 选择被移除客户所在的路径
		int removenRoutePosition = routeList.remove(0);
		Route removenRoute = sol.routes.get(removenRoutePosition);
		
		while(removenRoute.getServiceList().size() <= 2) {
			removenRoutePosition = routeList.remove(0);
			removenRoute = sol.routes.get(removenRoutePosition);
		}
		
		ArrayList<Integer> cusList= new ArrayList<Integer>();
        for(int j = 1; j < removenRoute.getServiceList().size() - 1; j++)
        	cusList.add(j);  
        
        Collections.shuffle(cusList);  
        
		// 选择被移除客户所在的路径
		int removenCusPosition = cusList.remove(0);
		Delivery removenCus = (Delivery) removenRoute.getServiceList().get(removenCusPosition);
		
		while(removenRoute.getServiceList().size() <= 2) {
			removenCusPosition = cusList.remove(0);
			removenCus = (Delivery) removenRoute.getServiceList().get(removenCusPosition);
		}
		sol.removeCustomer(removenRoutePosition, removenCusPosition);
		
		lastRemove = removenCus;
		lastRoute = removenRoute;
		lastRemovePos = -1;
		lastRoutePos = -1;

		DistanceDict distanceDict = sol.instance.getDistanceDict();
		// double[][] distanceDict = sol.instance.getDistanceMatrix();
		
		while(sol.removalCustomers.size() < removeNr ) {
			double minRelate = Double.MAX_VALUE;
			for(int j = 0; j < sol.routes.size(); j++) {
	        	for (int i = 1; i < sol.routes.get(j).getServiceList().size() - 1; ++i) {
	        		
	        		Delivery relatedDelivery = (Delivery) sol.routes.get(j).getServiceList().get(i);
	        		int l = (lastRoute.getId() == sol.routes.get(j).getId())? -1 : 1;
	        		
	        		double fitness = l * 2 + 
	        				3 * distanceDict.between(lastRemove, relatedDelivery) +
	        				2 * Math.abs(lastRemove.getTimeWindow().getStart() - relatedDelivery.getTimeWindow().getStart()) +
	        				2 * Math.abs(lastRemove.getAmount() - relatedDelivery.getAmount());
	        		
	        		if(minRelate > fitness) {
	        			minRelate = fitness;
	        			lastRemove = relatedDelivery;
	        			lastRoute = sol.routes.get(j);
	        			lastRemovePos = i;
	        			lastRoutePos = j;
	        		}
	        	}
	    	}
			sol.removeCustomer(lastRoutePos, lastRemovePos);
		}
        return sol;
    }
}
