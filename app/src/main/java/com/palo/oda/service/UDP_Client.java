package com.palo.oda.service;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import static android.content.ContentValues.TAG;

public class UDP_Client extends Thread {

    private InetAddress IPAddress = null;
    private String response = "Hello Android!" ;
    public byte[] Message;
    private static String MY_LOCAL_IP = "10.0.2.2";
    private static int PORT = 1111;
    private DataOutput dataOutput;
    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;
    private boolean canceled = true;

    @Override
    public synchronized void run() {
        try {
            Socket socket = null;

            try {
                socket = new Socket(MY_LOCAL_IP, PORT);

                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();
                PrintStream out = new PrintStream(socket.getOutputStream());
                out.println("TEST".getBytes());
                out.flush();
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }finally{
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}