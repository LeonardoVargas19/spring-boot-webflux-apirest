package org.springbootwebfluxapirest.controller;

import org.springbootwebfluxapirest.models.documents.Product;
import org.springbootwebfluxapirest.models.services.ProductServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private ProductServices productServices;

    public ProductController(ProductServices productServices) {
        this.productServices = productServices;
    }

//    @GetMapping
//    public Flux<Product> listTo() {
//        return productServices.findAll();
//    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Product>>> lisTo2() {
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(productServices.findAll())
        );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> getDetail(@PathVariable String id) {
        return productServices.findById(id).map(product -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(product))
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> creatProduct(@Valid @RequestBody Mono<Product> productMono) {
        Map<String, Object> response = new HashMap<>();

        return productMono.flatMap(product -> {
            if (product.getCreation() == null) {
                product.setCreation(new Date());
            }
            return productServices.save(product).map(product1 -> {
                response.put("product", product1);
                response.put("message", "Product created successfully");
                response.put("timeStamp", new Date());

                return ResponseEntity
                        .created(URI.create("/api/product/".concat(product1.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            });
        }).onErrorResume(WebExchangeBindException.class, e -> {
            List<String> errors = e.getFieldErrors().stream()
                    .map(field -> "El campo '" + field.getField() + "' " + field.getDefaultMessage())
                    .toList();

            response.put("errors", errors);
            response.put("timeStamp", new Date());
            response.put("status", HttpStatus.BAD_REQUEST.value());

            return Mono.just(ResponseEntity.badRequest().body(response));
        });
    }


    @PutMapping("/{id}")
    public Mono<ResponseEntity<Product>> edith(@RequestBody Product product, @PathVariable String id) {
        return productServices.findById(id).flatMap(product1 -> {
            product1.setId(product.getId());
            product1.setName(product.getName());
            product1.setPrice(product.getPrice());
            product1.setCategory(product.getCategory());
            return productServices.save(product1);
        }).map(product1 ->
                ResponseEntity.created(
                        URI.create("/api/product/".concat(product1.getId()))
                ).contentType(MediaType.APPLICATION_JSON).body(product)
        ).defaultIfEmpty(ResponseEntity.notFound().build());


    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return productServices.findById(id).flatMap(product -> {
            return productServices.delete(product).then(
                    Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
        }).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }
}
