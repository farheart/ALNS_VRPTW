package wy.alns.algrithm.alns.operation.destroy;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.ALNSResult;
import wy.alns.algrithm.alns.operation.ALNSAbstractOperation;

@Slf4j
abstract class ALNSAbstractDestroy extends ALNSAbstractOperation {
    protected static boolean isDestroyReady(ALNSResult result) {
        // removeSet should be empty before destroy
        if (result.removeSet.size() != 0) {
            log.error(">> [ERROR] removeSet MUST be empty to start destroy!");
            return false;
        }
        return true;
    }

}
