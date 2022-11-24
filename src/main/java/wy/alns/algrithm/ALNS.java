package wy.alns.algrithm;

import lombok.extern.slf4j.Slf4j;
import wy.alns.operation.IALNSOperation;
import wy.alns.operation.destroy.IALNSDestroy;
import wy.alns.operation.destroy.RandomDestroy;
import wy.alns.operation.destroy.ShawDestroy;
import wy.alns.operation.destroy.WorstCostDestroy;
import wy.alns.operation.repair.GreedyRepair;
import wy.alns.operation.repair.IALNSRepair;
import wy.alns.operation.repair.RandomRepair;
import wy.alns.operation.repair.RegretRepair;
import wy.alns.vo.Instance;

import java.io.IOException;
import java.util.Random;


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
    private ALNSSolution globalBestSol = null;
    // Local Best Solution
    private ALNSSolution localBestSol = null;
    private int i = 0;
    // time
    private double T;
    private double T_s;
    // time start
    private long timeStart;
    // time end
    private double T_end;
    

    public ALNS(Solution s_, Instance instance, ALNSConfig c) throws InterruptedException {
        config = c;
        globalBestSol = new ALNSSolution(s_, instance);
        localBestSol = new ALNSSolution(globalBestSol);

        initOperators(this.destroyOperators);
        initOperators(this.repairOperators);
    }

    public Solution improveSolution() throws Exception {
        T_s = -(config.getDelta() / Math.log(config.getBig_omega())) * localBestSol.measure.totalCost;
        T = T_s;
        T_end = T_end_t * T_s;

        timeStart = System.currentTimeMillis();
        
        while (true) {
        	// Generate new solution from local best solution s_c
            ALNSSolution s_c_new = new ALNSSolution(localBestSol);
            int q = getQ(s_c_new);
            
            // Find the best operators
            IALNSDestroy destroyOperator = this.chooseOperatorByChance(this.destroyOperators);
            IALNSRepair repairOperator = this.chooseOperatorByChance(this.repairOperators);

            // destroy then repair
            ALNSSolution solDestroy = destroyOperator.destroy(s_c_new, q);
            ALNSSolution solRepair = repairOperator.repair(solDestroy);

            log.info(">> Iteration : " +  i + ", Current TotalCost : " + Math.round(solRepair.measure.totalCost * 100) / 100.0);
            
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

            
            if (i % config.getTau() == 0 && i > 0) {
                segmentFinsihed();
            }
            
            T = config.getC() * T;
            i++;
            
            if (i > config.getOmega() && globalBestSol.isFeasible()) break;
            if (i > config.getOmega() * 1.5 ) break;
        }
        
        Solution solution = globalBestSol.toSolution();

        // time elapsed
        double s = Math.round((System.currentTimeMillis() - timeStart) * 1000) / 1000000.;
        log.info(">> Run time = " + s + " sec");

        // Utilization of operators
        String msg = "";
        for (IALNSDestroy op : destroyOperators){
        	msg += op.getDraws() +" invokes  - [" + op.getClass().getName() +  "]\n";
        }
        
        for (IALNSRepair op : repairOperators){
            msg += op.getDraws() +" invokes  - [" + op.getClass().getName() +  "]\n";
        }
        log.info(">> Statistics of operator utilization : \n" + msg);

        solution.testTime = s;
        return solution;
    }

    private void handleWorseSolution(IALNSDestroy destroyOperator, IALNSRepair repairOperator, ALNSSolution s_t) {
        // Accept worse solution by calculated probability
    	double p_accept = calculateProbabilityToAcceptTempSolutionAsNewCurrent(s_t);
        if (Math.random() < p_accept) {
            localBestSol = s_t;
        }
        destroyOperator.incPi(config.getSigma_3());
        repairOperator.incPi(config.getSigma_3());
    }


    private void handleNewLocalMinimum(IALNSDestroy destroyOperator, IALNSRepair repairOperator) {
        destroyOperator.incPi(config.getSigma_2());
        repairOperator.incPi(config.getSigma_2());
    }


    private void handleNewGlobalMinimum(IALNSDestroy destroyOperator, IALNSRepair repairOperator, ALNSSolution solRepair) throws IOException {
        //log.info(String.format("[%d]: Found new global minimum: %.2f, Required Vehicles: %d, I_uns: %d", i, solRepair.getCostFitness(), solRepair.activeVehicles(), s_g.getUnscheduledJobs().size()));

        // Accept global best
        this.globalBestSol = solRepair;
        destroyOperator.incPi(config.getSigma_1());
        repairOperator.incPi(config.getSigma_1());
    }


    private double calculateProbabilityToAcceptTempSolutionAsNewCurrent(ALNSSolution s_t) {
        return Math.exp(-(s_t.measure.totalCost - localBestSol.measure.totalCost) / T);
    }


    private int getQ(ALNSSolution s_c2) {
        int q_l = Math.min((int) Math.ceil(0.05 * s_c2.instance.getOrderNum()), 10);
        int q_u = Math.min((int) Math.ceil(0.20 * s_c2.instance.getOrderNum()), 30);

        Random r = new Random();
        return r.nextInt(q_u - q_l + 1) + q_l;
    }


    private void segmentFinsihed() {
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
            op.setW(1.);
            op.setP(1 / (double) ops.length);
        }
    }

}
