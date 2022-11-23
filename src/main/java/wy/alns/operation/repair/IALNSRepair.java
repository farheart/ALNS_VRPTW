package wy.alns.operation.repair;

import wy.alns.algrithm.MyALNSSolution;
import wy.alns.operation.IALNSOperation;

public interface IALNSRepair extends IALNSOperation {
    MyALNSSolution repair(MyALNSSolution from);
}
