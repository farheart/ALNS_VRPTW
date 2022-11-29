package wy.alns.algrithm.alns.operation.destroy;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.ALNSSolution;
import wy.alns.algrithm.alns.operation.ALNSAbstractOperation;

@Slf4j
abstract class ALNSAbstractDestroy extends ALNSAbstractOperation {
    protected static boolean isDestroyReady(ALNSSolution s) {
        // If removalCustomers not empty, something wrong in last step
        if(s.removeSet.size() != 0) {
            log.error(">> [ERROR] removalCustomers MUST be empty to start destroy!");
            return false;
        }
        return true;
    }

}
