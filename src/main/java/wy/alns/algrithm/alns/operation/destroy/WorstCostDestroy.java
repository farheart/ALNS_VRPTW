package wy.alns.algrithm.alns.operation.destroy;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.ALNSResult;
import wy.alns.vo.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * WorstCostDestroy
 *
 * @author Yu Wang
 * @date  2022-11-20
 */
@Slf4j
public class WorstCostDestroy extends ALNSAbstractDestroy implements IALNSDestroy {
	@Override
	public ALNSResult destroy(ALNSResult result) {
		if (!isDestroyReady(result)) {
			return result;
		}
        
		// Calculate fitness of stops
		ArrayList<Fitness> fitnessList = new ArrayList<Fitness>();
        for(Route route : result.routes) {
			for (int i = 1; i < route.getServiceList().size() - 1; ++i) {
				Delivery stop = (Delivery) route.getServiceList().get(i);
				Fitness fitness = Fitness.calculateFitness(result.instance, stop, route);
				fitnessList.add(fitness);
			}
    	}
        Collections.sort(fitnessList);

        HashSet<Integer> idSet = new HashSet<Integer>();
        for(int i = 0; i < this.findNumToRemove(result); ++i) {
			idSet.add(fitnessList.get(i).stopId);
		}
        
        for(int j = 0; j < result.routes.size(); j++) {
        	for (int i = 0; i < result.routes.get(j).getServiceList().size(); ++i) {
        		Service stop = result.routes.get(j).getServiceList().get(i);
        		if(idSet.contains(stop.getId())) {
        			result.removeStop(result.routes.get(j), i);
        		}	
        	} 
    	}
        return result;
    }
}

class Fitness implements Comparable<Fitness>{
	public int stopId;
	public double fitness;
	
	private Fitness(int stopID, double f) {
		stopId = stopID;
		fitness = f;
	}
	
	public static Fitness calculateFitness(Instance instance, Delivery stop, Route route) {
		DistanceDict distanceDict = instance.getDistanceDict();

		Service depot = route.getServiceList().get(0);
		double v = route.getMeasure().getTimeViolation() + route.getMeasure().getLoadViolation() + stop.getAmount();
		double distFactor = distanceDict.between(stop, depot) + distanceDict.between(depot, stop);
		double fitness = v * distFactor;

		return new Fitness(stop.getId(), fitness);
	}
	
	@Override
	public int compareTo(Fitness s) {
		if (s.fitness > this.fitness) {
			return 1;
		} else if (this.fitness == s.fitness) {
			return 0;
		} else {
			return -1;
		}
	}

}
