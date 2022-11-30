package wy.alns.algrithm.alns.operation.repair;

import wy.alns.algrithm.alns.ALNSResult;
import wy.alns.algrithm.alns.operation.IALNSOperation;

public interface IALNSRepair extends IALNSOperation {
    ALNSResult repair(ALNSResult from);
}
