package com.lld.im.common.route.algorithm.loop;

import com.lld.im.common.enums.user.UserErrorCode;
import com.lld.im.common.exception.ApplicationException;
import com.lld.im.common.route.RouteHandle;
import com.lld.im.common.route.RouteInfo;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LoopHandle implements RouteHandle {

    private final AtomicLong index = new AtomicLong();

    @Override
    public RouteInfo routeServer(List<RouteInfo> routes,String key) {
        int size = routes.size();
        if(size==0){
            throw new ApplicationException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }

        long i = index.incrementAndGet() % size;
        if(i<0L) {
            i=0L;
        }
        return routes.get((int) i);
    }
}
