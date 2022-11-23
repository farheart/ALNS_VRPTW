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
    private int numVehicle;
    
    /**
     * The capacity of vehicles.
     */
    private int vehicleCapacity;

    /**
     * distance map of every node to each other.
     */
    private Distance distance;




    public Instance(int size, String name, String instanceType) throws IOException {
    	this.name = name;
    	this.type = instanceType;



    	this.loadVehicle(size, name);
        this.loadOrder(size, name);

        this.distance = new Distance(this.customers);
    }
    

    public void loadOrder(int size, String name) throws IOException {
        this.customers = new ArrayList<Node>();

        String dataFileName = findFileName(size, name);

        BufferedReader bReader = new BufferedReader(new FileReader(dataFileName));

        int data_in_x_lines = Integer.MAX_VALUE;

        log.info(">> Loading customers ... ");

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
                this.customers.add(customer);
            }
            data_in_x_lines--;
        }
        bReader.close();
    }


    public void loadVehicle(int size, String name) throws IOException {
        String dataFileName = findFileName(size, name);

        BufferedReader bReader = new BufferedReader(new FileReader(dataFileName));

        log.info(">> Loading vehicles ... ");

        String line;
        int row = 0;
        while ((line = bReader.readLine()) != null) {
            String datavalue[] = line.split("\\s+");

            if (row == 4) {
            	//可用车辆数量
                this.numVehicle = Integer.valueOf(datavalue[1]);
                //车辆容量
                this.vehicleCapacity = Integer.valueOf(datavalue[2]);
                break;
            }
            row++;
        }
        bReader.close();
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
