package com.postech.msproducts.repository;

import com.postech.msproducts.domain.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends MongoRepository<Product, UUID> {

    Optional<Product> findById(UUID id);

    //para criar querys natives = @query() com linguagem do mongo
}