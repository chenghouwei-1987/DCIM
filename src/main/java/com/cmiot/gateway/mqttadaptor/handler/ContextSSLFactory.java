package com.cmiot.gateway.mqttadaptor.handler;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * 初始化sslcontext类
 */
public class ContextSSLFactory {

    private final static SslContext SSL_CONTEXT_S;

    static {
        SslContext sslContext = null;

        FileInputStream fs = null;
        try {
            fs = new FileInputStream(ContextSSLFactory.class.getResource("/serverCert.pem").getFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            sslContext = SslContextBuilder.forClient().trustManager(fs).build();
        } catch (SSLException e) {
            e.printStackTrace();
        }
        SSL_CONTEXT_S = sslContext;
    }

    public static SslContext getSslContext() {
        return SSL_CONTEXT_S;
    }

}
