package wy.alns.operation.repair;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.ALNSSolution;
import wy.alns.util.RandomUtil;
import wy.alns.vo.Measure;
import wy.alns.vo.Order;
import wy.alns.vo.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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
    	
    	// ��ȡ�����
    	Random r = RandomUtil.getRandom();
    	int insertCusNr = s.removalCustomers.size();	
    	
    	for (int i = 0; i < insertCusNr; i++) {
    		
    		Order insertOrder = s.removalCustomers.remove(0);
    		
    		// ����������Ҷ�����·��
    		int randomRouteNr = r.nextInt(s.routes.size() - 1) + 1;
    		
    		// ���Ų��뷽��
    		int bestRoutePosition = -1;
    		int bestCusomerPosition = -1;
    		Measure bestMeasure = new Measure();
    		bestMeasure.totalCost = Double.MAX_VALUE;
    		
    		ArrayList<Integer> routeList= new ArrayList<Integer>();
            for(int j = 0; j < s.routes.size(); j++)
                routeList.add(j);  
            
            Collections.shuffle(routeList);  
            
    		for (int j = 0; j < randomRouteNr; j++) {
    			
    			// ���ѡ��һ��route
    			int insertRoutePosition = routeList.remove(0);
    			Route insertRoute = s.routes.get(insertRoutePosition);
    			
    			while(insertRoute.getOrderList().size() < 1) {
    				insertRoutePosition = routeList.remove(0);
    				insertRoute = s.routes.get(insertRoutePosition);
    			}
    			
    			// ����������Ҷ��ٸ�λ��
    			int insertTimes = r.nextInt(insertRoute.getOrderList().size() - 1) + 1;
    			
        		ArrayList<Integer> customerList= new ArrayList<Integer>();
                for(int k = 1; k < insertRoute.getOrderList().size(); k++)
                	customerList.add(k);  
                
                Collections.shuffle(customerList); 
                
                // ���ѡ��һ��λ��
    			for (int k = 0; k < insertTimes; k++) {
    				
    				int insertCusPosition = customerList.remove(0);
    				
    				// ���۲������
    				Measure evalMeasure = new Measure(s.measure);
    				s.evaluateInsertCustomer(insertRoutePosition, insertCusPosition, insertOrder, evalMeasure);
                    
    				// �������Ų���λ��
    				if (evalMeasure.totalCost < bestMeasure.totalCost) {
    					bestRoutePosition = insertRoutePosition;
    					bestCusomerPosition = insertCusPosition;
    					bestMeasure = evalMeasure;
    				}
    			}
    			// ִ�в������
    			s.insertCustomer(bestRoutePosition, bestCusomerPosition, insertOrder);
    		}
    	}
    	
		return s;
	}
   
}
