package com.lld.im.service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CallBackController {

    @RequestMapping("callback")
    public void callback(@RequestBody Object req){
        log.info("接收到回调数据：{}",req);
    }
}
