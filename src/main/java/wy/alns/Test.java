package wy.alns;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import wy.alns.algrithm.Solution;
import wy.alns.algrithm.SolutionValidator;
import wy.alns.algrithm.solver.Solver;
import wy.alns.algrithm.ALNSConfiguration;
import wy.alns.vo.Instance;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;


/**
 * Test using standard Dataset
 *
 * @author Yu Wang
 * @date  2022-11-15
 */
@Slf4j
public class Test {

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


    public static void main(String args[]) throws Exception {
        String[] instanceNames = { "C101" };
        String[][] result = new String[instanceNames.length][];
        
        for (int j = 0; j < instanceNames.length; j = j + 1) {
            result[j] = solve(
                    instanceNames[j],  // Instance name
                    "Solomon",  // Type: Homberger / Solomon
                    25,  // Number of orders£¬Solomon: 25,50,100, Homberger: 200£¬400
                    ALNSConfiguration.DEFAULT  // Configuration Parameters
            );
        }
        log.info(">> ALL DONE");
    }


    private static String[] solve(
        String name,
        String instanceType,
        int size,
        ALNSConfiguration config
    ) throws Exception {
        Instance instance = new Instance(size, name, instanceType);
        SolutionValidator solutionValidator = new SolutionValidator(instance);
        Solver solver = new Solver();

        Solution is = solver.getInitialSolution(instance);
        Solution ims = solver.improveSolution(is, config, instance);
//        log.info(">> Solution : " + ims.toString());
        log.info(">> Validation : \n" + solutionValidator.Check(ims));
        
        String[] result = {String.valueOf(ims.getTotalCost()), String.valueOf(ims.testTime)};
        return result;
    }

}
