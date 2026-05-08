package com.lld.im.service.utils;


import com.lld.im.common.enums.conversation.ConversationTypeEnum;

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

    public static String genP2PConversationId(String fromId, String toId){
        if(fromId.compareTo(toId) < 0){
            return ConversationTypeEnum.P2P.getCode() + "_" + toId + "_" + fromId;
        }else{
            return ConversationTypeEnum.P2P.getCode() + "_" + fromId + "_" + toId;
        }
    }

    public static String genGroupConversationId(String groupId){
        return ConversationTypeEnum.GROUP.getCode() + "_" + groupId;
    }
}
