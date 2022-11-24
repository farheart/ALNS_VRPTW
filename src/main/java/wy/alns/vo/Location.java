package wy.alns.vo;


import lombok.Data;

/**
 * Location
 *
 * @author Yu Wang
 * @date  2022-11-22
 */
@Data
public class Location {
    /**
     * The X-axis coordinate in a theoretical 2-D space for the specific customer.
     */
    private double x;

    /**
     * The Y-axis coordinate in a theoretical 2-D space for the specific customer.
     */
    private double y;

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

}
