package wy.alns.vo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * Instance
 *
 * @author Yu Wang
 * @date  2022-11-15
 */
@Getter
@Slf4j
public class Instance {
    private String name;
    private String type;


    /**
     * This list will keep all the nodes of the problem.
     * NOTE: position 0 of the list contains the depot.
     */
    private List<Node> customers;

    /**
     * The available vehicles numbers.
     */
    private List<Vehicle> vehicles;


    /**
     * distance map of every node to each other.
     */
    private Distance distance;


    public Instance(int size, String name, String instanceType) throws IOException {
    	this.name = name;
    	this.type = instanceType;

        String dataFileName = findFileName(size, name);
    	this.vehicles = this.loadVehicle(dataFileName);
        this.customers = this.loadOrder(dataFileName);

        this.distance = new Distance(this.customers);
    }
    

    public ArrayList<Node> loadOrder(String dataFileName) throws IOException {
        log.info(">> Loading customers ... ");
        ArrayList<Node> customers = new ArrayList<Node>();

        BufferedReader bReader = new BufferedReader(new FileReader(dataFileName));

        int data_in_x_lines = Integer.MAX_VALUE;

        String line;
        while ((line = bReader.readLine()) != null) {
            String datavalue[] = line.split("\\s+");

            if (datavalue.length > 0 && datavalue[0].equals("CUST")) {
                data_in_x_lines = 2;
            }
            
            if (data_in_x_lines < 1 && datavalue.length > 0) {
            	Node customer = new Node();
                customer.setId(Integer.parseInt(datavalue[1]));
                customer.setX(Double.parseDouble(datavalue[2]));
                customer.setY(Double.parseDouble(datavalue[3]));
                customer.setDemand(Double.parseDouble(datavalue[4]));
                customer.setTimeWindow(Double.parseDouble(datavalue[5]), Double.parseDouble(datavalue[6]));
                customer.setServiceTime(Double.parseDouble(datavalue[7]));
                customers.add(customer);
            }
            data_in_x_lines--;
        }
        bReader.close();
        return customers;
    }


    public List<Vehicle> loadVehicle(String dataFileName) throws IOException {
        log.info(">> Loading vehicles ... ");

        List<Vehicle> result = new ArrayList<>();
        BufferedReader bReader = new BufferedReader(new FileReader(dataFileName));

        String line;
        int row = 0;
        while ((line = bReader.readLine()) != null) {
            String datavalue[] = line.split("\\s+");

            if (row == 4) {
            	// number of vehicles
                int numVehicle = Integer.valueOf(datavalue[1]);

                // capacity of vehicle
                int vehicleCapacity = Integer.valueOf(datavalue[2]);

                for (int i=0; i<numVehicle; i++) {
                    Vehicle v = new Vehicle(String.valueOf(i));
                    v.setCapacity(vehicleCapacity);
                    result.add(v);
                }
                break;
            }
            row++;
        }
        bReader.close();
        return result;
    }


    private String findFileName(int size, String name) {
        String dataFileName = "";
        if (type.equals("Solomon")) {
            dataFileName = "./instances" + "/solomon" + "/solomon_" + size + "/" + name + ".txt";
        } else if (type.equals("Homberger")) {
            dataFileName = "./instances" + "/homberger" + "/homberger_" + size + "/" + name + ".txt";
        }
        return dataFileName;
    }


    public int getCustomerNumber() {
        return this.customers.size();
    }

}
