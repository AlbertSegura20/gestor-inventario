package com.apec.poo.repository;

import com.apec.poo.entities.Product;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;

@GraphQLApi
public class GetProductGraphQL {

    @Inject
    ProductRepository productRepository;

    @Query("getProduct")
    @Transactional
    public List<Product> getProduct() {
        return productRepository.findAll().list();
    }

}
