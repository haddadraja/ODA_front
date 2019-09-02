package com.palo.oda.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

public class UDP_Serveur {
    private DatagramSocket socket;
    private boolean running = true;
    private byte[] buf = new byte[65000];
    private AtomicBoolean atomicBoolean = new AtomicBoolean(true);

    public UDPService(int PORT) throws SocketException {
        socket = new DatagramSocket(PORT);
    }

    public void run() {
        while (atomicBoolean.get()) {
            log.info("start thread:{}", running);
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            DatagramPacket finalPacket1 = packet;
            Util.wrapCheckedException(() -> socket.receive(finalPacket1));
            InetAddress address = packet.getAddress();
            int port = finalPacket1.getPort();
            String received = new String(finalPacket1.getData(), 0, finalPacket1.getLength());
            if (received.equals("end")) {
                running = false;
                continue;
            } else {
                log.info(" this is the message: {}", received);
                byte[] message = "receive".getBytes();
                packet = new DatagramPacket(message, message.length, address, port);
                DatagramPacket finalPacket2 = packet;
                Util.wrapCheckedException(() -> socket.send(finalPacket2));
            }
        }
        socket.close();
        atomicBoolean.set(false);
    }

    // configuration
    @Configuration
    public class UDP_configuration {

        @Bean
        @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
        public UDPService udpServerConfiguration() throws IOException {
            UDPService udpServer = new UDPService(64700);
            udpServer.start();
            return udpServer;
        }
    }
}
