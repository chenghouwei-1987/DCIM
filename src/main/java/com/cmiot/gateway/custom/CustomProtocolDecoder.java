package com.cmiot.gateway.custom;

import com.cmiot.gateway.config.IDeviceConfig;
import com.cmiot.gateway.entity.DeviceMessage;
import com.cmiot.gateway.utils.ExtensionUtils;

/**
 * 自定义协议解码器，此类由开发者编写，必须实现
 */
public class CustomProtocolDecoder {

    private IDeviceConfig deviceConfig = ExtensionUtils.getDeviceConfig();

    /**
     * 将自定义协议数据转换为协议网关内部数据
     * 当MessageType=CMD_REPLY时,decode函数应返回ProtocolCmdMessage对象以便填写cmdId
     *
     * @param data 自定义协议数据
     * @return 协议网关内部数据
     */
    public DeviceMessage decode(byte[] data) {

        // 示例代码
        return SampleCustomProtocolDecoder.decode(data);
    }

}
