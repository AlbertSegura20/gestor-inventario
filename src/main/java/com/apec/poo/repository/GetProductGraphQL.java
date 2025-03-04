package com.apec.poo.repository;

import com.apec.poo.entities.Product;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;

@GraphQLApi
public class GetProductGraphQL {

    private final ProductRepository productRepository;

    @Inject
   public GetProductGraphQL(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Query("getProduct")
    @Transactional
    public List<Product> getProduct() {
        return productRepository.findAll().list();
    }

}
