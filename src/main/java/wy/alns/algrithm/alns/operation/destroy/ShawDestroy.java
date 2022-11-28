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
	public ALNSSolution destroy(ALNSSolution s, int removeNr) {
		if (!checkSolution(s)) {
			return s;
		}

		Delivery lastRemove;
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
		
		while(removenRoute.getDeliveryList().size() <= 2) {
			removenRoutePosition = routeList.remove(0);
			removenRoute = s.routes.get(removenRoutePosition);
		}
		
		ArrayList<Integer> cusList= new ArrayList<Integer>();
        for(int j = 1; j < removenRoute.getDeliveryList().size() - 1; j++)
        	cusList.add(j);  
        
        Collections.shuffle(cusList);  
        
		// 选择被移除客户所在的路径
		int removenCusPosition = cusList.remove(0);
		Delivery removenCus = removenRoute.getDeliveryList().get(removenCusPosition);
		
		while(removenRoute.getDeliveryList().size() <= 2) {
			removenCusPosition = cusList.remove(0);
			removenCus = removenRoute.getDeliveryList().get(removenCusPosition);
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
	        	for (int i = 1; i < s.routes.get(j).getDeliveryList().size() - 1; ++i) {
	        		
	        		Delivery relatedDelivery = s.routes.get(j).getDeliveryList().get(i);
	        		int l = (lastRoute.getId() == s.routes.get(j).getId())? -1 : 1;
	        		
	        		double fitness = l * 2 + 
	        				3 * distanceDict.between(lastRemove, relatedDelivery) +
	        				2 * Math.abs(lastRemove.getTimeWindow().getStart() - relatedDelivery.getTimeWindow().getStart()) +
	        				2 * Math.abs(lastRemove.getAmount() - relatedDelivery.getAmount());
	        		
	        		if(minRelate > fitness) {
	        			minRelate = fitness;
	        			lastRemove = relatedDelivery;
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
