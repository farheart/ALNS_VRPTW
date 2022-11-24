package wy.alns.vo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


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
     * All orders, where index = 0 : Depot
     */
    private List<Order> orderList;

    /**
     * The available vehicles
     */
    private List<Vehicle> vehicleList;


    /**
     * distance map of every node to each other.
     */
    private Distance distance;


    public Instance(int size, String name, String instanceType) throws IOException {
    	this.name = name;
    	this.type = instanceType;

        List<String> dataLineList = this.loadData(size, name);
        this.vehicleList = this.loadVehicle(dataLineList);
        this.orderList = this.loadOrder(dataLineList);

        this.distance = new Distance(this.orderList);
    }


    private List<String> loadData(int size, String name) throws IOException {
        String dataFileName = "";
        if (type.equals("Solomon")) {
            dataFileName = "./instances" + "/solomon" + "/solomon_" + size + "/" + name + ".txt";
        } else if (type.equals("Homberger")) {
            dataFileName = "./instances" + "/homberger" + "/homberger_" + size + "/" + name + ".txt";
        }
        return Files.readAllLines(Paths.get(dataFileName));
    }


    public ArrayList<Order> loadOrder(List<String> lineList) {
        log.info(">> Loading customers ... ");

        ArrayList<Order> result = new ArrayList<Order>();

        int tableStartLineNIndex = Integer.MAX_VALUE;
        for (int i=0; i<lineList.size(); ++i) {
            String line = lineList.get(i);
            if (line.startsWith("CUSTOMER")) {
                tableStartLineNIndex = i + 3;
            }

            if (i >= tableStartLineNIndex) {
                String cols[] = line.split("\\s+");
                if (cols.length > 0) {
                    Order order = new Order();
                    order.setId(Integer.parseInt(cols[1]));
                    order.setX(Double.parseDouble(cols[2]));
                    order.setY(Double.parseDouble(cols[3]));
                    order.setDemand(Double.parseDouble(cols[4]));
                    order.setTimeWindow(Double.parseDouble(cols[5]), Double.parseDouble(cols[6]));
                    order.setServiceTime(Double.parseDouble(cols[7]));
                    result.add(order);
                }
            }
        }
        return result;
    }


    public List<Vehicle> loadVehicle(List<String> lineList) throws IOException {
        log.info(">> Loading vehicles ... ");

        ArrayList<Vehicle> result = new ArrayList<>();

        int tableStartLineNIndex = Integer.MAX_VALUE;
        for (int i=0; i<lineList.size(); ++i) {
            String line = lineList.get(i);
            if (line.startsWith("VEHICLE")) {
                tableStartLineNIndex = i + 2;
            }

            if (i == tableStartLineNIndex) {
                String cols[] = line.split("\\s+");
                if (cols.length == 3) {
                    int numVehicle = Integer.valueOf(cols[1]);
                    int vehicleCapacity = Integer.valueOf(cols[2]);
                    for (int vi = 0; vi < numVehicle; vi++) {
                        Vehicle v = new Vehicle(String.valueOf(vi));
                        v.setCapacity(vehicleCapacity);
                        result.add(v);
                    }
                    break;
                }
            }
        }
        return result;
    }


    public int getOrderNum() {
        return this.orderList.size();
    }

}
