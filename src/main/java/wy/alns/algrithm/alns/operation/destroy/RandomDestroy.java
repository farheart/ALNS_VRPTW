package wy.alns.algrithm.alns.operation.destroy;

import lombok.extern.slf4j.Slf4j;
import wy.alns.algrithm.alns.ALNSResult;
import wy.alns.util.RandomUtil;
import wy.alns.vo.Route;

import java.util.List;
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
	public ALNSResult destroy(ALNSResult result) {
		if (!isDestroyReady(result)) {
			return result;
		}

		while(result.removeSet.size() < this.findNumToRemove(result)) {
			// Select a route to remove stop from
			Route removeRoute = findRandomRoute(result);

			// Select a stop
			int removePos = RandomUtil.getRandom().nextInt(removeRoute.getServiceList().size() - 2) + 1;

			// do remove
			result.removeStop(removeRoute, removePos);
		}

		return result;
	}


	public static Route findRandomRoute(ALNSResult sol) {
		// filter valid route index
		List<Integer> inxList = IntStream
				.range(0, sol.routes.size())   // [0, sol.routes.size-1]
				.boxed()
				.filter(i -> sol.routes.get(i).getServiceList().size() > 2)
				.collect(Collectors.toList());

		// Select a route to remove stop from
		int ii = RandomUtil.getRandom().nextInt(inxList.size());
		int rIndex = inxList.get(ii);
		Route removeRoute = sol.routes.get(rIndex);
		return removeRoute;
	}

}
