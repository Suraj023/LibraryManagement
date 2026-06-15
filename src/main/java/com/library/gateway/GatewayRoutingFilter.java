package com.library.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GatewayRoutingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(GatewayRoutingFilter.class);
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final RateLimiterService rateLimiterService;
    private final List<RouteDefinition> routes;

    public GatewayRoutingFilter(RateLimiterService rateLimiterService, List<RouteDefinition> routes) {
        this.rateLimiterService = rateLimiterService;
        this.routes = routes;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Only apply gateway logic to /api/** paths
        if (!path.startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        RouteDefinition route = matchRoute(path);
        if (route == null) {
            log.warn("Gateway: no route for {} {}", request.getMethod(), path);
            writeError(response, HttpStatus.NOT_FOUND, "No route defined for: " + path);
            return;
        }

        String clientIp = resolveClientIp(request);
        String rateLimitKey = clientIp + ":" + route.getId();

        if (!rateLimiterService.tryConsume(rateLimitKey, route.getRateLimit())) {
            log.warn("Gateway: rate limit exceeded for IP={} route={}", clientIp, route.getId());
            response.setHeader("X-RateLimit-Limit", String.valueOf(route.getRateLimit()));
            response.setHeader("Retry-After", "60");
            writeError(response, HttpStatus.TOO_MANY_REQUESTS,
                    "Rate limit exceeded (" + route.getRateLimit() + " req/min). Retry after 60s.");
            return;
        }

        // Attach metadata for downstream use (e.g. audit logging)
        request.setAttribute("gateway.routeId", route.getId());
        response.setHeader("X-Route-Id", route.getId());
        response.setHeader("X-RateLimit-Limit", String.valueOf(route.getRateLimit()));

        log.debug("Gateway: {} {} → route={} client={}", request.getMethod(), path, route.getId(), clientIp);
        chain.doFilter(request, response);
    }

    private RouteDefinition matchRoute(String path) {
        return routes.stream()
                .filter(r -> PATH_MATCHER.match(r.getPathPattern(), path))
                .findFirst()
                .orElse(null);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void writeError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(MAPPER.writeValueAsString(
                Map.of("status", status.value(), "error", message)
        ));
    }
}