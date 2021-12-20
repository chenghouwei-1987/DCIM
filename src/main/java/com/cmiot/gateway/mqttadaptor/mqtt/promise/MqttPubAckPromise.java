/*
 * Copyright (C) 2018 Issey Yamakoshi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cmiot.gateway.mqttadaptor.mqtt.promise;

import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.util.Timeout;
import io.netty.util.concurrent.EventExecutor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class MqttPubAckPromise extends MqttPromise<Void> {

    private final int packetId;

    public MqttPubAckPromise(EventExecutor executor, long timeout, TimeUnit unit, int packetId) {
        super(executor);
        this.packetId = packetId;
    }

    @Override
    public final MqttMessageType messageType() {
        return MqttMessageType.PUBACK;
    }

    public int packetId() {
        return packetId;
    }

    @Override
    public void run(Timeout timeout) {
        tryFailure(new TimeoutException("PubAck timeout"));
    }
}
