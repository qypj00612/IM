package com.lld.im.service.utils;


public class ConversationIdGenerate {

    //A|B
    //B A
    public static String generateSeqP2PId(String fromId, String toId){
        int i = fromId.compareTo(toId);
        if(i < 0){
            return toId+"|"+fromId;
        }else if(i > 0){
            return fromId+"|"+toId;
        }

        throw new RuntimeException("");
    }

    public static String genConversationId(Integer type, String fromId, String toId){
        return type + "_" + fromId + "_" + toId;
    }
}
