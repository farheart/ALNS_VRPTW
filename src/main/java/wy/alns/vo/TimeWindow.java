package wy.alns.vo;


import lombok.Data;

/**
 * RandomDestroy
 *
 * @author Yu Wang
 * @date  2022-11-20
 */
@Data
public class TimeWindow {
    private double start;
    private double end;

    public TimeWindow(double s, double e) {
        this.start = s;
        this.end = e;
    }
}
