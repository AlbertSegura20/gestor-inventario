package com.apec.poo.repository;

import com.apec.poo.entities.Transaction;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class TransactionRepository implements PanacheRepository<Transaction> {

    public Optional<Transaction> getTransactionByClient(Long clientId) {
        return find("client.id", clientId).firstResultOptional();
    }

    public boolean existsTransactionByProduct(Long productId) {
        return find("product.id", productId).firstResultOptional().isPresent();
    }


}
