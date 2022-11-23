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
 * Test ALNS by stardard Dataset
 *
 * @author Yu Wang
 * @date  2022-11-15
 */
@Slf4j
public class Test {

    private static final String[] SOLOMON_ALL = new String[]{
            "C101", "C102", "C103", "C104", "C105", "C106", "C107", "C108", "C109", "C201", "C202", "C203", "C204", "C205", "C206", "C207", "C208",
            "R101", "R102", "R103", "R104", "R105", "R106", "R107", "R108", "R109", "R110", "R111", "R112", "R201", "R202", "R203", "R204", "R205", "R206", "R207", "R208", "R209", "R210", "R211",
            "RC101", "RC102", "RC103", "RC104", "RC105", "RC106", "RC107", "RC108", "RC201", "RC202", "RC203", "RC204", "RC205", "RC206", "RC207", "RC208"
    };
    static String[] SOLOMON_CLUSTERED = new String[]{"C101", "C102", "C103", "C104", "C105", "C106", "C107", "C108", "C109", "C201", "C202", "C203", "C204", "C205", "C206", "C207", "C208"};
    static String[] SOLOMON_RANDOM = new String[]{"R101", "R102", "R103", "R104", "R105", "R106", "R107", "R108", "R109", "R110", "R111", "R112", "R201", "R202", "R203", "R204", "R205", "R206", "R207", "R208", "R209", "R210", "R211",};
    static String[] SOLOMON_CLUSTERRANDOM = new String[]{"RC101", "RC102", "RC103", "RC104", "RC105", "RC106", "RC107", "RC108", "RC201", "RC202", "RC203", "RC204", "RC205", "RC206", "RC207", "RC208"};
    static String[] VRPFD_INSTANCES = new String[]{"C108", "C206", "C203", "R202", "R207", "R104", "RC202", "RC205", "RC208"};
    static String[] Homberger_200 = new String[] {"C1_2_1", "C1_2_2", "C1_2_3", "C1_2_4"};
    static String[] Homberger_400 = new String[] {"C1_4_1", "C1_4_2", "C1_4_3", "C1_4_4"};


    public static void main(String args[]) {
    	
        String[] instanceNames = { "C101" };
        String[][] result = new String[instanceNames.length][];
        
        for (int j = 0; j < instanceNames.length; j = j + 1) {
            try {
                result[j] = solve(
                        instanceNames[j],  // Instance name
                        "Solomon",   // Type: Homberger / Solomon
                        25,  // Number of orders，Solomon: 25,50,100, Homberger: 200，400
                        ALNSConfiguration.DEFAULT  // Configuration Parameters
                );
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        log.info(">> ALL DONE");
    }


    private static String[] solve(String name, String instanceType, int size, ALNSConfiguration c) throws Exception {
        Instance instance = new Instance(size, name, instanceType);
        SolutionValidator solutionValidator = new SolutionValidator(instance);
        Solver solver = new Solver();
        // 初始解
        Solution is = solver.getInitialSolution(instance);
        //System.out.println(is);
        //System.out.println(solutionValidator.Check(is));
        // 满意解
        Solution ims = solver.improveSolution(is, c, instance);
        System.out.println(ims);
        System.out.println(solutionValidator.Check(ims));
        
        String[] result = {String.valueOf(ims.getTotalCost()), String.valueOf(ims.testTime)};
        return result;
    }
    
	public static void printToCSV(String FILE_NAME, String[][] result, int size) {
		final String[] FILE_HEADER={"InstanceName", "BestCost", "TimeCost"};
		
		FileWriter fileWriter=null;
		CSVPrinter csvPrinter=null;
		CSVFormat csvFormat=CSVFormat.DEFAULT.withHeader(FILE_HEADER);
		
		try {
			fileWriter=new FileWriter(FILE_NAME + ".csv");
			csvPrinter=new CSVPrinter(fileWriter, csvFormat);
			
			for(int i = 0; i < size; i++){
				List<String> record=new ArrayList<>();
				record.add(Homberger_400[i]);
				record.add(result[i][0]);
				record.add(result[i][1]);
				csvPrinter.printRecord(record);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try{
				fileWriter.flush();
				fileWriter.close();
				csvPrinter.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
