package com.uofc.acmeplex.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseData<T> implements IResponse {

    private String status;
    private String message;
    private T data;

    public void setResponse(ResponseCodeEnum iResponseEnum) {
        this.status = iResponseEnum.getStatus();
        this.message = iResponseEnum.getMessage();
    }

    public static ResponseData<Object> getInstance(ResponseCodeEnum responseCodeEnum, Object data) {
        ResponseData<Object> responseData = new ResponseData();
        responseData.setStatus(responseCodeEnum.getStatus());
        responseData.setMessage(responseCodeEnum.getMessage());
        responseData.setData(data);

        return responseData;
    }

    public static ResponseData<Object> getInstance(ResponseCodeEnum responseCodeEnum) {
        ResponseData<Object> responseData = new ResponseData();
        responseData.setStatus(responseCodeEnum.getStatus());
        responseData.setMessage(responseCodeEnum.getMessage());

        return responseData;
    }
    public static ResponseData<Object> getInstance(ResponseCodeEnum responseCodeEnum, String description) {
        ResponseData<Object> responseData = new ResponseData();
        responseData.setStatus(responseCodeEnum.getStatus());
        responseData.setMessage(description);
        return responseData;
    }
}
