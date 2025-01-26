package com.apec.poo.repository;

import com.apec.poo.entities.Client;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;


@ApplicationScoped
public class ClientRepository implements PanacheRepository<Client> {
}
