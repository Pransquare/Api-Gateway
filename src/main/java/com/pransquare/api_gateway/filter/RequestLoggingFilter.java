package com.pransquare.api_gateway.filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = (exchange.getRequest().getMethod() != null)
                ? exchange.getRequest().getMethod().name()
                : "UNKNOWN";


        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        String serviceId = "UNKNOWN";
        if (route != null) {
            // route.getId() looks like "ReactiveCompositeDiscoveryClient_NEMS"
            String routeId = route.getId();
            if (routeId.contains("_")) {
                serviceId = routeId.substring(routeId.lastIndexOf("_") + 1);
            } else {
                serviceId = routeId;
            }
        }


        log.info("➡️ Incoming request: {} {} -> Service: {}", method, path, serviceId);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1; // run first
    }
}
