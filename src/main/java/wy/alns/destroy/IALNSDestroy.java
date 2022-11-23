package wy.alns.destroy;

import wy.alns.algrithm.MyALNSSolution;
import wy.alns.operation.IALNSOperation;

public interface IALNSDestroy extends IALNSOperation {

    MyALNSSolution destroy(MyALNSSolution s, int nodes) throws Exception;

}
