package com.coupleapp.realtimeservice.service;
import com.coupleapp.realtimeservice.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;
@Component @RequiredArgsConstructor
public class WebSocketEventListener {
    private final PresenceService presenceService;
    @EventListener public void onConnect(SessionConnectedEvent e){var u=extract(StompHeaderAccessor.wrap(e.getMessage()));if(u!=null)presenceService.markOnline(u.getUserId(),u.getCoupleId(),"app");}
    @EventListener public void onDisconnect(SessionDisconnectEvent e){var u=extract(StompHeaderAccessor.wrap(e.getMessage()));if(u!=null)presenceService.markOffline(u.getUserId(),u.getCoupleId());}
    private AuthenticatedUser extract(StompHeaderAccessor acc){if(acc.getUser()instanceof UsernamePasswordAuthenticationToken t&&t.getPrincipal()instanceof AuthenticatedUser u)return u;return null;}
}
