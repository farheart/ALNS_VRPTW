package wy.alns.algrithm.alns;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.operation.IALNSOperation;
import wy.alns.algrithm.alns.operation.destroy.IALNSDestroy;
import wy.alns.algrithm.alns.operation.destroy.RandomDestroy;
import wy.alns.algrithm.alns.operation.destroy.ShawDestroy;
import wy.alns.algrithm.alns.operation.destroy.WorstCostDestroy;
import wy.alns.algrithm.alns.operation.repair.GreedyRepair;
import wy.alns.algrithm.alns.operation.repair.IALNSRepair;
import wy.alns.algrithm.alns.operation.repair.RandomRepair;
import wy.alns.algrithm.alns.operation.repair.RegretRepair;
import wy.alns.algrithm.solution.Solution;
import wy.alns.vo.Instance;


/**
 * ALNS
 *
 * @author Yu Wang
 * @date  2022-11-20
 */
@Slf4j
public class ALNS {
    private final ALNSConfig config;

    private final IALNSDestroy[] destroyOperators = new IALNSDestroy[]{
            //new ProximityZoneDestroy(),
            //new ZoneDestroy(),
            //new NodesCountDestroy(false),
            //new SubrouteDestroy(),
            new ShawDestroy(),
            new RandomDestroy(),
            new WorstCostDestroy()
    };

    private final IALNSRepair[] repairOperators = new IALNSRepair[]{
            new RegretRepair(),
            new GreedyRepair(),
            new RandomRepair()
    };

    private final double T_end_t = 0.01;

    // Global Best Solution
    private ALNSResult globalBestSol;

    // Local Best Solution
    private ALNSResult localBestSol;

    private int iteration = 0;

    public static int invalidIterCount = 0;

    // time
    private double T;

    private double T_s;

    private long timeStart;

    private double timeEnd;
    

    public ALNS(Solution sol, Instance instance, ALNSConfig config) {
        this.config = config;
        this.globalBestSol = new ALNSResult(sol, instance);
        this.localBestSol = new ALNSResult(this.globalBestSol);

        initOperators(this.destroyOperators);
        initOperators(this.repairOperators);
    }

    public Solution improveSolution() {
        T_s = -(config.getDelta() / Math.log(config.getBig_omega())) * localBestSol.measure.totalCost;
        T = T_s;
        timeEnd = T_end_t * T_s;

        timeStart = System.currentTimeMillis();
        
        while (true) {
        	// Generate new solution from local best solution s_c
            ALNSResult newSol = new ALNSResult(localBestSol);
            
            // Find the best operators
            IALNSDestroy destroyOperator = this.chooseOperatorByChance(this.destroyOperators);
            IALNSRepair repairOperator = this.chooseOperatorByChance(this.repairOperators);

            // destroy then repair
            ALNSResult solDestroy = destroyOperator.destroy(newSol);
            ALNSResult solRepair = repairOperator.repair(solDestroy);

            log.info(">> " + iteration + " - TotalCost : " + solRepair.measure.totalCost);
            
            // Update local best solution
            if (solRepair.measure.totalCost < localBestSol.measure.totalCost) {
                localBestSol = solRepair;
                // Update global best solution
                if (solRepair.measure.totalCost < globalBestSol.measure.totalCost) {
                    handleNewGlobalMinimum(destroyOperator, repairOperator, solRepair);
                } else {
                	// Update local best solution
                    handleNewLocalMinimum(destroyOperator, repairOperator);
                }
            } else {
            	// Accept worse solution by chance
                handleWorseSolution(destroyOperator, repairOperator, solRepair);
            }

            if (iteration % config.getTau() == 0 && iteration > 0) {
                updateFactors();
            }
            
            T = config.getC() * T;
            iteration++;
            
            if (iteration > config.getOmega() && globalBestSol.isFeasible())
                break;
            if (iteration > config.getOmega() * 1.5 )
                break;
        }
        
        Solution solution = globalBestSol.toSolution();
        solution.solveTime = (System.currentTimeMillis() - timeStart) / 1000.0;  // time elapsed
        solution.setSolveInfo(this.collectInfo(solution));

        return solution;
    }

    private String collectInfo(Solution solution) {
        String result = (">> Run time = " + solution.solveTime + " sec\n\n");

        // Utilization of operators
        String msg = "";
        for (IALNSDestroy op : destroyOperators){
        	msg += op.getDraws() +" invokes  - [" + op.getClass().getName() +  "]\n";
        }

        for (IALNSRepair op : repairOperators){
            msg += op.getDraws() +" invokes  - [" + op.getClass().getName() +  "]\n";
        }
        result += (">> Statistics of operator utilization : \n" + msg);
        return result;
    }


    private void handleWorseSolution(IALNSDestroy destroyOperator, IALNSRepair repairOperator, ALNSResult s_t) {
        // Accept worse solution by calculated probability
    	double p_accept = calculateProbabilityToAcceptTempSolutionAsNewCurrent(s_t);
        if (Math.random() < p_accept) {
            this.localBestSol = s_t;
        }
        destroyOperator.incPi(config.getSigma_3());
        repairOperator.incPi(config.getSigma_3());
    }


    private void handleNewLocalMinimum(IALNSDestroy destroyOperator, IALNSRepair repairOperator) {
        destroyOperator.incPi(config.getSigma_2());
        repairOperator.incPi(config.getSigma_2());
    }


    private void handleNewGlobalMinimum(
            IALNSDestroy destroyOperator,
            IALNSRepair repairOperator,
            ALNSResult solRepair
    ) {
        //log.info(String.format("[%d]: Found new global minimum: %.2f, Required Vehicles: %d, I_uns: %d", i, solRepair.getCostFitness(), solRepair.activeVehicles(), s_g.getUnscheduledJobs().size()));

        // Accept global best
        this.globalBestSol = solRepair;
        destroyOperator.incPi(config.getSigma_1());
        repairOperator.incPi(config.getSigma_1());
    }


    private double calculateProbabilityToAcceptTempSolutionAsNewCurrent(ALNSResult s_t) {
        return Math.exp(-(s_t.measure.totalCost - localBestSol.measure.totalCost) / T);
    }


    private void updateFactors() {
        // Update factor of Destroy operator
        double w_sum = 0;
        for (IALNSDestroy dstr : destroyOperators) {
            double w_old1 = dstr.getW() * (1 - config.getR_p());
            double recentFactor = dstr.getDraws() < 1 ? 0 : (double) dstr.getPi() / (double) dstr.getDraws();
            double w_old2 = config.getR_p() * recentFactor;
            double w_new = w_old1 + w_old2;
            w_sum += w_new;
            dstr.setW(w_new);
        }

        // Update weight
        for (IALNSDestroy dstr : destroyOperators) {
            dstr.setP(dstr.getW() / w_sum);
            //dstr.setDraws(0);
            //dstr.setPi(0);
        }

        // Update factor of Repair operator
        w_sum = 0;
        for (IALNSRepair rpr : repairOperators) {
            double recentFactor = rpr.getDraws() < 1 ? 0 : (double) rpr.getPi() / (double) rpr.getDraws();
            double w_new = (rpr.getW() * (1 - config.getR_p())) + config.getR_p() * recentFactor;
            w_sum += w_new;
            rpr.setW(w_new);
        }

        // Update weight
        for (IALNSRepair rpr : repairOperators) {
            rpr.setP(rpr.getW() / w_sum);
            //rpr.setDraws(0);
            //rpr.setPi(0);
        }
    }


    private <T extends IALNSOperation> T chooseOperatorByChance(T[] ops) {
        double random = Math.random();
        double threshold = 0.;
        for (T op : ops) {
            threshold += op.getP();
            if (random <= threshold) {
                op.drawn();
                return op;
            }
        }
        ops[ops.length - 1].drawn();
        return ops[ops.length - 1];
    }


    private <T extends IALNSOperation> void initOperators(T[] ops) {
        for (T op : ops) {
        	op.setDraws(0);
            op.setPi(0);
            op.setW(1.0);
            op.setP(1.0 / ops.length);
        }
    }

}
