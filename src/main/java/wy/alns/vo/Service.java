package wy.alns.vo;


import lombok.Data;

/**
 * Service : parent class of Depot and Delivery
 *
 * @author Yu Wang
 * @date  2022-11-22
 */
@Data
public class Service {
    protected int id;

    protected Location location;

    protected TimeWindow timeWindow;

    public Service() {
    }
}
