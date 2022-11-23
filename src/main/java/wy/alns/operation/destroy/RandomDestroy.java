package wy.alns.operation.destroy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.MyALNSSolution;
import wy.alns.operation.ALNSAbstractOperation;
import wy.alns.util.RandomUtil;
import wy.alns.vo.Route;

/**
 * RandomDestroy
 *
 * @author Yu Wang
 * @date  2022-11-20
 */
@Slf4j
public class RandomDestroy extends ALNSAbstractOperation implements IALNSDestroy {
	/*
	@Override
	public ALNSStrategieVisualizationManager getVisualizationManager() {
		// TODO Auto-generated method stub
		return null;
	}
	*/
	@Override
	public MyALNSSolution destroy(MyALNSSolution s, int removeNr) throws Exception {
		
		if(s.removalCustomers.size() != 0) {
			log.error("removalCustomers is not empty.");
			return s;
		}
		
		while(s.removalCustomers.size() < removeNr ) {
			
			// 获取随机数
			Random r = RandomUtil.getRandom();
			
    		ArrayList<Integer> routeList= new ArrayList<Integer>();
            for(int j = 0; j < s.routes.size(); j++)
                routeList.add(j);  
            
            Collections.shuffle(routeList);  
            
			// 选择被移除客户所在的路径
			int removenRoutePosition = routeList.remove(0);
			Route removenRoute = s.routes.get(removenRoutePosition);
			
			while(removenRoute.getRoute().size() <= 2) {
				removenRoutePosition = routeList.remove(0);
				removenRoute = s.routes.get(removenRoutePosition);
			}
			
			// 选择被移除的客户
			int removenCustomerPosition = r.nextInt(removenRoute.getRoute().size() - 2) + 1;
			
			s.removeCustomer(removenRoutePosition, removenCustomerPosition);
		}

		return s;
	}

}
