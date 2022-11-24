package wy.alns.operation;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.MyALNSSolution;

import java.util.*;


/**
 * ALNSAbstractOperation
 *
 * @author Yu Wang
 * @date  2022-11-21
 */
@Data
@Slf4j
public abstract class ALNSAbstractOperation implements IALNSOperation {
    private final Random r = new Random();

    private int pi;
    private double p;
    private int draws;
    private double w;


    // 被使用的次数
    @Override
    public void drawn() {
        draws++;
    }


    // 优化最优满意解，则增加pi值
    @Override
    public void incPi(int pi) {
        this.pi += pi;
    }

}
