package com.lld.im.service.friendship.controller;

import com.lld.im.common.ResponseVO;
import com.lld.im.service.friendship.model.req.*;
import com.lld.im.service.friendship.model.resp.CheckFriendShipResp;
import com.lld.im.service.friendship.model.resp.ImportFriendshipResp;
import com.lld.im.service.friendship.service.ImFriendshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1/friendship")
@RequiredArgsConstructor
public class ImFriendShipController {

    private final ImFriendshipService imFriendshipService;

    @RequestMapping("importfriendship")
    public ResponseVO<ImportFriendshipResp> importFriendship(@RequestBody @Valid ImportFriendshipReq importFriendshipReq) {
        ImportFriendshipResp importFriendshipResp = imFriendshipService.importFriendship(importFriendshipReq);
        return ResponseVO.successResponse(importFriendshipResp);
    }

    @RequestMapping("addFriend")
    public ResponseVO addFriend(@RequestBody @Valid AddFriendReq addFriendReq) {
        return imFriendshipService.addFriend(addFriendReq);
    }

    @RequestMapping("updateFriend")
    public ResponseVO importFriendship(@RequestBody @Valid UpdateFriendReq updateFriendReq) {
        return imFriendshipService.updateFriend(updateFriendReq);
    }

    @RequestMapping("deleteFriend")
    public ResponseVO deleteFriend(@RequestBody @Valid DeleteFriendReq deleteFriendReq) {
        return imFriendshipService.deleteFriend(deleteFriendReq);
    }

    @RequestMapping("deleteAllFriend")
    public ResponseVO deleteAllFriend(@RequestBody @Valid DeleteFriendReq deleteFriendReq) {
        return imFriendshipService.deleteAllFriend(deleteFriendReq);
    }

    @RequestMapping("getAllFriendShip")
    public ResponseVO getAllFriend(@RequestBody @Valid GetAllFriendshipReq getAllFriendReq) {
        return imFriendshipService.getAllFriend(getAllFriendReq);
    }

    @RequestMapping("getRelationShip")
    public ResponseVO getRelationShip(@RequestBody @Valid GetRelationshipReq getRelationshipReq) {
        return imFriendshipService.getRelationShip(getRelationshipReq);
    }

    @RequestMapping("checkFriendShip")
    public ResponseVO<List<CheckFriendShipResp>> checkFriendShip(
            @RequestBody @Valid CheckFriendShipReq checkFriendShipReq) {

        List<CheckFriendShipResp> resp = imFriendshipService.checkFriendShip(checkFriendShipReq);
        return ResponseVO.successResponse(resp);
    }

    @RequestMapping("addBlack")
    public ResponseVO addBlack(@RequestBody @Valid AddBlackReq addBlackReq) {
        return imFriendshipService.addBlack(addBlackReq);
    }

    @RequestMapping("deleteBlack")
    public ResponseVO deleteBlack(@RequestBody @Valid DeleteBlackReq deleteBlackReq) {
        return imFriendshipService.deleteBlack(deleteBlackReq);
    }

    @RequestMapping("checkBlack")
    public ResponseVO<List<CheckFriendShipResp>> checkBlack(@RequestBody @Valid CheckFriendShipReq checkFriendShipReq) {
        List<CheckFriendShipResp> resp = imFriendshipService.checkBlack(checkFriendShipReq);
        return ResponseVO.successResponse(resp);
    }

}
