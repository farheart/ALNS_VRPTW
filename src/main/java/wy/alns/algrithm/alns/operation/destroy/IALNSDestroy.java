package wy.alns.algrithm.alns.operation.destroy;

import wy.alns.algrithm.alns.ALNSResult;
import wy.alns.algrithm.alns.operation.IALNSOperation;

public interface IALNSDestroy extends IALNSOperation {
    ALNSResult destroy(ALNSResult s, int nodes);
}
