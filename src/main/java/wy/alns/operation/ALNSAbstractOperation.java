package wy.alns.operation;

import lombok.*;

import java.util.*;

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
