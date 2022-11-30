package wy.alns.algrithm.alns.operation.destroy;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.ALNSResult;
import wy.alns.util.RandomUtil;
import wy.alns.vo.Delivery;
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
	public ALNSResult destroy(ALNSResult sol, int removeNum) {
		if (!isDestroyReady(sol)) {
			return sol;
		}

		// Select a route to remove stop from
		Route removeRoute = RandomDestroy.findRandomRoute(sol);

		// Select a stop
		int removePos = RandomUtil.getRandom().nextInt(removeRoute.getServiceList().size() - 2) + 1;
		Delivery removeStop = (Delivery) removeRoute.getServiceList().get(removePos);

		// do remove
		sol.removeStop(removeRoute, removePos);

		// Find related
		Delivery lastRemoveStop = removeStop;
		Route lastRoute = removeRoute;
		int lastRemovePos = -1;

		while(sol.removeSet.size() < removeNum) {
			double minRelate = Double.MAX_VALUE;
			for(int j = 0; j < sol.routes.size(); j++) {
	        	for (int i = 1; i < sol.routes.get(j).getServiceList().size() - 1; ++i) {
	        		
	        		Delivery relatedDelivery = (Delivery) sol.routes.get(j).getServiceList().get(i);
	        		int l = (lastRoute.getId() == sol.routes.get(j).getId())? -1 : 1;
	        		
	        		double fitness = l * 2 + 
	        				3 * sol.instance.getDistanceDict().between(lastRemoveStop, relatedDelivery) +
	        				2 * Math.abs(lastRemoveStop.getTimeWindow().getStart() - relatedDelivery.getTimeWindow().getStart()) +
	        				2 * Math.abs(lastRemoveStop.getAmount() - relatedDelivery.getAmount());
	        		
	        		if(minRelate > fitness) {
	        			minRelate = fitness;
	        			lastRemoveStop = relatedDelivery;
	        			lastRoute = sol.routes.get(j);
	        			lastRemovePos = i;
	        		}
	        	}
	    	}
			sol.removeStop(lastRoute, lastRemovePos);
		}
        return sol;
    }
}
