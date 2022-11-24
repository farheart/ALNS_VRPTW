package wy.alns.vo;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Distance
 *
 * @author Yu Wang
 * @date  2022-11-19
 */
public class Distance {
    private final Map<String, Double> distanceMap;


    public Distance(List<Order> orderList) {
        this.distanceMap = new HashMap<>();
        this.init(orderList);
    }


    public static double calDistance(Order n1, Order n2) {
        double x1 = n1.getX();
        double y1 = n1.getY();
        double x2 = n2.getX();
        double y2 = n2.getY();

        return Math.round(
                Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)) * 100
        ) / 100.0;
    }


    public double between(Order n1, Order n2) {
        return this.distanceMap.get(getNodePairKey(n1, n2));
    }


    private void init(List<Order> orderList) {
        for (Order n1: orderList) {
            for (Order n2: orderList) {
                this.distanceMap.put(getNodePairKey(n1, n2), calDistance(n1, n2));
            }
        }
    }


    public static String getNodePairKey(Order n1, Order n2) {
        return n1.getId() + "~" + n2.getId();
    }
}
