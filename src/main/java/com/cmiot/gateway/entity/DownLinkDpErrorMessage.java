package com.cmiot.gateway.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 上传DP错误消息(平台下行)
 */
@Data
public final class DownLinkDpErrorMessage extends DownLinkErrorMessage {
    @JSONField(name = "id")
    private int id;
}


