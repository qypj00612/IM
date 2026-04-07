package com.lld.im.common.route.algorithm.random;

import com.lld.im.common.enums.user.UserErrorCode;
import com.lld.im.common.exception.ApplicationException;
import com.lld.im.common.route.RouteHandle;
import com.lld.im.common.route.RouteInfo;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomHandle implements RouteHandle {
    @Override
    public RouteInfo routeServer(List<RouteInfo> routes,String key) {
        int size = routes.size();
        if (size == 0) {
            throw new ApplicationException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }
        int i = ThreadLocalRandom.current().nextInt(size);
        return routes.get(i);
    }
}
