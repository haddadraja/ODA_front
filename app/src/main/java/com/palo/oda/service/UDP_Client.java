package com.palo.oda.service;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Message;
import android.util.Log;

import com.palo.oda.ui.preview.CameraFragment;
import com.palo.oda.ui.preview.DataGramPacketListener;
import com.palo.util.Util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Camera camera;
    private Size size = new Size(500,450);
    private UDP_Client udp_client;
    private ByteArrayOutputStream baos;
    CameraFragment.UdpClientHandler handler;
    public UDP_Client(Camera camera) throws SocketException {
        this.camera = camera;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Size{
        private int height;
        private int width;
    }

    private void sendVideo(byte[] msg) throws UnknownHostException, SocketException {
        socket = new DatagramSocket(PORT);
        ipAddress = InetAddress.getByName(MY_LOCAL_IP);
        Log.i(TAG, "sendVideo: ipaddress" + ipAddress.getHostAddress());
            if (msg != null) {
                Log.i(TAG, "sendVideo: send packet udp mode" + i++);
                //--------- send packet---------------//
                Log.i(TAG, "sendVideo: send packet udp mode" + msg.length);
                DatagramPacket packet = new DatagramPacket(msg, msg.length, ipAddress, PORT);
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
                } else {
                    Log.i(TAG, "sendVideo: " + received);

                }
            }
    }
    private Camera.PreviewCallback byteCameraBiConsumer(){
        return (bytes, camera) -> {
           // Log.i(TAG, "onPreviewFrame: " + bytes.length);
            YuvImage image = new YuvImage(bytes, ImageFormat.NV21,
                    size.width, size.height, null);
            baos = new ByteArrayOutputStream();
            int jpeg_quality = 80;
            image.compressToJpeg(new Rect(0, 0, size.width, size.height),
                    jpeg_quality, baos);
            try {
                sendVideo(baos.toByteArray());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "onPreviewFrame image traitement: " + baos.toByteArray().length);
        };
    }

    @Override
    public void run() {
        camera.setPreviewCallback(byteCameraBiConsumer());
    }

    public void close() {
        atomicBoolean.set(false);
    }
    @Override
    public void destroy() {
        super.destroy();
    }

    private void sendState(String state){
        handler.sendMessage(
                Message.obtain(handler,
                        CameraFragment.UdpClientHandler.UPDATE_STATE, state));
    }

}