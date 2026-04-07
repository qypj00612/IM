package com.lld.im.common.route;


import java.util.List;

public interface RouteHandle {
    RouteInfo routeServer(List<RouteInfo> routes, String key);
}
