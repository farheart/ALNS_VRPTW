package wy.alns.algrithm.alns.operation.destroy;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.ALNSResult;
import wy.alns.algrithm.alns.operation.ALNSAbstractOperation;
import wy.alns.util.RandomUtil;

@Slf4j
abstract class ALNSAbstractDestroy extends ALNSAbstractOperation {
    protected boolean isDestroyReady(ALNSResult result) {
        log.debug(">> [{}] invoked ...", this.getClass().getSimpleName());
        if (result.removeSet.size() != 0) {
            log.error(">> [ERROR] - [{}] removeSet MUST be empty to start destroy!", this.getClass().getSimpleName());
            return false;
        }
        return true;
    }


    protected int findNumToRemove(ALNSResult result) {
        int q_l = Math.min((int) Math.ceil(0.05 * result.instance.getOrderNum()), 10);
        int q_u = Math.min((int) Math.ceil(0.20 * result.instance.getOrderNum()), 30);
        return RandomUtil.getRandom().nextInt(q_u - q_l + 1) + q_l;
    }

}
