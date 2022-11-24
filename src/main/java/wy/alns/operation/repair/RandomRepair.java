package wy.alns.operation.repair;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import wy.alns.util.RandomUtil;
import wy.alns.vo.Measure;
import wy.alns.algrithm.ALNSSolution;
import wy.alns.vo.Node;
import wy.alns.vo.Route;

/**
 * RandomRepair
 *
 * @author Yu Wang
 * @date  2022-11-20
 */
@Slf4j
public class RandomRepair extends ALNSAbstractRepair implements IALNSRepair {

	@Override
	public ALNSSolution repair(ALNSSolution s) {
		if (!checkSolution(s)) {
			return s;
		}
    	
    	// 获取随机数
    	Random r = RandomUtil.getRandom();
    	int insertCusNr = s.removalCustomers.size();	
    	
    	for (int i = 0; i < insertCusNr; i++) {
    		
    		Node insertNode = s.removalCustomers.remove(0);
    		
    		// 随机决定查找多少条路径
    		int randomRouteNr = r.nextInt(s.routes.size() - 1) + 1;
    		
    		// 最优插入方案
    		int bestRoutePosition = -1;
    		int bestCusomerPosition = -1;
    		Measure bestMeasure = new Measure();
    		bestMeasure.totalCost = Double.MAX_VALUE;
    		
    		ArrayList<Integer> routeList= new ArrayList<Integer>();
            for(int j = 0; j < s.routes.size(); j++)
                routeList.add(j);  
            
            Collections.shuffle(routeList);  
            
    		for (int j = 0; j < randomRouteNr; j++) {
    			
    			// 随机选择一条route
    			int insertRoutePosition = routeList.remove(0);
    			Route insertRoute = s.routes.get(insertRoutePosition);
    			
    			while(insertRoute.getNodeList().size() < 1) {
    				insertRoutePosition = routeList.remove(0);
    				insertRoute = s.routes.get(insertRoutePosition);
    			}
    			
    			// 随机决定查找多少个位置
    			int insertTimes = r.nextInt(insertRoute.getNodeList().size() - 1) + 1;
    			
        		ArrayList<Integer> customerList= new ArrayList<Integer>();
                for(int k = 1; k < insertRoute.getNodeList().size(); k++)
                	customerList.add(k);  
                
                Collections.shuffle(customerList); 
                
                // 随机选择一条位置
    			for (int k = 0; k < insertTimes; k++) {
    				
    				int insertCusPosition = customerList.remove(0);
    				
    				// 评价插入情况
    				Measure newMeasure = new Measure(s.measure);
    				s.evaluateInsertCustomer(insertRoutePosition, insertCusPosition, insertNode, newMeasure);
                    
    				// 更新最优插入位置
    				if (newMeasure.totalCost < bestMeasure.totalCost) {
    					bestRoutePosition = insertRoutePosition;
    					bestCusomerPosition = insertCusPosition;
    					bestMeasure = newMeasure;
    				}
    			}
    			// 执行插入操作
    			s.insertCustomer(bestRoutePosition, bestCusomerPosition, insertNode);
    		}
    	}
    	
		return s;
	}
   
}
