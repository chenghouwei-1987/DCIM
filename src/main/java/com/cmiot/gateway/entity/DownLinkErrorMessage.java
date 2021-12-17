package com.cmiot.gateway.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 错误消息(平台下行)
 */
@Data
public class DownLinkErrorMessage {

    @JSONField(name = "err_code")
    private int errCode;

    @JSONField(name = "err_msg")
    private String errMsg;
}
