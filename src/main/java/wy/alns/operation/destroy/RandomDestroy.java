package wy.alns.operation.destroy;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.ALNSSolution;
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
	public ALNSSolution destroy(ALNSSolution s, int removeNr) throws Exception {
		if (!checkSolution(s)) {
			return s;
		}

		while(s.removalCustomers.size() < removeNr ) {
			Random r = RandomUtil.getRandom();
			
    		ArrayList<Integer> routeList= new ArrayList<Integer>();
            for(int j = 0; j < s.routes.size(); j++) {
                routeList.add(j);
			}
            Collections.shuffle(routeList);  
            
			// Select a route to remove customer from
			int removenRoutePosition = routeList.remove(0);
			Route removenRoute = s.routes.get(removenRoutePosition);
			
			while(removenRoute.getNodeList().size() <= 2) {
				removenRoutePosition = routeList.remove(0);
				removenRoute = s.routes.get(removenRoutePosition);
			}
			
			// Select customer
			int removenCustomerPosition = r.nextInt(removenRoute.getNodeList().size() - 2) + 1;
			s.removeCustomer(removenRoutePosition, removenCustomerPosition);
		}

		return s;
	}



}
