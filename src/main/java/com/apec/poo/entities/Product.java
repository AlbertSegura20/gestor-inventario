package com.apec.poo.entities;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Product extends AbstractEntity {

    private String name;
    private BigDecimal price;
    private int quantity;
    private String description;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;
    private LocalDate registryDate;
    private String code;
    @Transient
    private String transientPrice;

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
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
        transientPrice = price.toString();
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Product product)) return false;
        if (!super.equals(o)) return false;
        return quantity == product.quantity && Objects.equals(name, product.name) && Objects.equals(price, product.price)
                && Objects.equals(description, product.description) && status == product.status
                && Objects.equals(registryDate, product.registryDate) && Objects.equals(code, product.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, price, quantity, description, status, registryDate, code);
    }

    public String getTransientPrice() {
        return transientPrice;
    }

    public void setTransientPrice(String transientPrice) {
        this.transientPrice = transientPrice;
    }

    public void loadPrice(){
        if(price != null){
            transientPrice = price.toString();
        }
    }
}
