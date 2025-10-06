package com.zapaticorp.gateway.config;

import com.zapaticorp.gateway.filter.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Value("${URI_AUTH}")
    private String authUri;

    @Value("${URI_VIAJERO}")
    private String viajeroUri;

    @Value("${URI_ACTIVIDAD}")
    private String actividadUri;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder, JwtAuthFilter jwtAuthFilter) {
        return builder.routes()

                // Ruta al microservicio de autenticación
                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f.addRequestHeader("X-Gateway", "Gateway-auth"))
                        .uri(authUri))

                // Ruta al microservicio de viajeros
                .route("viajero-service", r -> r.path("/api/viajeros/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter)
                                .addRequestHeader("X-Gateway", "Gateway-Viajero"))
                        .uri(viajeroUri))

                // Ruta al microservicio de actividades
                .route("actividad-service", r -> r
                        .path("/api/actividades/**")
                        .or().path("/api/ubicaciones/**")
                        .or().path("/api/tipoActividades/**")
                        .or().path("/api/viajes/**")
                        .filters(f -> f
                                .filter(jwtAuthFilter)
                                .addRequestHeader("X-Gateway", "Gateway-Actividades"))
                        .uri(actividadUri))

                .build();
    }
}
