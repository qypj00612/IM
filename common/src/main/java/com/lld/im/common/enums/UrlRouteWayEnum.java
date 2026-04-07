package com.lld.im.common.enums;

import lombok.Getter;

/**
 * 负载均衡策略
 */
@Getter
public enum UrlRouteWayEnum {

    /**
     * 随机
     */
    RANDOM(1,"com.lld.im.common.route.algorithm.random.RandomHandle"),


    /**
     * 1.轮训
     */
    LOOP(2,"com.lld.im.common.route.algorithm.loop.LoopHandle"),

    /**
     * HASH
     */
    HASH(3,"com.lld.im.common.route.algorithm.consistenthash.ConsistentHashHandle"),
    ;


    private final int code;
    private final String clazz;


    public static UrlRouteWayEnum getHandler(int ordinal) {
        for (int i = 0; i < UrlRouteWayEnum.values().length; i++) {
            if (UrlRouteWayEnum.values()[i].getCode() == ordinal) {
                return UrlRouteWayEnum.values()[i];
            }
        }
        return null;
    }

    UrlRouteWayEnum(int code, String clazz){
        this.code=code;
        this.clazz=clazz;
    }

}
