package com.apec.poo;
import com.apec.poo.entities.Client;
import com.apec.poo.repository.ClientRepository;
import com.apec.poo.view.ClientGridView;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;

@Path("/hello")
public class GreetingResource {

    @Inject
    ClientRepository clientRepository;
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST";
    }

    @GET()
    @Path("/hi")
//    @Query("Hello")
    public String Hello(){

        getAllClients();
        return "Hello from GraphQL";
    }


    private void getAllClients(){

        List<Client> clients = clientRepository.findAll().list();
//        x.forEach(z -> System.out.println(z.toString()));

        clients.forEach(z -> System.out.println(z.getEmail()));
    }

}

