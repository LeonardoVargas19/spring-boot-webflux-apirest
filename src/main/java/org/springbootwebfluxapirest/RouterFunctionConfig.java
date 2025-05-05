package org.springbootwebfluxapirest;

import org.springbootwebfluxapirest.handler.ProductHandler;
import org.springbootwebfluxapirest.models.documents.Product;
import org.springbootwebfluxapirest.models.services.ProductServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterFunctionConfig {
    private ProductServices productServices;

    public RouterFunctionConfig(ProductServices productServices) {
        this.productServices = productServices;
    }

    @Bean
    public RouterFunction<ServerResponse> routerFunction(ProductHandler productHandler) {
        return route(GET("/api/v2/product").or(GET("/api/v3/")), productHandler::list)
                .andRoute(GET("/api/v2/product/{id}"), productHandler::see)
                .andRoute(POST("/api/v2/product"), productHandler::create);
    }


}
