package com.lld.im.common.enums;

import lombok.Getter;

/**
 * 一致性hash 的算法策略
 */

@Getter
public enum RouteHashMethodEnum {

    TREE(1,"com.lld.im.common.route.algorithm.consistenthash.TreeMapConsistentHash"),


    CUSTOMER(2,"com.stupm.common.route.algorithm.consistenthash.xxxx"),

    ;


    private final int code;
    private final String clazz;


    public static RouteHashMethodEnum getHandler(int ordinal) {
        for (int i = 0; i < RouteHashMethodEnum.values().length; i++) {
            if (RouteHashMethodEnum.values()[i].getCode() == ordinal) {
                return RouteHashMethodEnum.values()[i];
            }
        }
        return null;
    }

    RouteHashMethodEnum(int code, String clazz){
        this.code=code;
        this.clazz=clazz;
    }

}
