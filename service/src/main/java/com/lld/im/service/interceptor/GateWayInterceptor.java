package com.lld.im.service.interceptor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.lld.im.common.ResponseVO;
import com.lld.im.common.enums.ApplicationExceptionEnum;
import com.lld.im.common.enums.BaseCode;
import com.lld.im.common.enums.GateWayErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@RequiredArgsConstructor
public class GateWayInterceptor implements HandlerInterceptor {

    private final IdentityCheck identityCheck;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String appId = request.getParameter("appId");
        if(StrUtil.isBlank(appId)){
            resp(ResponseVO.errorResponse(GateWayErrorCode.APPID_NOT_EXIST),response);
            return false;
        }
        String operator = request.getParameter("operator");
        if(StrUtil.isBlank(operator)){
            resp(ResponseVO.errorResponse(GateWayErrorCode.OPERATER_NOT_EXIST),response);
            return false;
        }
        String userSign = request.getParameter("userSign");
        if(StrUtil.isBlank(userSign)){
            resp(ResponseVO.errorResponse(GateWayErrorCode.USERSIGN_NOT_EXIST),response);
            return false;
        }
        ApplicationExceptionEnum applicationExceptionEnum = identityCheck.checkUserSign(appId, operator, userSign);
        if(applicationExceptionEnum != BaseCode.SUCCESS){
            resp(ResponseVO.errorResponse(applicationExceptionEnum),response);
            return false;
        }

        return true;
    }

    private void resp(ResponseVO responseVO, HttpServletResponse resp) throws Exception {
        PrintWriter out = null;
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=utf-8");
        try {
            String jsonString = JSONObject.toJSONString(responseVO);
            out = resp.getWriter();
            out.write(jsonString);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(out != null){
                out.close();
            }
        }
    }
}
