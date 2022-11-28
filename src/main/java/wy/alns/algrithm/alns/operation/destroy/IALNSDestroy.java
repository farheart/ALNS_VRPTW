package wy.alns.algrithm.alns.operation.destroy;

import wy.alns.algrithm.alns.ALNSSolution;
import wy.alns.algrithm.alns.operation.IALNSOperation;

public interface IALNSDestroy extends IALNSOperation {
    ALNSSolution destroy(ALNSSolution s, int nodes);
}
