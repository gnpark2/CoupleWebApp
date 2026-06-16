package com.coupleapp.realtimeservice.config;
import com.coupleapp.common.security.JwtTokenProvider;
import com.coupleapp.realtimeservice.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.*;
import java.util.Collections;
@Configuration @EnableWebSocketMessageBroker @RequiredArgsConstructor
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtTokenProvider jwtProvider;
    @Override public void configureClientInboundChannel(ChannelRegistration reg){
        reg.interceptors(new ChannelInterceptor(){
            @Override public Message<?> preSend(Message<?> msg,MessageChannel ch){
                StompHeaderAccessor acc=MessageHeaderAccessor.getAccessor(msg,StompHeaderAccessor.class);
                if(acc!=null&&StompCommand.CONNECT.equals(acc.getCommand())){
                    String t=acc.getFirstNativeHeader("Authorization");
                    if(t!=null&&t.startsWith("Bearer ")){
                        t=t.substring(7);
                        if(jwtProvider.validateToken(t)){
                            var p=new AuthenticatedUser(jwtProvider.getUserId(t),jwtProvider.getCoupleId(t));
                            acc.setUser(new UsernamePasswordAuthenticationToken(p,null,Collections.emptyList()));
                        }
                    }
                }
                return msg;
            }
        });
    }
}
