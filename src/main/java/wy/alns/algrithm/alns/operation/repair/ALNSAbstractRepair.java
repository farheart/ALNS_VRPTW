package wy.alns.algrithm.alns.operation.repair;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.ALNS;
import wy.alns.algrithm.alns.ALNSResult;
import wy.alns.algrithm.alns.operation.ALNSAbstractOperation;

@Slf4j
abstract class ALNSAbstractRepair extends ALNSAbstractOperation {
    protected boolean isRepairReady(ALNSResult result) {
        if (result.removeSet.size() == 0) {
            ALNS.invalidIterCount++;
            log.error(">> [ERROR] - [{}] removeSet should NOT be empty!", this.getClass().getSimpleName());
            return false;
        }
        return true;
    }

}
