package com.gaurav.cloudgateway.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String token = extractToken(exchange.getRequest().getHeaders().getFirst("Authorization"));

            boolean hasRequiredScope=true;
            boolean hasRequiredRoles=true;

            if (config.requiredScope!=null) {
                hasRequiredScope = checkRequiredScope(token, config.requiredScope);
            }
            if(config.requiredRoles!=null) {
                hasRequiredRoles = checkRequiredRole(token,config.requiredRoles);
            }

            if (hasRequiredScope&&hasRequiredRoles) {
                return chain.filter(exchange);
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        };
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    private boolean checkRequiredScope(String token, String requiredScope) {
        if (token != null) {
            DecodedJWT jwt = JWT.decode(token);
            Claim scopeClaim = jwt.getClaim("scope");
            if (scopeClaim != null && !scopeClaim.isNull()) {
                String[] scopes = scopeClaim.asArray(String.class);
                for (String scope : scopes) {
                    if (requiredScope.contains(scope)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public boolean checkRequiredRole(String token,String requiredRole) {
        if (token != null) {
            DecodedJWT jwt = JWT.decode(token);
            Claim scopeClaim = jwt.getClaim("authorities");
            if (scopeClaim != null && !scopeClaim.isNull()) {
                String[] scopes = scopeClaim.asArray(String.class);
                for (String scope : scopes) {
                    if (requiredRole.contains(scope)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Setter
    @Getter
    public static class Config {
        private String requiredScope;
        private String requiredRoles;

    }
}