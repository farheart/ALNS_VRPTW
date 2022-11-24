package wy.alns.operation.repair;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.MyALNSSolution;
import wy.alns.operation.ALNSAbstractOperation;

@Slf4j
abstract class ALNSAbstractRepair extends ALNSAbstractOperation {
    protected static boolean checkSolution(MyALNSSolution s) {
        // If no customer to remove, something in last step
        if(s.removalCustomers.size() == 0) {
            log.error(">> [ERROR] removalCustomers should NOT be empty!");
            return false;
        }
        return true;
    }

}
