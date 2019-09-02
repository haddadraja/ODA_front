package com.palo.socketserveur;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;
import java.util.Base64;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static sun.jvm.hotspot.debugger.win32.coff.DebugVC50X86RegisterEnums.TAG;

@Slf4j
public class SocketServer {
    public final static int port = 1111;
    ServerSocket serverSocket;
    public SocketServer() {
    }

    public Thread thread(OutputStream outpustream1) {
        return new Thread(() -> {
            try {
                ServerSocket socketServeur = new ServerSocket(port);
                System.out.println("Lancement du serveur");
                while (true) {
                    Socket socketClient = socketServeur.accept();
                    String message = "";
                    System.out.println("Connexion avec : "+socketClient.getInetAddress());
// InputStream in = socketClient.getInputStream();
// OutputStream out = socketClient.getOutputStream();
                    BufferedReader in = new BufferedReader(
                    new InputStreamReader(socketClient.getInputStream()));
                    PrintStream out = new PrintStream(socketClient.getOutputStream());
                    System.out.println(in.readLine());
                    //out.println(message);
                    while(true){
                        //System.out.println(in.readLine());
                    }
                    //socketClient.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


/*
    public static class UDPClient implements Runnable {
        String name = "";
        long sleepTime = 1000;

        UDPClient(String pName, long sleep) {
            name = pName;
            sleepTime = sleep;
        }

        public void run() {
            int nbre = 0;
            while (true) {
                String envoi = name + "-" + (++nbre);
                byte[] buffer = envoi.getBytes();

                try {
                    //On initialise la connexion côté client
                    DatagramSocket client = new DatagramSocket();

                    //On crée notre datagramme
                    InetAddress adresse = InetAddress.getByName("127.0.0.1");
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, adresse, port);

                    //On lui affecte les données à envoyer
                    packet.setData(buffer);

                    //On envoie au serveur
                    client.send(packet);

                    //Et on récupère la réponse du serveur
                    byte[] buffer2 = new byte[8196];
                    DatagramPacket packet2 = new DatagramPacket(buffer2, buffer2.length, adresse, port);
                    client.receive(packet2);
                    print(envoi + " a reçu une réponse du serveur : ");
                    println(new String(packet2.getData()));

                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(2==1) break;
            }
        }
    }
*/
}
