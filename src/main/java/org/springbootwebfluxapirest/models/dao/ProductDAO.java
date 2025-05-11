package org.springbootwebfluxapirest.models.dao;

import org.springbootwebfluxapirest.models.documents.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ProductDAO extends ReactiveMongoRepository<Product,String> {


}
