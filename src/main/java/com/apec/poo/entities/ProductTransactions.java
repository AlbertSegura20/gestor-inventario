package com.apec.poo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
public class ProductTransactions extends AbstractEntity {

    @JoinColumn(name = "product_id" )
    @ManyToOne
    private Product product;

    @JoinColumn(name = "user_id" )
    @ManyToOne
    private Client userId;
    private Long quantity;
    private BigDecimal price;
    private LocalDate date;

    public Product getProductId() {
        return product;
    }
    public void setProductId(Product productId) {
        this.product = productId;
    }
    public Client getUserId() {
        return userId;
    }
    public void setUserId(Client userId) {
        this.userId = userId;
    }
    public Long getQuantity() {
        return quantity;
    }
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }


}
