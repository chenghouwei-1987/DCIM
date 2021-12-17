package com.cmiot.gateway.custom;

import com.cmiot.gateway.entity.DeviceMessage;

/**
 * 自定义协议编码器示例代码，仅以设备登陆成功为例，可删除
 */
public class SampleCustomProtocolEncoder {

    /**
     * 将协议网关内部数据转换为自定义协议数据
     * <p>
     * 示例代码中，二进制数组第一字节表示消息类型，剩余字节为响应的payload
     *
     * @param message 协议网关内部数据
     * @return 自定义协议数据
     */
    public static byte[] encode(DeviceMessage message) {
        switch (message.getMessageType()) {
            // 设备主动登陆成功
            case LOGIN_ACCEPTED_RESPONSE:
                byte[] payload = message.getData();
                byte[] data = new byte[payload.length + 1];
                // 实例代码中0x81表示设备成功登陆的响应
                data[0] = (byte) 0x81;
                System.arraycopy(payload, 0, data, 1, payload.length);
                return data;
            default:
                // 返回null表示丢弃此次下行数据
                return null;
        }
    }

}
