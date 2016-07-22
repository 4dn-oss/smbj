/*
 * Copyright (C)2016 - SMBJ Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hierynomus.smbj.transport;

import com.hierynomus.protocol.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public abstract class PacketReader<P extends Packet<P, ?>> implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(PacketReader.class);

    protected InputStream in;
    private PacketReceiver<P> handler;

    public PacketReader(InputStream in, PacketReceiver<P> handler) {
        this.in = in;
        this.handler = handler;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                readPacket();
            } catch (TransportException e) {
                handler.handleError(e);
                throw new RuntimeException(e);
            }
        }
    }

    private void readPacket() throws TransportException {
        P smb2Packet = doRead();
        logger.debug("Received packet << {} >>", smb2Packet);
        handler.handle(smb2Packet);
    }

    /**
     * Read the actual SMB2 Packet from the {@link InputStream}
     * @return the read SMB2Packet
     * @throws TransportException
     */
    protected abstract P doRead() throws TransportException;
}
