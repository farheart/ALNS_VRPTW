package wy.alns.operation.repair;

import wy.alns.algrithm.ALNSSolution;
import wy.alns.operation.IALNSOperation;

public interface IALNSRepair extends IALNSOperation {
    ALNSSolution repair(ALNSSolution from);
}
