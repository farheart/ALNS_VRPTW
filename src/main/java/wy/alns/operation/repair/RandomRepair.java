package wy.alns.operation.repair;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import wy.alns.util.RandomUtil;
import wy.alns.vo.Measure;
import wy.alns.algrithm.MyALNSSolution;
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
	public MyALNSSolution repair(MyALNSSolution s) {
		// ���û���Ƴ��Ŀͻ�����һ������
    	if(s.removalCustomers.size() == 0) {
			log.error("removalCustomers is empty!");
			return s;
		}
    	
    	// ��ȡ�����
    	Random r = RandomUtil.getRandom();
    	int insertCusNr = s.removalCustomers.size();	
    	
    	for (int i = 0; i < insertCusNr; i++) {
    		
    		Node insertNode = s.removalCustomers.remove(0);
    		
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
    			
    			while(insertRoute.getRoute().size() < 1) {
    				insertRoutePosition = routeList.remove(0);
    				insertRoute = s.routes.get(insertRoutePosition);
    			}
    			
    			// ����������Ҷ��ٸ�λ��
    			int insertTimes = r.nextInt(insertRoute.getRoute().size() - 1) + 1;
    			
        		ArrayList<Integer> customerList= new ArrayList<Integer>();
                for(int k = 1; k < insertRoute.getRoute().size(); k++)
                	customerList.add(k);  
                
                Collections.shuffle(customerList); 
                
                // ���ѡ��һ��λ��
    			for (int k = 0; k < insertTimes; k++) {
    				
    				int insertCusPosition = customerList.remove(0);
    				
    				// ���۲������
    				Measure newMeasure = new Measure(s.measure);
    				s.evaluateInsertCustomer(insertRoutePosition, insertCusPosition, insertNode, newMeasure);
                    
    				// �������Ų���λ��
    				if (newMeasure.totalCost < bestMeasure.totalCost) {
    					bestRoutePosition = insertRoutePosition;
    					bestCusomerPosition = insertCusPosition;
    					bestMeasure = newMeasure;
    				}
    			}
    			// ִ�в������
    			s.insertCustomer(bestRoutePosition, bestCusomerPosition, insertNode);
    		}
    	}
    	
		return s;
	}
   
}
