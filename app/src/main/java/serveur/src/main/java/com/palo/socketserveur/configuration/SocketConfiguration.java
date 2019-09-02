package serveur.src.main.java.com.palo.socketserveur.configuration;

import io.netty.channel.unix.Socket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketConfiguration {

    @Bean
    public Socket intializesocket(){
        return new Socket(12);
    }
}
