package wy.alns.algrithm.alns.operation.destroy;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.ALNSSolution;
import wy.alns.util.RandomUtil;
import wy.alns.vo.Route;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * RandomDestroy
 *
 * @author Yu Wang
 * @date  2022-11-20
 */
@Slf4j
public class RandomDestroy extends ALNSAbstractDestroy implements IALNSDestroy {
	@Override
	public ALNSSolution destroy(ALNSSolution sol, int removeNum) {
		if (!isDestroyReady(sol)) {
			return sol;
		}

		Random r = RandomUtil.getRandom();
		while(sol.removeSet.size() < removeNum) {
			// filter valid route index
			List<Integer> inxList = IntStream
					.range(0, sol.routes.size())   // [0, sol.routes.size-1]
					.boxed()
					.filter(i -> sol.routes.get(i).getServiceList().size() > 2)
					.collect(Collectors.toList());

			// Select a route to remove stop from
			int ii = r.nextInt(inxList.size());
			int rIndex = inxList.get(ii);
			Route removeRoute = sol.routes.get(rIndex);

			// Select a stop
			int removePos = r.nextInt(removeRoute.getServiceList().size() - 2) + 1;
			sol.removeStop(removeRoute, removePos);
		}

		return sol;
	}

}
