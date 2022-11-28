package wy.alns.algrithm.alns.operation.destroy;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.ALNSSolution;
import wy.alns.algrithm.alns.operation.ALNSAbstractOperation;

@Slf4j
abstract class ALNSAbstractDestroy extends ALNSAbstractOperation {
    protected static boolean checkSolution(ALNSSolution s) {
        // If removalCustomers not empty, something in last step
        if(s.removalCustomers.size() != 0) {
            log.error(">> [ERROR] removalCustomers MUST be empty to start destroy!");
            return false;
        }
        return true;
    }

}
