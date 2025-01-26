package com.apec.poo.entities;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Product extends AbstractEntity {

    private String name;
    private BigDecimal price;
    private int quantity;
    private String description;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;
    private LocalDate registryDate;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public ProductStatus getStatus() {
        return status;
    }
    public void setStatus(ProductStatus status) {
        this.status = status;
    }
    public LocalDate getRegistryDate() {
        return registryDate;
    }
    public void setRegistryDate(LocalDate registryDate) {
        this.registryDate = registryDate;
    }


}
