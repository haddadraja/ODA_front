package com.palo.oda.service;

import android.util.Log;

import com.palo.util.Util;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.ContentValues.TAG;

public class UDP_Client extends Thread {

    private InetAddress ipAddress = null;
    private String response = "Hello Android!";
    public byte[] message;
    public byte[]buff = new byte[65000];
    private static String MY_LOCAL_IP = "10.0.2.2";
    private static int PORT = 64700;
    private DataOutput dataOutput;
    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;
    private boolean canceled = true;
    private DatagramSocket socket;
    private AtomicBoolean atomicBoolean = new AtomicBoolean(true);
    private int i;
    public UDP_Client(byte[] message) {
        this.message = message;
    }


    private void sendVideo(byte[] msg) throws UnknownHostException, SocketException {
        ipAddress = InetAddress.getByName(MY_LOCAL_IP);
        Log.i(TAG, "sendVideo: ipaddress" + ipAddress.getHostAddress());
        socket = new DatagramSocket(PORT);
        while (atomicBoolean.get()) {
            if (message != null) {
                Log.i(TAG, "sendVideo: send packet udp mode" + i++);
                //--------- send packet---------------//
                Log.i(TAG, "sendVideo: send packet udp mode" + message.length);
                DatagramPacket packet = new DatagramPacket(message, message.length, ipAddress, PORT);
                DatagramPacket finalPacket2 = packet;
                Util.wrapCheckedException(() -> socket.send(finalPacket2));
                //---------- receive packet-----------//
                InetAddress address = packet.getAddress();
                packet = new DatagramPacket(buff, buff.length);
                DatagramPacket finalPacket = packet;
                Util.wrapCheckedException(() -> socket.receive(finalPacket));
                String received = new String(finalPacket2.getData(), 0, finalPacket2.getLength());


                if (received.equals("end")) {
                    atomicBoolean.set(false);
                    continue;
                } else {
                    Log.i(TAG, "sendVideo: " + received);

                }
            }
        }
    }


    public void close() {
        atomicBoolean.set(false);
    }

    @Override
    public synchronized void run() {
        try {
            sendVideo(message);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}