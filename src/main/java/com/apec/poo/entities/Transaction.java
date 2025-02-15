package com.apec.poo.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Transaction extends AbstractEntity {

    @JoinColumn(name = "client_id" )
    @ManyToOne
    private Client client;
    @Enumerated(EnumType.STRING)
    private TransactionsStatus status;
    @JoinColumn(name = "product_id" )
    @ManyToOne
    private Product product;
    private LocalDateTime transactionDate;
    private BigDecimal totalPrice;
    private Double quantityTransaction;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public TransactionsStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionsStatus status) {
        this.status = status;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getQuantityTransaction() {
        return quantityTransaction;
    }

    public void setQuantityTransaction(Double quantityTransaction) {
        this.quantityTransaction = quantityTransaction;
    }
}
