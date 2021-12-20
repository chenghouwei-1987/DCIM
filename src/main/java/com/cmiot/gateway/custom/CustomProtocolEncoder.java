package com.cmiot.gateway.custom;

import com.cmiot.gateway.entity.DeviceMessage;

/**
 * 自定义协议编码器，此类由开发者编写，必须实现
 */
public class CustomProtocolEncoder {

    /**
     * 将协议网关内部数据转换为自定义协议数据
     * 当MessageType=时DOWN_LINK_CMD、CMD_REPLY_ACCEPTED_RESPONSE、CMD_REPLY_REJECTED_RESPONSE时
     * 应该将message强转为ProtocolCmdMessage对象以便获取cmdId
     *
     * @param message 协议网关内部数据
     * @return 自定义协议数据
     */
    public byte[] encode(DeviceMessage message) {

        // 示例代码
        return SampleCustomProtocolEncoder.encode(message);
    }
}
