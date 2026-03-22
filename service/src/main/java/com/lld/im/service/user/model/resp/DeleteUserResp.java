package com.lld.im.service.user.model.resp;

import lombok.Data;

import java.util.List;

@Data
public class DeleteUserResp {
    private List<String> success;
    private List<String> error;
}
