package wy.alns.algrithm.solver;

import wy.alns.algrithm.ALNSConfig;
import wy.alns.algrithm.ALNS;
import wy.alns.algrithm.Solution;
import wy.alns.vo.Instance;

/**
 * Solver
 *
 * @author Yu Wang
 * @date  2022-11-17
 */
public class Solver {
    public Solution getInitialSolution(Instance instance) {
    	GreedyVRP greedyVRP = new GreedyVRP(instance);
    	return greedyVRP.getInitialSolution();
    }

    public Solution improveSolution(Solution s, ALNSConfig ac, Instance is) throws Exception {
        ALNS alns = new ALNS(s, is, ac);
    	return alns.improveSolution();
    }
}
