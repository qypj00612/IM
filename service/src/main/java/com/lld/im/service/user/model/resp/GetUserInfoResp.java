package com.lld.im.service.user.model.resp;


import com.lld.im.service.user.dao.ImUserData;
import lombok.Data;

import java.util.List;

@Data
public class GetUserInfoResp {

    private List<ImUserData> userDataItem;

    private List<String> failUser;


}
