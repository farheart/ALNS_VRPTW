package wy.alns.vo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
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
     * All orders
     */
    private Collection<Delivery> deliverySet;

    private int deliveryNum;

    /**
     * Depot where all vehicles depart from
     */
    private Depot depot;

    /**
     * The available vehicles
     */
    private List<Vehicle> vehicleList;


    /**
     * distanceDict store distance between every node to each other.
     */
    private DistanceDict distanceDict;


    public Instance(String name, String instanceType, int size)  {
    	this.name = name;
    	this.type = instanceType;

        List<String> dataLineList = this.loadDataLines(size, name);
        this.vehicleList = this.loadVehicle(dataLineList);
        this.deliverySet = this.loadDelivery(dataLineList);
        this.deliveryNum = this.deliverySet.size();
        this.depot = this.loadDepot(dataLineList);

        this.distanceDict = new DistanceDict(this);
    }


    private List<String> loadDataLines(int size, String name) {
        String dataFileName = "";
        if (this.type.equals("Solomon")) {
            dataFileName = "./instances" + "/solomon" + "/solomon_" + size + "/" + name + ".txt";
        } else if (this.type.equals("Homberger")) {
            dataFileName = "./instances" + "/homberger" + "/homberger_" + size + "/" + name + ".txt";
        }

        List<String> result = null;
        try {
            result = Files.readAllLines(Paths.get(dataFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    public ArrayList<Delivery> loadDelivery(List<String> lineList) {
        log.info(">> Loading delivery ... ");

        ArrayList<Delivery> result = new ArrayList<Delivery>();

        int tableStartLineNIndex = Integer.MAX_VALUE;
        for (int i=0; i<lineList.size(); ++i) {
            String line = lineList.get(i);
            if (line.startsWith("CUSTOMER")) {
                tableStartLineNIndex = i + 4;
            }

            if (i >= tableStartLineNIndex) {
                String cols[] = line.split("\\s+");
                if (cols.length > 0) {
                    Delivery delivery = createDelivery(cols);
                    result.add(delivery);
                }
            }
        }
        return result;
    }


    private static Delivery createDelivery(String[] cols) {
        Delivery delivery = new Delivery();
        delivery.setId(Integer.parseInt(cols[1]));

        double X = (Double.parseDouble(cols[2]));
        double Y = (Double.parseDouble(cols[3]));
        delivery.setLocation(new Location(X, Y));

        delivery.setAmount(Double.parseDouble(cols[4]));

        double s = Double.parseDouble(cols[5]);
        double e = Double.parseDouble(cols[6]);
        delivery.setTimeWindow(new TimeWindow(s, e));

        delivery.setServiceTime(Double.parseDouble(cols[7]));
        return delivery;
    }


    private Depot loadDepot(List<String> lineList) {
        log.info(">> Loading Depot ... ");
        Depot result = null;

        int tableStartLineNIndex = Integer.MAX_VALUE;
        for (int i = 0; i < lineList.size(); ++i) {
            String line = lineList.get(i);
            if (line.startsWith("CUSTOMER")) {
                tableStartLineNIndex = i + 3;
            }

            if (i == tableStartLineNIndex) {
                String cols[] = line.split("\\s+");
                if (cols.length > 0) {
                    result = createDepot(cols);;
                    break;
                }
            }
        }
        return result;
    }


    private static Depot createDepot(String[] cols) {
        Depot result = new Depot();
        result.setId(Integer.parseInt(cols[1]));

        double X = (Double.parseDouble(cols[2]));
        double Y = (Double.parseDouble(cols[3]));
        result.setLocation(new Location(X, Y));

        double s = Double.parseDouble(cols[5]);
        double e = Double.parseDouble(cols[6]);
        result.setTimeWindow(new TimeWindow(s, e));

        return result;
    }


    public List<Vehicle> loadVehicle(List<String> lineList) {
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

}
