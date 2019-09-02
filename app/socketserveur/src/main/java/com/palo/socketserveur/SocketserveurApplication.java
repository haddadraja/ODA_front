package com.palo.socketserveur;

import jdk.jfr.events.SocketReadEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.InetSocketAddress;

@SpringBootApplication
public class SocketserveurApplication {

    public static void main(String[] args) {

        SpringApplication.run(SocketserveurApplication.class, args);
        //SocketServer socketServer = new SocketServer(new InetSocketAddress("127.0.0.1", 1111));
        //socketServer.thread().start();
    }

    @Bean
    public SocketServer socketServer(){
        return new SocketServer();
    }

}
