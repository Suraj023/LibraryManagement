package com.library.config;

import com.library.model.User;
import com.library.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        final String usernameOrEmail;
        try {
            usernameOrEmail = jwtService.extractUsername(jwt);
        } catch (Exception exception) {
            log.warn("Unable to extract username from JWT", exception);
            filterChain.doFilter(request, response);
            return;
        }

        if (!StringUtils.hasText(usernameOrEmail)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null || !jwtService.validateJwtToken(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<User> userOptional = userRepository.findByEmail(usernameOrEmail);
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByUserName(usernameOrEmail);
        }

        if (userOptional.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        User user = userOptional.get();
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (user.getRoles() != null) {
            for (String role : user.getRoles()) {
                if (StringUtils.hasText(role)) {
                    String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                    grantedAuthorities.add(new SimpleGrantedAuthority(authority));
                }
            }
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user.getUserName(),
                null,
                grantedAuthorities
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
