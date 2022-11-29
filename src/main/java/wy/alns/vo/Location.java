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
     * The X-axis coordinate
     */
    private double x;

    /**
     * The Y-axis coordinate
     */
    private double y;

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

}
