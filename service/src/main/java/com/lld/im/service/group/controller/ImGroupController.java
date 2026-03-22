package com.lld.im.service.group.controller;

import com.lld.im.common.ResponseVO;
import com.lld.im.service.group.dao.ImGroup;
import com.lld.im.service.group.model.req.*;
import com.lld.im.service.group.model.resp.GetGroupResp;
import com.lld.im.service.group.service.ImGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/group")
@RequiredArgsConstructor
public class ImGroupController {

    private final ImGroupService imGroupService;

    @RequestMapping("/import")
    public ResponseVO importGroup(@RequestBody @Validated ImportGroupReq importGroupReq) {
        return imGroupService.importGroup(importGroupReq);
    }

    @RequestMapping("create")
    public ResponseVO createGroup(@RequestBody @Validated CreateGroupReq req) {
        return imGroupService.createGroup(req);
    }

    @RequestMapping("update")
    public ResponseVO updateGroup(@RequestBody @Validated UpdateGroupReq req) {
        return imGroupService.updateGroup(req);
    }

    @RequestMapping("getInfo")
    public ResponseVO<GetGroupResp> getInfo(@RequestBody @Valid GetGroupInfoReq req){
        GetGroupResp resp = imGroupService.getInfo(req);
        return ResponseVO.successResponse(resp);
    }

    @RequestMapping("getJoined")
    public ResponseVO<List<ImGroup>> getJoined(@RequestBody @Valid GetJoinedGroupReq req){
        List<ImGroup> resp = imGroupService.getJoined(req);
        return ResponseVO.successResponse(resp);
    }

    @RequestMapping("destroy")
    public ResponseVO destroy(@RequestBody @Valid DestroyGroupReq req){
        return imGroupService.destroy(req);
    }

    @RequestMapping("transfer")
    public ResponseVO transfer(@RequestBody @Valid TransferGroupReq req){
        return imGroupService.transfer(req);
    }

    @RequestMapping("mute")
    public ResponseVO mute(@RequestBody @Valid MuteGroupReq req){
        return imGroupService.mute(req);
    }
}
