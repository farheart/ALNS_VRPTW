package wy.alns.operation;

import lombok.*;

import java.util.*;


/**
 * ALNSAbstractOperation
 *
 * @author Yu Wang
 * @date  2022-11-21
 */
@Data
public abstract class ALNSAbstractOperation implements IALNSOperation {
    private final Random r = new Random();
    private int pi;
    private double p;
    private int draws;
    private double w;

    @Override
    // ��ʹ�õĴ���
    public void drawn() {
        draws++;
    }

    @Override
    // �Ż���������⣬������piֵ
    public void addToPi(int pi) {
        this.pi += pi;
    }

}
