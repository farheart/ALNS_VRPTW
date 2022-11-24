package wy.alns.algrithm;

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

    private final int omega; // iteration numbers
    private final int tau; //更新算子选择概率的间隔迭代次数
    private final double r_p; //计算概率
    private final int sigma_1; //发现全局最优，add
    private final int sigma_2;//发现局部最优，add
    private final int sigma_3;//发现较差，add
    private final double c;
    private final double delta;
    private final double big_omega;


    ALNSConfig(int omega, int tau, double r_p, int sigma_1, int sigma_2, int sigma_3, double c, double delta, double big_omega) {
        this.omega = omega;
        this.tau = tau;
        this.r_p = r_p;
        this.sigma_1 = sigma_1;
        this.sigma_2 = sigma_2;
        this.sigma_3 = sigma_3;
        this.c = c;
        this.delta = delta;
        this.big_omega = big_omega;
    }

}
