package com.lld.im.ai.dao.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AITools {

    @Tool(description = "获取当前时间")
    public String getCurrentDateTime() {
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId())
                .toString();
    }

}
