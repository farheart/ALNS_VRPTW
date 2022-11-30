package wy.alns.algrithm.alns.operation.repair;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.ALNSResult;
import wy.alns.util.RandomUtil;
import wy.alns.vo.Delivery;
import wy.alns.vo.Measure;
import wy.alns.vo.Route;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * RandomRepair
 *
 * @author Yu Wang
 * @date  2022-11-20
 */
@Slf4j
public class RandomRepair extends ALNSAbstractRepair implements IALNSRepair {

	@Override
	public ALNSResult repair(ALNSResult result) {
		if (!isRepairReady(result)) {
			return result;
		}

		List<Route> routeList = result.routes;

    	int insertNum = result.removeSet.size();
    	for (int i = 0; i < insertNum; i++) {
    		Delivery insertDelivery = result.removeSet.remove(0);

			// Search for the best insertion position
    		Route bestRoute = null;
    		int bestInsertPos = -1;
    		Measure bestMeasure = new Measure();
    		bestMeasure.totalCost = Double.MAX_VALUE;

			List<Integer> routeIndexList = IntStream
					.range(0, routeList.size())   // [0, sol.routes.size-1]
					.filter(n -> routeList.get(n).getServiceList().size() > 0)
					.boxed()
					.collect(Collectors.toList());
            Collections.shuffle(routeIndexList);

			// Number of routes to check for insertion
			int numRouteToCheck = RandomUtil.getRandom().nextInt(routeList.size() - 1) + 1;
    		for (int j = 0; j < numRouteToCheck; j++) {
    			// select a route randomly
    			int routeIndex = routeIndexList.remove(0);
    			Route insertRoute = routeList.get(routeIndex);

				List<Integer> stopIndexList = IntStream
						.range(1, insertRoute.getServiceList().size())   // [1, insertRoute.getServiceList().size-1]
						.boxed()
						.collect(Collectors.toList());
				Collections.shuffle(stopIndexList);

    			// number of positions to check for insertion
    			int numPosToCheck = RandomUtil.getRandom().nextInt(insertRoute.getServiceList().size() - 1) + 1;
    			for (int k = 0; k < numPosToCheck; k++) {
					// select a position randomly
    				int insertPos = stopIndexList.remove(0);
    				
    				// evaluate
    				Measure evalMeasure = result.evalInsertStop(insertRoute, insertPos, insertDelivery);

    				// update if better
    				if (evalMeasure.totalCost < bestMeasure.totalCost) {
    					bestRoute = insertRoute;
    					bestInsertPos = insertPos;
    					bestMeasure = evalMeasure;
    				}
    			}
    		}
			// do insert
			result.insertStop(bestRoute, bestInsertPos, insertDelivery);
    	}
		return result;
	}
   
}
