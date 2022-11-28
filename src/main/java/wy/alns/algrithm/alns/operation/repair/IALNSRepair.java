package wy.alns.algrithm.alns.operation.repair;

import wy.alns.algrithm.alns.ALNSSolution;
import wy.alns.algrithm.alns.operation.IALNSOperation;

public interface IALNSRepair extends IALNSOperation {
    ALNSSolution repair(ALNSSolution from);
}
