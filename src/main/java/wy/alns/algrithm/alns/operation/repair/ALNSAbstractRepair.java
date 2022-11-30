package wy.alns.algrithm.alns.operation.repair;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.ALNSResult;
import wy.alns.algrithm.alns.operation.ALNSAbstractOperation;

@Slf4j
abstract class ALNSAbstractRepair extends ALNSAbstractOperation {
    protected static boolean isRepairReady(ALNSResult result) {
        // Need to have removed stops in removeSet
        if (result.removeSet.size() == 0) {
            log.error(">> [ERROR] removeSet should NOT be empty!");
            return false;
        }
        return true;
    }

}
