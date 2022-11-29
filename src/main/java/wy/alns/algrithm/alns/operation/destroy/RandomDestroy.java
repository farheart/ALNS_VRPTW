package wy.alns.algrithm.alns.operation.destroy;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.ALNSSolution;
import wy.alns.util.RandomUtil;
import wy.alns.vo.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * RandomDestroy
 *
 * @author Yu Wang
 * @date  2022-11-20
 */
@Slf4j
public class RandomDestroy extends ALNSAbstractDestroy implements IALNSDestroy {
	@Override
	public ALNSSolution destroy(ALNSSolution sol, int removeNr) {
		if (!checkSolution(sol)) {
			return sol;
		}

		while(sol.removalCustomers.size() < removeNr) {
			Random r = RandomUtil.getRandom();
			
    		ArrayList<Integer> routeList= new ArrayList<Integer>();
            for(int j = 0; j < sol.routes.size(); j++) {
                routeList.add(j);
			}
            Collections.shuffle(routeList);  
            
			// Select a route to remove customer from
			int removeRouteIndex = routeList.remove(0);
			Route removeRoute = sol.routes.get(removeRouteIndex);
			
			while(removeRoute.getServiceList().size() <= 2) {
				removeRouteIndex = routeList.remove(0);
				removeRoute = sol.routes.get(removeRouteIndex);
			}
			
			// Select customer
			int removePos = r.nextInt(removeRoute.getServiceList().size() - 2) + 1;
			sol.removeStop(removeRoute, removePos);
		}
		return sol;
	}

}
