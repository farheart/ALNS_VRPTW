package wy.alns.algrithm;

import java.io.IOException;
import java.util.Random;

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


/**
 * ALNS
 *
 * @author Yu Wang
 * @date  2022-11-19
 */
@Slf4j
public class ALNS {
    // 参数
    private final ALNSConfig config;
    private final IALNSDestroy[] destroy_ops = new IALNSDestroy[]{
            //new ProximityZoneDestroy(),
            //new ZoneDestroy(),
            //new NodesCountDestroy(false),
            //new SubrouteDestroy(),
            new ShawDestroy(),
            new RandomDestroy(),
            new WorstCostDestroy()
    };
    private final IALNSRepair[] repair_ops = new IALNSRepair[]{
            new RegretRepair(),
            new GreedyRepair(),
            new RandomRepair()
    };

    private final double T_end_t = 0.01;
    // 全局满意解
    private ALNSSolution s_g = null;
    // 局部满意解
    private ALNSSolution s_c = null;
    private int i = 0;
    // time
    private double T;
    private double T_s;
    // time start
    private long t_start;
    // time end
    private double T_end;
    

    public ALNS(Solution s_, Instance instance, ALNSConfig c) throws InterruptedException {
        config = c;
        s_g = new ALNSSolution(s_, instance);
        s_c = new ALNSSolution(s_g);
        
        // 初始化alns参数
        initStrategies();
    }

    public Solution improveSolution() throws Exception {
        T_s = -(config.getDelta() / Math.log(config.getBig_omega())) * s_c.measure.totalCost;
        T = T_s;
        T_end = T_end_t * T_s;
        
        // 计时开始
        t_start = System.currentTimeMillis();
        
        while (true) {
        	// Generate new solution from local best solution s_c
            ALNSSolution s_c_new = new ALNSSolution(s_c);
            int q = getQ(s_c_new);
            
            // Find the best operators
            IALNSDestroy destroyOperator = this.chooseOperatorByChance(this.destroy_ops);
            IALNSRepair repairOperator = this.chooseOperatorByChance(this.repair_ops);

            // destroy
            ALNSSolution s_destroy = destroyOperator.destroy(s_c_new, q);

            // repair
            ALNSSolution s_t = repairOperator.repair(s_destroy);


            log.info(">> Iteration : " +  i + ", Current TotalCost : " + Math.round(s_t.measure.totalCost * 100) / 100.0);
            
            // 更新局部满意解
            if (s_t.measure.totalCost < s_c.measure.totalCost) {
                s_c = s_t;
                // Upda更新全局满意解，sg全局满意解
                if (s_t.measure.totalCost < s_g.measure.totalCost) {
                    handleNewGlobalMinimum(destroyOperator, repairOperator, s_t);
                } else {
                	// 更新局部满意解
                    handleNewLocalMinimum(destroyOperator, repairOperator);
                }
            } else {
            	// 概率接受较差解
                handleWorseSolution(destroyOperator, repairOperator, s_t);
            }

            
            if (i % config.getTau() == 0 && i > 0) {
                segmentFinsihed();
            }
            
            T = config.getC() * T;
            i++;
            
            if (i > config.getOmega() && s_g.feasible()) break;
            if (i > config.getOmega() * 1.5 ) break;
        }
        
        Solution solution = s_g.toSolution();
        
        // 输出程序耗时s
        double s = Math.round((System.currentTimeMillis() - t_start) * 1000) / 1000000.;
        log.info(">> Run time = " + s + " sec");


        // Utilization of operators
        String msg = "";
        for (IALNSDestroy op : destroy_ops){
        	msg += op.getDraws() +" invokes  - [" + op.getClass().getName() +  "]\n";
        }
        
        for (IALNSRepair op : repair_ops){
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
            s_c = s_t;
        }
        destroyOperator.incPi(config.getSigma_3());
        repairOperator.incPi(config.getSigma_3());
    }

    private void handleNewLocalMinimum(IALNSDestroy destroyOperator, IALNSRepair repairOperator) {
        destroyOperator.incPi(config.getSigma_2());
        repairOperator.incPi(config.getSigma_2());
    }

    private void handleNewGlobalMinimum(IALNSDestroy destroyOperator, IALNSRepair repairOperator, ALNSSolution s_t) throws IOException {
        //log.info(String.format("[%d]: Found new global minimum: %.2f, Required Vehicles: %d, I_uns: %d", i, s_t.getCostFitness(), s_t.activeVehicles(), s_g.getUnscheduledJobs().size()));

        // Accept global best
        s_g = s_t;
        destroyOperator.incPi(config.getSigma_1());
        repairOperator.incPi(config.getSigma_1());
    }

    private double calculateProbabilityToAcceptTempSolutionAsNewCurrent(ALNSSolution s_t) {
        return Math.exp (-(s_t.measure.totalCost - s_c.measure.totalCost) / T);
    }

    private int getQ(ALNSSolution s_c2) {
        int q_l = Math.min((int) Math.ceil(0.05 * s_c2.instance.getCustomerNumber()), 10);
        int q_u = Math.min((int) Math.ceil(0.20 * s_c2.instance.getCustomerNumber()), 30);

        Random r = new Random();
        return r.nextInt(q_u - q_l + 1) + q_l;
    }


    private void segmentFinsihed() {
        // Update factor of Destroy operator
        double w_sum = 0;
        for (IALNSDestroy dstr : destroy_ops) {
            double w_old1 = dstr.getW() * (1 - config.getR_p());
            double recentFactor = dstr.getDraws() < 1 ? 0 : (double) dstr.getPi() / (double) dstr.getDraws();
            double w_old2 = config.getR_p() * recentFactor;
            double w_new = w_old1 + w_old2;
            w_sum += w_new;
            dstr.setW(w_new);
        }
        // Update weight
        for (IALNSDestroy dstr : destroy_ops) {
            dstr.setP(dstr.getW() / w_sum);
            //dstr.setDraws(0);
            //dstr.setPi(0);
        }

        // Update factor of Repair operator
        w_sum = 0;
        for (IALNSRepair rpr : repair_ops) {
            double recentFactor = rpr.getDraws() < 1 ? 0 : (double) rpr.getPi() / (double) rpr.getDraws();
            double w_new = (rpr.getW() * (1 - config.getR_p())) + config.getR_p() * recentFactor;
            w_sum += w_new;
            rpr.setW(w_new);
        }
        // Update weight
        for (IALNSRepair rpr : repair_ops) {
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


    private void initStrategies() {
        for (IALNSDestroy dstr : destroy_ops) {
        	dstr.setDraws(0);
            dstr.setPi(0);
            dstr.setW(1.);
            dstr.setP(1 / (double) destroy_ops.length);
        }
        for (IALNSRepair rpr : repair_ops) {
            rpr.setDraws(0);
        	rpr.setPi(0);
            rpr.setW(1.);
            rpr.setP(1 / (double) repair_ops.length);
        }
    }



}
