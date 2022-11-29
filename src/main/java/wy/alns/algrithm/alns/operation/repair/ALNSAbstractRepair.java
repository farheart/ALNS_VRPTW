package wy.alns.algrithm.alns.operation.repair;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.ALNSSolution;
import wy.alns.algrithm.alns.operation.ALNSAbstractOperation;

@Slf4j
abstract class ALNSAbstractRepair extends ALNSAbstractOperation {
    protected static boolean checkSolution(ALNSSolution s) {
        // If no customer to remove, something wrong in last step
        if(s.removeSet.size() == 0) {
            log.error(">> [ERROR] removalCustomers should NOT be empty!");
            return false;
        }
        return true;
    }

}
