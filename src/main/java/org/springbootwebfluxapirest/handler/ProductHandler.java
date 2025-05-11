package org.springbootwebfluxapirest.handler;

import org.springbootwebfluxapirest.models.documents.Product;
import org.springbootwebfluxapirest.models.services.ProductServices;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class ProductHandler {
    private ProductServices productServices;

    public ProductHandler(ProductServices productServices) {
        this.productServices = productServices;
    }

    public Mono<ServerResponse> list(ServerRequest serverRequest) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(productServices.findAll(), Product.class);
    }


    public Mono<ServerResponse> see(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return productServices.findById(id).flatMap(product -> ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromObject(product))
        ).switchIfEmpty(ServerResponse.notFound().build());

    }


    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        Mono<Product> productMono = serverRequest.bodyToMono(Product.class);

        return productMono.flatMap(product -> {
            if (product.getCreation() == null) {
                product.setCreation(new Date());
            }
            return productServices.save(product);
        }).flatMap(product -> ServerResponse.created(URI.create("/api/v2/".concat(product.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromObject(product)));
    }



}
