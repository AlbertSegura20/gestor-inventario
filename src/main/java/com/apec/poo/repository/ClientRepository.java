package com.apec.poo.repository;

import com.apec.poo.entities.Client;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;


@ApplicationScoped
public class ClientRepository implements PanacheRepository<Client> {

    @Transactional
    public void updateClient(Client client, String clientNameUpdated, String lastNameUpdated, String emailUpdated, String phoneUpdated) {
        update("name = ?1, lastName = ?2, email = ?3, phoneNumber = ?4 WHERE id = ?5",
                clientNameUpdated, lastNameUpdated, emailUpdated, phoneUpdated, client.getId()
        );

    }

    @Transactional
    public void deleteClientById(Long id) {
        delete("id", id);
    }
}
