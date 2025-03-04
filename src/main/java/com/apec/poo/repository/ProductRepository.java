package com.apec.poo.repository;

import com.apec.poo.entities.Product;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {

    @Transactional
    public void updateProduct(Long productId, String name, BigDecimal price, Integer quantity, String description) {
        update("name = ?1, price = ?2, quantity = ?3, description = ?4 WHERE id = ?5",
                name, price, quantity, description, productId);
    }


    @Transactional
    public void deleteProductById(Long id) {
        delete("id", id);
    }
}
