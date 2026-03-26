package com.lld.im.common.constant;

import com.lld.im.common.model.UserSession;

public class Constants {

    public static String UserId = "userId";
    public static String AppId = "appId";
    public static String ClientType = "clientType";
    public static String imei = "imei";
    public static String ReadTime = "readTime";


    public static class RedisConstants{
        /**
         *  用户session：appId + UserSessionConstant + userId
         */
        public static final String UserSessionConstant=":userSession:";

    }

    public static class NacosConstants{
        public static final String NacosTcpServer = "tcp";
        public static final String NacosWebServer = "web";

    }

    public static class RocketConstants{

        public static final String IM_TO_SERVICE = "im-to-service";

        public static final String Im2UserService = "pipeline2UserService";
        public static final String Im2MessageService = "pipeline2MessageService";
        public static final String Im2GroupService = "pipeline2GroupService";
        public static final String Im2FriendshipService = "pipeline2FriendshipService";


        public static final String SERVICE_TO_IM = "service-to-im";

        public static final String MessageService2Im = "messageService2Pipeline";
        public static final String GroupService2Im = "GroupService2Pipeline";
        public static final String FriendShip2Im = "friendShip2Pipeline";


        public static final String MESSAGE_STORE = "message-store";

        public static final String StoreP2PMessage = "storeP2PMessage";
        public static final String StoreGroupMessage = "storeGroupMessage";


        public static final String IM_BROADCAST = "im-broadcast";

        public static final String USER_LOGIN = "userLogin";
    }

}
