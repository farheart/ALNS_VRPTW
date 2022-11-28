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
			int removenRoutePosition = routeList.remove(0);
			Route removenRoute = sol.routes.get(removenRoutePosition);
			
			while(removenRoute.getDeliveryList().size() <= 2) {
				removenRoutePosition = routeList.remove(0);
				removenRoute = sol.routes.get(removenRoutePosition);
			}
			
			// Select customer
			int removenCustomerPosition = r.nextInt(removenRoute.getDeliveryList().size() - 2) + 1;
			sol.removeCustomer(removenRoutePosition, removenCustomerPosition);
		}
		return sol;
	}

}
