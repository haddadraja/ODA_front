package com.palo.socketserveur.controller;

import com.palo.socketserveur.SocketServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/video")
public class VideoController {
    private SocketServer socketServer;
    public VideoController(final SocketServer socketServer) {
        this.socketServer = socketServer;
    }

    @RequestMapping("/lire")
    public StreamingResponseBody handleRequest(){
        return new StreamingResponseBody() {
            @Override
            public void writeTo(final OutputStream outputStream) throws IOException {
                socketServer.thread(outputStream).start();
            }
        };
    }
}
