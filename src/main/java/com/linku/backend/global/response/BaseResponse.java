package com.linku.backend.global.response;

import lombok.Getter;

@Getter
public class BaseResponse<T> {
    private final boolean isSuccess;
    private final int code;
    private final String message;
    private final T result;

    public BaseResponse(ResponseCode responseCode, T result) {
        this.isSuccess = responseCode.isSuccess();
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        this.result = result;
    }
}
