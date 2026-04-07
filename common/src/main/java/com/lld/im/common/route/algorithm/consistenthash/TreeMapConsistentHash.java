package com.lld.im.common.route.algorithm.consistenthash;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class TreeMapConsistentHash extends AbstractConsistentHash{

    private final TreeMap<Long,String> map = new TreeMap<>();

    private final int NODE_SIZE = 2;

    @Override
    protected void add(Long key, String value) {
        for(int i=0;i<NODE_SIZE;i++){
            map.put(super.hash("node"+":"+key+i),value);
        }
        map.put(key,value);
    }

    @Override
    protected String getFirstNodeValue(String key) {
        Long hash = super.hash(key);
        SortedMap<Long, String> last = map.tailMap(hash);
        if(!last.isEmpty()){
            return last.get(last.firstKey());
        }
        return map.firstEntry().getValue();
    }

    @Override
    protected void beforeProcess() {
        map.clear();
    }
}
