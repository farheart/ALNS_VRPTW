package wy.alns.vo;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DistanceDict
 *
 * @author Yu Wang
 * @date  2022-11-19
 */
public class DistanceDict {
    private final Map<String, Double> distanceMap;

    private final Instance instance;


    public DistanceDict(Instance instance) {
        this.instance = instance;
        this.distanceMap = new HashMap<>();
        this.init();
    }


    public double between(Service n1, Service n2) {
        return this.distanceMap.get(getNodePairKey(n1, n2));
    }


    private static double calDistance(Service n1, Service n2) {
        double x1 = n1.getLocation().getX();
        double y1 = n1.getLocation().getY();
        double x2 = n2.getLocation().getX();
        double y2 = n2.getLocation().getY();
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }


    private void init() {
        List<Service> serviceList = new ArrayList<>();
        serviceList.add(this.instance.getDepot());
        serviceList.addAll(this.instance.getDeliverySet());

        for (Service n1: serviceList) {
            for (Service n2: serviceList) {
                this.distanceMap.put(getNodePairKey(n1, n2), calDistance(n1, n2));
            }
        }
    }


    private static String getNodePairKey(Service n1, Service n2) {
        return n1.getId() + "~" + n2.getId();
    }
}
