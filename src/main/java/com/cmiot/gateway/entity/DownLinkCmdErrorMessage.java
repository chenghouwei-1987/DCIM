package com.cmiot.gateway.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 命令错误消息(平台下行)
 */
@Data
public final class DownLinkCmdErrorMessage extends DownLinkErrorMessage {
    @JSONField(deserialize = false)
    private String cmdId;
}
