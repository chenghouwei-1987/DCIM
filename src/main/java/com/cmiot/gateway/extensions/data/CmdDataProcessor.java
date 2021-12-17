package com.cmiot.gateway.extensions.data;

import com.cmiot.gateway.entity.DeviceCmdMessage;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

/**
 * 命令数据处理器扩展
 */
public class CmdDataProcessor {

    private CmdDataProcessor() {
    }

    private static class ExpiryItem {
        @Getter
        private DeviceCmdMessage protocolCmdMessage;
        private long expiryTime;

        ExpiryItem(DeviceCmdMessage protocolCmdMessage, long liveTime) {
            this.protocolCmdMessage = protocolCmdMessage;
            if (liveTime > 0) {
                this.expiryTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(liveTime, TimeUnit.MILLISECONDS);
            } else {
                expiryTime = 0;
            }
        }

        boolean expired() {
            if (expiryTime > 0) {
                return System.currentTimeMillis() - expiryTime > 0;
            }
            return false;
        }
    }

    private static Map<String, ConcurrentLinkedDeque<ExpiryItem>> deviceCmdQueue = new ConcurrentHashMap<>();

    /**
     * 根据设备id从缓存中获取命令
     *
     * @param deviceId 设备ID
     * @return 第一个未超时的ProtocolCmdMessage
     */
    public static DeviceCmdMessage pollCmd(String deviceId) {
        ConcurrentLinkedDeque<ExpiryItem> queue = deviceCmdQueue.get(deviceId);
        if (null == queue) {
            return null;
        }

        while (true) {
            ExpiryItem cmdMessageExpiryItem = queue.poll();
            if (null == cmdMessageExpiryItem) {
                return null;
            }
            if (!cmdMessageExpiryItem.expired()) {
                return cmdMessageExpiryItem.getProtocolCmdMessage();
            }
        }
    }


    /**
     * 添加不超时的命令
     *
     * @param deviceId   设备id
     * @param cmdMessage 命令内容
     * @return 是否添加成功
     */
    public static boolean putCmd(String deviceId, DeviceCmdMessage cmdMessage) {
        return putCmdWithTimeout(deviceId, cmdMessage, 0);
    }

    /**
     * 添加超时命令
     *
     * @param deviceId   设备id
     * @param cmdMessage 命令内容
     * @param timeout    超时时间(单位为s)
     * @return 是否添加成功
     */
    public static boolean putCmdWithTimeout(String deviceId, DeviceCmdMessage cmdMessage, int timeout) {
        ConcurrentLinkedDeque<ExpiryItem> queue = deviceCmdQueue.get(deviceId);
        if (null != queue) {
            queue.push(new ExpiryItem(cmdMessage, timeout));
            return true;
        }

        queue = new ConcurrentLinkedDeque<>();
        queue.push(new ExpiryItem(cmdMessage, timeout));
        deviceCmdQueue.put(deviceId, queue);
        return true;
    }
}
