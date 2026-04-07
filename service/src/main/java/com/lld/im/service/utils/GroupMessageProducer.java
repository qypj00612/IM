package com.lld.im.service.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lld.im.codec.pack.group.AddGroupMemberPack;
import com.lld.im.codec.pack.group.RemoveGroupMemberPack;
import com.lld.im.codec.pack.group.UpdateGroupMemberPack;
import com.lld.im.common.enums.ClientType;
import com.lld.im.common.enums.command.Command;
import com.lld.im.common.enums.command.group.GroupEventCommand;
import com.lld.im.common.model.ClientInfo;
import com.lld.im.service.group.model.dto.GroupMemberDTO;
import com.lld.im.service.group.service.ImGroupMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GroupMessageProducer {

    private final MessageProducer producer;

    private final ImGroupMemberService imGroupMemberService;

    public void send(String userId, Command command, Object data, ClientInfo clientInfo, String tag) {
        JSONObject json = (JSONObject) JSONObject.toJSON(data);
        String groupId = (String) json.get("groupId");
        List<String> ids = imGroupMemberService.getGroupMemberIds(groupId,clientInfo.getAppId());

        if(command.equals(GroupEventCommand.ADDED_MEMBER)){
            // 加入群成员，只需发给管理员和被加入人本身
            List<String> managers = imGroupMemberService.getGroupManagers(groupId,clientInfo.getAppId());
            AddGroupMemberPack addMemberPack = JSONObject.toJavaObject(json, AddGroupMemberPack.class);
            List<String> addMembers = addMemberPack.getMembers();
            for(String manager : managers){
                if(clientInfo.getClientType()!=null&&
                        clientInfo.getClientType()!= ClientType.WEB.getCode()&&
                        userId.equals(manager)){

                    producer.sendToUserExceptClient(manager,
                            command,
                            data,
                            clientInfo,
                            tag);
                }else{
                    producer.sendToUser(manager,
                            command,
                            data,
                            clientInfo.getAppId(),
                            tag);
                }
            }

            for(String addMember: addMembers){
                if(clientInfo.getClientType()!=null&&
                        clientInfo.getClientType()!= ClientType.WEB.getCode()&&
                        userId.equals(addMember)){

                    producer.sendToUserExceptClient(addMember,
                            command,
                            data,
                            clientInfo,
                            tag);
                }else{
                    producer.sendToUser(addMember,
                            command,
                            data,
                            clientInfo.getAppId(),
                            tag);
                }
            }
        } else if (command.equals(GroupEventCommand.DELETED_MEMBER)) {
            // 删除群成员 只需发给管理员和被删除人本身
            List<String> managers = imGroupMemberService.getGroupManagers(groupId,clientInfo.getAppId());
            RemoveGroupMemberPack removeGroupMemberPack = JSONObject.toJavaObject(json, RemoveGroupMemberPack.class);
            String member = removeGroupMemberPack.getMember();
            managers.add(member);

            for(String manager : managers){
                if(clientInfo.getClientType()!=null&&
                        clientInfo.getClientType()!= ClientType.WEB.getCode()&&
                        userId.equals(manager)){

                    producer.sendToUserExceptClient(manager,
                            command,
                            data,
                            clientInfo,
                            tag);
                }else{
                    producer.sendToUser(manager,
                            command,
                            data,
                            clientInfo.getAppId(),
                            tag);
                }
            }

        } else if (command.equals(GroupEventCommand.UPDATED_MEMBER)) {
            // 更新群成员资料 只需发给管理员和更新人本身
            List<String> managers = imGroupMemberService.getGroupManagers(groupId,clientInfo.getAppId());
            UpdateGroupMemberPack pack = JSONObject.toJavaObject(json, UpdateGroupMemberPack.class);
            String memberId = pack.getMemberId();
            managers.add(memberId);

            for(String manager : managers){
                if(clientInfo.getClientType()!=null&&
                        clientInfo.getClientType()!= ClientType.WEB.getCode()&&
                        userId.equals(manager)){

                    producer.sendToUserExceptClient(manager,
                            command,
                            data,
                            clientInfo,
                            tag);
                }else{
                    producer.sendToUser(manager,
                            command,
                            data,
                            clientInfo.getAppId(),
                            tag);
                }
            }
        } else{
            // 其他指令 通知所有群成员
            for(String id : ids){
                if(clientInfo.getClientType()!=null&&
                        clientInfo.getClientType()!= ClientType.WEB.getCode()&&
                        userId.equals(id)){

                    producer.sendToUserExceptClient(id,
                            command,
                            data,
                            clientInfo,
                            tag);
                }else{
                    producer.sendToUser(id,
                            command,
                            data,
                            clientInfo.getAppId(),
                            tag);
                }
            }
        }

    }

}
