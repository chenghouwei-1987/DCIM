package com.cmiot.gateway.custom;

import com.cmiot.gateway.config.IDeviceConfig;
import com.cmiot.gateway.entity.DeviceEntity;
import com.cmiot.gateway.entity.DeviceMessage;
import com.cmiot.gateway.entity.MessageType;
import com.cmiot.gateway.extensions.logging.ILogger;
import com.cmiot.gateway.security.IAuthenticator;
import com.cmiot.gateway.security.IAuthorizatorPolicy;
import com.cmiot.gateway.utils.ExtensionUtils;
import lombok.extern.slf4j.Slf4j;

import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.DEV_UP_LINK;

/**
 * 自定义协议解码器示例代码，仅以设备登陆为例，可删除
 */
@Slf4j
public class SampleCustomProtocolDecoder {

    /**
     * 日志
     */
    private static ILogger logger = ExtensionUtils.getLogger();
    /**
     * 设备配置
     */
    private static IDeviceConfig deviceConfig = ExtensionUtils.getDeviceConfig();
    /**
     * 鉴权
     */
    private static IAuthenticator authenticator = ExtensionUtils.getAuthenticator();
    /**
     * 权限
     */
    private static IAuthorizatorPolicy authorizatorPolicy = ExtensionUtils.getAuthorizatorPolicy();

    /**
     * 将自定义协议数据转换为协议网关内部数据
     * <p>
     * 示例代码中，二进制数组第一字节表示消息类型，剩余字节为设备的标识，即devices.conf中的originalIdentity
     *
     * @param data 自定义协议数据
     * @return 协议网关内部数据
     */
    public static DeviceMessage decode(byte[] data) {
        if (data.length <= 1) {
            // 返回null表示丢弃此次消息
            return null;
        }

        // 上行数据的payload
        byte[] payload = null;
        // 协议网关内部消息
        DeviceMessage message;
        // OneNET设备信息实体
        DeviceEntity deviceEntity;
        // 消息类型，请注意解码器中构造的消息类型仅支持上行类型
        MessageType messageType;
        byte[] originalIdentityBytes;
        switch (data[0]) {
            // 0x01表示设备登陆
            case 0x01:
                messageType = MessageType.LOGIN;
                originalIdentityBytes = new byte[data.length - 1];
                System.arraycopy(data, 1, originalIdentityBytes, 0, data.length - 1);
                // 获取设备对应的OneNET设备信息
                // 用户可自主开发工具用于获取和缓存设备信息的映射
                deviceEntity = deviceConfig.getDeviceEntity(new String(originalIdentityBytes));
                String deviceId = deviceEntity.getDeviceId();
                // 鉴权及权限校验
                if (!authenticator.checkValid(deviceId) ||
                        !authorizatorPolicy.canWrite(deviceId)) {
                    logger.logDevWarn(log, DEV_UP_LINK, deviceEntity.getProductId(), deviceId, "device auth failed");
                    return null;
                }
                break;
            //offline
            case 0x02:
                originalIdentityBytes = new byte[data.length - 1];
                System.arraycopy(data, 1, originalIdentityBytes, 0, data.length - 1);
                messageType = MessageType.LOGOUT;
                deviceEntity = deviceConfig.getDeviceEntity(new String(originalIdentityBytes));
                break;
            default:
                return null;

        }
        message = DeviceMessage.builder()
                .messageType(messageType)
                // 登陆时data可为空
                .data(payload)
                .productId(deviceEntity.getProductId())
                .deviceId(deviceEntity.getDeviceId())
                .deviceName(deviceEntity.getDeviceName())
                .deviceKey(deviceEntity.getDeviceKey())
                .build();
        return message;
    }

}
