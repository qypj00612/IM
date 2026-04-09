package com.lld.im.common;

import com.lld.im.common.enums.ApplicationExceptionEnum;
import com.lld.im.common.enums.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class ResponseVO<T> {

    private int code;
    private String msg;
    private T data;

    public ResponseVO(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public ResponseVO(int code) {
        this.code = code;
    }

    public static <T> ResponseVO<T> successResponse(T data) {
        return new ResponseVO<T>(
                BaseCode.SUCCESS.getCode()
                , BaseCode.SUCCESS.getDesc()
                , data);
    }

    public static <T> ResponseVO<T> successResponse() {
        return new ResponseVO<T>(200);
    }

    public static ResponseVO errorResponse(ApplicationExceptionEnum applicationExceptionEnum) {
        return new ResponseVO(applicationExceptionEnum.getCode(), applicationExceptionEnum.getDesc());
    }

    public static ResponseVO errorResponse(int code, String msg) {
        return new ResponseVO(code, msg);
    }

    public boolean isOk(){
        return this.code == 200;
    }

    public static ResponseVO errorResponse() {
        return ResponseVO.errorResponse(BaseCode.SYSTEM_ERROR);
    }

}
