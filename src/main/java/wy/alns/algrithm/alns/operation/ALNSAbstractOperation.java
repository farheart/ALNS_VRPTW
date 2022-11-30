package wy.alns.algrithm.alns.operation;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

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


    // number of times being selected
    @Override
    public void drawn() {
        draws++;
    }


    // increase Pi accordingly
    @Override
    public void incPi(int pi) {
        this.pi += pi;
    }

}
