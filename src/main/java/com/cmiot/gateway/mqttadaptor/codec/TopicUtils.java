package com.cmiot.gateway.mqttadaptor.codec;


import com.cmiot.gateway.entity.MessageType;

/**
 * 平台接入机Topic相关工具类
 */
final class TopicUtils {
    private final static String LOGIN_TOPIC_FORMAT = "$gw-proxy/%d/%s/login";
    private final static String LOGOUT_TOPIC_FORMAT = "$gw-proxy/%d/%s/logout";
    private final static String DP_TOPIC_FORMAT = "$gw-proxy/%d/%s/dp/post/json";
    private final static String CMD_RESPOND_TOPIC_FORMAT = "$gw-proxy/%d/%s/cmd/response/%s";
    private final static String GW_CMD_RESPOND_TOPIC_FORMAT = "$gw-ctrl/%s/%s/cmd/response/%s";
    private final static String IMAGE_UPDATE_TOPIC_FORMAT = "$gw-proxy/%d/%s/image/update";
    private final static String IMAGE_GET_TOPIC_FORMAT = "$gw-proxy/%d/%s/image/get";


    final static String LOGIN = "login";
    final static String LOGOUT = "logout";
    final static String DP = "dp";
    final static String CMD = "cmd";
    final static String IMAGE = "image";

    private final static String POST = "post";
    private final static String JSON = "json";

    private final static String REQUEST = "request";
    private final static String RESPONSE = "response";

    private final static String UPDATE = "update";
    private final static String GET = "get";
    private final static String DELTA = "delta";
    private final static String ACCEPTED = "accepted";
    private final static String REJECTED = "rejected";
    private final static String NOTIFY = "notify";


    private final static int LOGIN_RESPONSE_TOPIC_LEN = 5;
    private final static int LOGOUT_RESPONSE_TOPIC_LEN = 5;
    private final static int DP_RESPONSE_TOPIC_LEN = 7;
    private final static int CMD_DOWN_LINK_TOPIC_LEN = 6;
    private final static int GW_CMD_TOPIC_LEN = 6;
    private final static int CMD_DOWN_LINK_RESPONSE_TOPIC_LEN = 7;
    private final static int GW_CMD_RESPONSE_TOPIC_LEN = 7;
    private final static int IMAGE_UPDATE_RESPONSE_TOPIC_LEN = 6;
    private final static int IMAGE_GET_RESPONSE_TOPIC_LEN = 6;
    private final static int IMAGE_DELTA_RESPONSE_TOPIC_LEN = 5;

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 上行login的MQTT topic
     */
    static String createLoginTopic(long pid, String deviceName) {
        return String.format(LOGIN_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 上行logout的MQTT topic
     */
    static String createLogoutTopic(long pid, String deviceName) {
        return String.format(LOGOUT_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 上行dp的MQTT topic
     */
    static String createDpTopic(long pid, String deviceName) {
        return String.format(DP_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 上行image update的MQTT topic
     */
    static String createImageUpdateTopic(long pid, String deviceName) {
        return String.format(IMAGE_UPDATE_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 上行image get的MQTT topic
     */
    static String createImageGetTopic(long pid, String deviceName) {
        return String.format(IMAGE_GET_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @param cmdId      命令id
     * @return 上行设备命令回复的MQTT topic
     */
    static String createCmdRespondTopic(long pid, String deviceName, String cmdId) {
        return String.format(CMD_RESPOND_TOPIC_FORMAT, pid, deviceName, cmdId);
    }

    /**
     * @param svcId      svcId
     * @param instName   instName
     * @param cmdId      命令id
     * @return 上行设备命令回复的MQTT topic
     */
    static String createGwCmdRespondTopic(String svcId,String instName, String cmdId) {
        return String.format(GW_CMD_RESPOND_TOPIC_FORMAT, svcId, instName, cmdId);
    }

    /**
     * @param topic MQTT topic
     * @return 用"/"分割后的tokens
     */
    static String[] splitTopic(String topic) {
        String[] tokens = topic.split("/");

        if (topic.endsWith("/")) {
            String[] newSplitted = new String[tokens.length + 1];
            System.arraycopy(tokens, 0, newSplitted, 0, tokens.length);
            newSplitted[tokens.length] = "";
            tokens = newSplitted;
        }
        return tokens;
    }

    /**
     * 校验下行login响应topic
     *
     * @param tokens 用"/"分割后MQTT topic后的tokens
     * @return 是否合法
     */
    static boolean validateDownLinkLoginTopic(String[] tokens) {
        if (tokens.length != LOGIN_RESPONSE_TOPIC_LEN) {
            return false;
        }
        if (!LOGIN.equals(tokens[3])) {
            return false;
        }
        return ACCEPTED.equals(tokens[4]) || REJECTED.equals(tokens[4]);
    }

    /**
     * 校验下行logout响应或notify的topic
     *
     * @param tokens 用"/"分割后MQTT topic后的tokens
     * @return 是否合法
     */
    static boolean validateDownLinkLogoutTopic(String[] tokens) {
        if (tokens.length != LOGOUT_RESPONSE_TOPIC_LEN) {
            return false;
        }
        if (!LOGOUT.equals(tokens[3])) {
            return false;
        }
        return ACCEPTED.equals(tokens[4]) || NOTIFY.equals(tokens[4]);
    }

    /**
     * 校验下行dp响应topic
     *
     * @param tokens 用"/"分割后MQTT topic后的tokens
     * @return 是否合法
     */
    static boolean validateDownLinkDpTopic(String[] tokens) {
        if (tokens.length != DP_RESPONSE_TOPIC_LEN) {
            return false;
        }
        if (!DP.equals(tokens[3])) {
            return false;
        }
        if (!POST.equals(tokens[4])) {
            return false;
        }
        if (!JSON.equals(tokens[5])) {
            return false;
        }

        return ACCEPTED.equals(tokens[6]) || REJECTED.equals(tokens[6]);
    }

    /**
     * 校验下行cmd或设备回复命令后的平台响应topic
     *
     * @param tokens 用"/"分割后MQTT topic后的tokens
     * @return 是否合法
     */
    static boolean validateDownLinkCmdTopic(String[] tokens) {
        if (tokens.length < CMD_DOWN_LINK_TOPIC_LEN) {
            return false;
        }
        if (!CMD.equals(tokens[3])) {
            return false;
        }
        switch (tokens[4]) {
            case REQUEST:
                return tokens.length == CMD_DOWN_LINK_TOPIC_LEN;
            case RESPONSE:
                if (tokens.length != CMD_DOWN_LINK_RESPONSE_TOPIC_LEN) {
                    return false;
                }
                return ACCEPTED.equals(tokens[6]) || REJECTED.equals(tokens[6]);
            default:
                return false;
        }
    }

    /**
     * 校验下行image update、get或delta消息的topic
     *
     * @param tokens 用"/"分割后MQTT topic后的tokens
     * @return 是否合法
     */
    static boolean validateDownLinkImageTopic(String[] tokens) {
        if (tokens.length < IMAGE_DELTA_RESPONSE_TOPIC_LEN) {
            return false;
        }
        if ((!IMAGE.equals(tokens[3]))) {
            return false;
        }
        switch (tokens[4]) {
            case UPDATE: {
                if (tokens.length != IMAGE_UPDATE_RESPONSE_TOPIC_LEN) {
                    return false;
                }
                return ACCEPTED.equals(tokens[5]) || REJECTED.equals(tokens[5]) || DELTA.equals(tokens[5]);
            }
            case GET: {
                if (tokens.length != IMAGE_GET_RESPONSE_TOPIC_LEN) {
                    return false;
                }
                return ACCEPTED.equals(tokens[5]) || REJECTED.equals(tokens[5]);
            }
            default:
                return false;
        }
    }

    /**
     * @param tokens 用"/"分割后MQTT topic后的tokens
     * @return 下行login topic中对应到gateway内部消息的MessageType
     */
    static MessageType getDownLinkLoginMessageType(String[] tokens) {
        return ACCEPTED.equals(tokens[4]) ? MessageType.LOGIN_ACCEPTED_RESPONSE : MessageType.LOGIN_REJECTED_RESPONSE;
    }

    /**
     * @param tokens 用"/"分割后MQTT topic后的tokens
     * @return 下行logout topic中对应到gateway内部消息的MessageType
     */
    static MessageType getDownLinkLogoutMessageType(String[] tokens) {
        return ACCEPTED.equals(tokens[4]) ? MessageType.LOGOUT_ACCEPTED_RESPONSE : MessageType.LOGOUT_NOTIFY_RESPONSE;
    }

    /**
     * @param tokens 用"/"分割后MQTT topic后的tokens
     * @return dp topic中对应到gateway内部消息的MessageType
     */
    public static MessageType getDownLinkDpMessageType(String[] tokens) {
        return ACCEPTED.equals(tokens[6]) ? MessageType.DP_ACCEPTED_RESPONSE : MessageType.DP_REJECTED_RESPONSE;
    }

    /**
     * @param tokens 用"/"分割后MQTT topic后的tokens
     * @return 下行cmd topic中对应到gateway内部消息的MessageType
     */
    static MessageType getDownLinkCmdMessageType(String[] tokens) {
        switch (tokens[4]) {
            case REQUEST:
                return MessageType.DOWN_LINK_CMD;
            case RESPONSE:
                return ACCEPTED.equals(tokens[6]) ? MessageType.CMD_REPLY_ACCEPTED_RESPONSE : MessageType.CMD_REPLY_REJECTED_RESPONSE;
            default:
                return MessageType.UNKNOWN;
        }
    }


    /**
     * @param tokens 用"/"分割后MQTT topic后的tokens
     * @return 下行image topic中对应到gateway内部消息的MessageType
     */
    static MessageType getDownLinkImageMessageType(String[] tokens) {
        switch (tokens[4]) {
            case UPDATE:
                switch (tokens[5]) {
                    case ACCEPTED:
                        return MessageType.IMAGE_UPDATE_ACCEPTED_RESPONSE;
                    case REQUEST:
                        return MessageType.IMAGE_UPDATE_REJECTED_RESPONSE;
                    case DELTA:
                        return MessageType.IMAGE_DELTA;
                    default:
                        return MessageType.UNKNOWN;
                }
            case GET:
                return ACCEPTED.equals(tokens[5]) ? MessageType.IMAGE_GET_ACCEPTED_RESPONSE : MessageType.IMAGE_GET_REJECTED_RESPONSE;
            default:
                return MessageType.UNKNOWN;
        }
    }

    /**
     * 校验下行网关cmd或回复命令后的平台响应topic
     *
     * @param tokens 用"/"分割后MQTT topic后的tokens
     * @return 是否合法
     */
    static boolean validateGwCmdTopic(String[] tokens) {
        if (tokens.length < GW_CMD_TOPIC_LEN) {
            return false;
        }
        if (!CMD.equals(tokens[3])) {
            return false;
        }
        switch (tokens[4]) {
            case REQUEST:
                return tokens.length == GW_CMD_TOPIC_LEN;
            case RESPONSE:
                if (tokens.length != GW_CMD_RESPONSE_TOPIC_LEN) {
                    return false;
                }
                return ACCEPTED.equals(tokens[6]) || REJECTED.equals(tokens[6]);
            default:
                return false;
        }
    }

    /**
     * @param tokens 用"/"分割后MQTT topic后的tokens
     * @return 网关命令相关类型对应到gateway内部消息的MessageType
     */
    static MessageType getGwCmdMessageType(String[] tokens) {
        switch (tokens[4]) {
            case REQUEST:
                return MessageType.DOWN_LINK_GW_CMD;
            case RESPONSE:
                return ACCEPTED.equals(tokens[6]) ? MessageType.GW_CMD_REPLY_ACCEPTED_RESP : MessageType.GW_CMD_REPLY_REJECTED_RESP;
            default:
                return MessageType.UNKNOWN;
        }

    }
}
