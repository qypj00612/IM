package com.lld.im.service.user.model.resp;

import lombok.Data;

import java.util.List;

@Data
public class ImportUserResp {
    private List<String> success;
    private List<String> error;
}
