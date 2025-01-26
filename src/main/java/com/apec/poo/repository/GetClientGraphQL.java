package com.apec.poo.repository;

import com.apec.poo.entities.Client;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;

@GraphQLApi
public class GetClientGraphQL {

    @Inject
    ClientRepository clientRepository;

    @Query("getClient")
    @Transactional
    public List<Client> getClient() {
        // Assuming there's a repository or service to fetch client data by ID
        return clientRepository.findAll().list();
    }

}
