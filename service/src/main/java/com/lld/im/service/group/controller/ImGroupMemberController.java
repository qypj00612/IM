package com.lld.im.service.group.controller;

import com.lld.im.common.ResponseVO;
import com.lld.im.service.group.model.req.*;
import com.lld.im.service.group.model.resp.AddGroupMemberResp;
import com.lld.im.service.group.service.ImGroupMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/group/member")
@RequiredArgsConstructor
public class ImGroupMemberController {

    private final ImGroupMemberService imGroupMemberService;

    @RequestMapping("/import")
    public ResponseVO<List<AddGroupMemberResp>> importGroupMember(@RequestBody @Validated ImportGroupMemberReq req) {
        List<AddGroupMemberResp> resp = imGroupMemberService.importGroupMember(req);
        return ResponseVO.successResponse(resp);
    }

    @RequestMapping("/add")
    public ResponseVO addGroupMember(@RequestBody @Validated AddGroupMemberReq req) {
        return imGroupMemberService.addGroupMember(req);
    }

    @RequestMapping("remove")
    public ResponseVO removeGroupMember(@RequestBody @Validated RemoveGroupMemberReq req) {
        return imGroupMemberService.removeGroupMember(req);
    }

    @RequestMapping("exit")
    public ResponseVO exitGroupMember(@RequestBody @Validated ExitGroupMemberReq req) {
        return imGroupMemberService.exitGroupMember(req);
    }

    @RequestMapping("mute")
    public ResponseVO speak(@RequestBody @Valid MuteMemberReq req){
        return imGroupMemberService.speak(req);
    }
}
