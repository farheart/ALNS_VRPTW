package wy.alns;


import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.ALNSConfig;
import wy.alns.algrithm.solution.Solution;
import wy.alns.algrithm.solver.ALNSSolver;
import wy.alns.vo.Instance;


/**
 * Test using standard Dataset
 *
 * @author Yu Wang
 * @date  2022-11-15
 */
@Slf4j
public class Test {
    public static void main(String args[]) {
        test_1();
        log.info(">> ALL DONE");
    }

    private static void test_1() {
        String instanceName = "C101";
        String instanceType = "Solomon";  // Type: Homberger / Solomon
        int size = 25;  // Number of orders£¬Solomon: 25,50,100, Homberger: 200£¬400

        Instance instance = new Instance(instanceName, instanceType, size);
        Solution sol = new ALNSSolver().solve(instance, ALNSConfig.DEFAULT);
        String[] msg = {String.valueOf(sol.getTotalCost()), String.valueOf(sol.solveTime)};
    }


    private static final String[] SOLOMON_ALL = new String[]{
            // SOLOMON_CLUSTERED
            "C101", "C102", "C103", "C104", "C105", "C106", "C107", "C108", "C109",
            "C201", "C202", "C203", "C204", "C205", "C206", "C207", "C208",
            // SOLOMON_RANDOM
            "R101", "R102", "R103", "R104", "R105", "R106", "R107", "R108", "R109",
            "R110", "R111", "R112", "R201", "R202", "R203", "R204", "R205", "R206",
            "R207", "R208", "R209", "R210", "R211",
            // SOLOMON_CLUSTERRANDOM
            "RC101", "RC102", "RC103", "RC104", "RC105", "RC106", "RC107", "RC108",
            "RC201", "RC202", "RC203", "RC204", "RC205", "RC206", "RC207", "RC208"
    };

}
