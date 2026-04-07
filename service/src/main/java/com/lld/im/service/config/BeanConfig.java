package com.lld.im.service.config;

import cn.hutool.core.lang.ConsistentHash;
import com.lld.im.common.config.AppConfig;
import com.lld.im.common.enums.RouteHashMethodEnum;
import com.lld.im.common.enums.UrlRouteWayEnum;
import com.lld.im.common.route.RouteHandle;
import com.lld.im.common.route.algorithm.consistenthash.AbstractConsistentHash;
import com.lld.im.common.route.algorithm.consistenthash.ConsistentHashHandle;
import com.lld.im.common.route.algorithm.consistenthash.TreeMapConsistentHash;
import com.lld.im.common.route.algorithm.loop.LoopHandle;
import com.lld.im.common.route.algorithm.random.RandomHandle;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Configuration
@RequiredArgsConstructor
public class BeanConfig {

    private final AppConfig appConfig;

    @Bean
    public RouteHandle randomHandle() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Integer imRouteWay = appConfig.getImRouteWay();
        String routeWay = "";
        UrlRouteWayEnum handlerEnum = UrlRouteWayEnum.getHandler(imRouteWay);
        routeWay = handlerEnum.getClazz();

        Class<?> handleClass = Class.forName(routeWay);
        RouteHandle handle = (RouteHandle) handleClass.newInstance();

        if(handlerEnum == UrlRouteWayEnum.HASH){
            Method setConsistentHash = handleClass.getMethod("setConsistentHash", AbstractConsistentHash.class);
            Integer consistentHashWay = appConfig.getConsistentHashWay();
            String hashWay = "";
            RouteHashMethodEnum hasHandler = RouteHashMethodEnum.getHandler(consistentHashWay);
            hashWay = hasHandler.getClazz();
            AbstractConsistentHash routeHandle = (AbstractConsistentHash) Class.forName(hashWay).newInstance();
            setConsistentHash.invoke(handle, routeHandle);
        }

        return handle;
    }
}
