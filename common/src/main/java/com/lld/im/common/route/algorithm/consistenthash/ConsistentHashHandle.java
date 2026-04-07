package com.lld.im.common.route.algorithm.consistenthash;

import com.lld.im.common.route.RouteHandle;
import com.lld.im.common.route.RouteInfo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class ConsistentHashHandle implements RouteHandle {

    private AbstractConsistentHash consistentHash = null;

    public void setConsistentHash(AbstractConsistentHash consistentHash) {
        this.consistentHash = consistentHash;
    }

    @Override
    public RouteInfo routeServer(List<RouteInfo> routes,String key) {
        return consistentHash.process(routes,key);
    }
}
