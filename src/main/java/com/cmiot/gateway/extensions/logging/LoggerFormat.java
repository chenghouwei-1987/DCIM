package com.cmiot.gateway.extensions.logging;


public class LoggerFormat {
    enum Event {
        //内部业务事件
        INNER("Inner"),
        //协议中心相关事件
        PROTOCOL_HUB("ProtocolHub"),
        //设备相关事件
        DEV("Dev"),
        //网关控制连接相关事件
        GW_CTRL("Ctrl"),
        //网关代理连接相关事件
        GW_PROXY("Pxy"),
        //监控相关事件
        METRIC("Metric");

        String str;

        Event(String str) {
            this.str = str;
        }

        public String get() {
            return str;
        }

    }

    public enum Action {
        //
        LAUNCH("launch"),
        SHUTDOWN("shutdown"),
        RUNTIME("runtime"),

        INIT("init"),

        DISCONNECT("disconnect"),
        //平台下行数据
        PLATFORM_DOWN_LINK("platformDownLink"),
        //网关下行数据
        GW_DOWN_LINK("gwDownLink"),
        //网关上行数据
        GW_UP_LINK("gwUpLink"),
        //设备上行数据
        DEV_UP_LINK("devUpLink"),
        LOGOUT("logout"),
        LOGIN("login");


        String str;

        Action(String str) {
            this.str = str;
        }

        public String get() {
            return str;
        }
    }


}
