package wy.alns.algrithm.solver;

import wy.alns.algrithm.ALNSConfiguration;
import wy.alns.algrithm.MyALNSProcess;
import wy.alns.algrithm.Solution;
import wy.alns.algrithm.solver.GreedyVRP;
import wy.alns.vo.Instance;


public class Solver {

    public Solver() {
    }

    public Solution getInitialSolution(Instance instance) {
    	GreedyVRP greedyVRP = new GreedyVRP(instance);
    	return greedyVRP.getInitialSolution();
    }

    public Solution improveSolution(Solution s, ALNSConfiguration ac, Instance is) throws Exception {
        MyALNSProcess ALNS = new MyALNSProcess(s, is, ac);
    	return ALNS.improveSolution();
    }
}
