package wy.alns.vo;


import lombok.Data;

/**
 * AbstractService : parent class of Pickup and Delivery
 *
 * @author Yu Wang
 * @date  2022-11-22
 */
@Data
public class AbstractService {
    protected int id;

    protected Location location;

    protected double serviceTime;

    protected TimeWindow timeWindow;
}
