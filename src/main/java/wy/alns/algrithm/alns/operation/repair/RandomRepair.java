package wy.alns.algrithm.alns.operation.repair;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.ALNSSolution;
import wy.alns.util.RandomUtil;
import wy.alns.vo.Delivery;
import wy.alns.vo.Measure;
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
	public ALNSSolution repair(ALNSSolution sol) {
		if (!checkSolution(sol)) {
			return sol;
		}
    	
    	// ��ȡ�����
    	Random r = RandomUtil.getRandom();
    	int insertNum = sol.removeSet.size();
    	
    	for (int i = 0; i < insertNum; i++) {
    		Delivery insertDelivery = sol.removeSet.remove(0);
    		
    		// ����������Ҷ�����·��
    		int randomRouteNr = r.nextInt(sol.routes.size() - 1) + 1;
    		
    		// ���Ų��뷽��
    		Route bestRoute = null;
    		int bestInsertPos = -1;
    		Measure bestMeasure = new Measure();
    		bestMeasure.totalCost = Double.MAX_VALUE;
    		
    		ArrayList<Integer> routeList= new ArrayList<Integer>();
            for(int j = 0; j < sol.routes.size(); j++)
                routeList.add(j);  
            
            Collections.shuffle(routeList);  
            
    		for (int j = 0; j < randomRouteNr; j++) {
    			// ���ѡ��һ��route
    			int routeIndex = routeList.remove(0);
    			Route insertRoute = sol.routes.get(routeIndex);
    			
    			while(insertRoute.getServiceList().size() < 1) {
    				routeIndex = routeList.remove(0);
    				insertRoute = sol.routes.get(routeIndex);
    			}
    			
    			// ����������Ҷ��ٸ�λ��
    			int insertTimes = r.nextInt(insertRoute.getServiceList().size() - 1) + 1;
    			
        		ArrayList<Integer> customerList= new ArrayList<Integer>();
                for(int k = 1; k < insertRoute.getServiceList().size(); k++)
                	customerList.add(k);  
                
                Collections.shuffle(customerList); 
                
                // ���ѡ��һ��λ��
    			for (int k = 0; k < insertTimes; k++) {
    				int insertPos = customerList.remove(0);
    				
    				// ���۲������
    				Measure evalMeasure = sol.evalInsertStop(insertRoute, insertPos, insertDelivery);

    				// �������Ų���λ��
    				if (evalMeasure.totalCost < bestMeasure.totalCost) {
    					bestRoute = insertRoute;
    					bestInsertPos = insertPos;
    					bestMeasure = evalMeasure;
    				}
    			}
    			// ִ�в������
    			sol.insertStop(bestRoute, bestInsertPos, insertDelivery);
    		}
    	}
		return sol;
	}
   
}
