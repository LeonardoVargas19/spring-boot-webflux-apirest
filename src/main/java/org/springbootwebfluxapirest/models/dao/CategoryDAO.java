package org.springbootwebfluxapirest.models.dao;


import org.springbootwebfluxapirest.models.documents.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoryDAO extends ReactiveMongoRepository<Category, String> {
}
