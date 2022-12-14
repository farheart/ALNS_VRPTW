package wy.alns.algrithm.alns;

import lombok.Getter;


/**
 * ALNSConfig : parameters
 *
 * @author Yu Wang
 * @date  2022-11-20
 */
@Getter
public enum ALNSConfig {
    DEFAULT(10000, 500, 0.1, 20, 5, 1, 0.99937, 0.05, 0.5);

    private final int maxIterNum;  // max iteration numbers -- omega
    private final int itersBetweenUpdate;  // iterations between updating the probability of op selection -- tau
    private final double r_p; // probability used in calculate weight factor -- r_p
    private final int delta_GlobalBest;  // delta to add to Pi for Global Best -- sigma_1
    private final int delta_LocalBest;  // delta to add to Pi for Local Best -- sigma_2
    private final int delta_Worse;  // delta to add to Pi for Worse -- sigma_3
    private final double c;
    private final double delta;
    private final double big_omega;


    ALNSConfig(int maxIterNum, int itersBetweenUpdate, double r_p, int delta_GlobalBest, int delta_LocalBest, int delta_Worse, double c, double delta, double big_omega) {
        this.maxIterNum = maxIterNum;
        this.itersBetweenUpdate = itersBetweenUpdate;
        this.r_p = r_p;
        this.delta_GlobalBest = delta_GlobalBest;
        this.delta_LocalBest = delta_LocalBest;
        this.delta_Worse = delta_Worse;
        this.c = c;
        this.delta = delta;
        this.big_omega = big_omega;
    }

}
