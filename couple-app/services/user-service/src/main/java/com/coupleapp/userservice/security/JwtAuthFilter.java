package com.coupleapp.userservice.security;
import com.coupleapp.common.security.JwtTokenProvider;
import jakarta.servlet.*; import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException; import java.util.*;
@Component @RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtProvider;
    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String h = req.getHeader("Authorization");
        if (h==null||!h.startsWith("Bearer ")){chain.doFilter(req,res);return;}
        String token=h.substring(7);
        if(!jwtProvider.validateToken(token)){res.sendError(401,"Invalid token");return;}
        var p=new AuthenticatedUser(jwtProvider.getUserId(token),jwtProvider.getCoupleId(token));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(p,null,Collections.emptyList()));
        chain.doFilter(req,res);
    }
}
