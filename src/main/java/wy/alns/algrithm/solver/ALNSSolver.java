package wy.alns.algrithm.solver;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.ALNSConfig;
import wy.alns.algrithm.alns.ALNS;
import wy.alns.algrithm.solution.Solution;
import wy.alns.algrithm.solution.SolutionValidator;
import wy.alns.vo.Instance;

/**
 * ALNSSolver
 *
 * @author Yu Wang
 * @date  2022-11-17
 */
@Slf4j
public class ALNSSolver {
    private Solution getInitialSolution(Instance instance) {
    	GreedySolver greedySolver = new GreedySolver(instance);
    	return greedySolver.getInitialSolution();
    }


    private Solution improveSolution(Solution s, ALNSConfig ac, Instance is) {
        ALNS alns = new ALNS(s, is, ac);
    	return alns.improveSolution();
    }


    public Solution solve(Instance instance, ALNSConfig config) {
        Solution initSol = this.getInitialSolution(instance);
        // log.info(">> Init Solution : " + initSol.toString());

        Solution sol = this.improveSolution(initSol, config, instance);
        // log.info(">> Solution : " + sol.toString());

        SolutionValidator solutionValidator = new SolutionValidator(instance);
        log.info(">> Validation : \n" + solutionValidator.Check(sol));

        log.info(">> Invalid Iters : {}", ALNS.invalidIterCount);
        log.info(">> Solve Info : {}", sol.getSolveInfo());

        return sol;
    }
}
