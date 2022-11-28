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


    public double between(Delivery n1, Delivery n2) {
        return this.distanceMap.get(getNodePairKey(n1, n2));
    }


    private static double calDistance(Delivery n1, Delivery n2) {
        double x1 = n1.getLocation().getX();
        double y1 = n1.getLocation().getY();
        double x2 = n2.getLocation().getX();
        double y2 = n2.getLocation().getY();
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }


    private void init() {
        List<Delivery> deliveryList = new ArrayList<>();
        deliveryList.add(this.instance.getDepot());
        deliveryList.addAll(this.instance.getDeliveryList());

        for (Delivery n1: deliveryList) {
            for (Delivery n2: deliveryList) {
                this.distanceMap.put(getNodePairKey(n1, n2), calDistance(n1, n2));
            }
        }
    }


    private static String getNodePairKey(Delivery n1, Delivery n2) {
        return n1.getId() + "~" + n2.getId();
    }
}
