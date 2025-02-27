package com.apec.poo.entities;

import jakarta.persistence.Entity;

import java.util.Objects;
import java.util.StringJoiner;

@Entity
public class Client extends AbstractEntity{

    private String name;
    private String lastName;
    private String email;
    private String phoneNumber;

    public String getName() {
        return name;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Client.class.getSimpleName() + "[", "]")
                .add("id='" + getId() +"'")
                .add("name='" + name + "'")
                .add("lastName='" + lastName + "'")
                .add("email='" + email + "'")
                .add("phoneNumber='" + phoneNumber + "'")
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Client client)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(name, client.name) && Objects.equals(lastName, client.lastName) && Objects.equals(email,
                client.email) && Objects.equals(phoneNumber, client.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, lastName, email, phoneNumber);
    }
}
