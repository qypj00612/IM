package com.lld.im.common.exception;

import com.lld.im.common.enums.ApplicationExceptionEnum;
import com.lld.im.common.enums.BaseCode;
import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException{
    final private int code;
    final private String msg;

    public ApplicationException(BaseCode baseCode) {
        super(baseCode.getDesc());
        this.code = baseCode.getCode();
        this.msg = baseCode.getDesc();
    }

    public ApplicationException(ApplicationExceptionEnum applicationExceptionEnum) {
        super(applicationExceptionEnum.getDesc());
        this.code = applicationExceptionEnum.getCode();
        this.msg = applicationExceptionEnum.getDesc();
    }

    public ApplicationException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
