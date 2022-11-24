package wy.alns.operation.destroy;

import wy.alns.algrithm.ALNSSolution;
import wy.alns.operation.IALNSOperation;

public interface IALNSDestroy extends IALNSOperation {
    ALNSSolution destroy(ALNSSolution s, int nodes) throws Exception;
}
