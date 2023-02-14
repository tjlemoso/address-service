package org.acme.com.resource;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.acme.com.model.Address;
import org.acme.com.repository.AddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


import io.quarkus.panache.common.Sort;

@Path("address")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class AddressRepositoryResource {

    @Inject
    AddressRepository addressRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressRepositoryResource.class.getName());

    @GET
    public List<Address> get() {
        return addressRepository.listAll(Sort.by("address"));
    }

    @GET
    @Path("{id}")
    public Address getSingle(Long id) {
      Address entity = addressRepository.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Address with id of " + id + " does not exist.", 404);
        }
        return entity;
    }

    @POST
    @Transactional
    public Response create(Address address) {
        if (address.getAddress() == "") {
            throw new WebApplicationException("Address was invalidly set on request.", 422);
        }
        addressRepository.persist(address);
        return Response.ok(address).status(201).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Address update(Long id, Address address) {
        if (address.getAddress() == null) {
            throw new WebApplicationException("Address was not set on request.", 422);
        }

        Address entity = addressRepository.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Address with id of " + id + " does not exist.", 404);
        }

        entity.setAddress(address.getAddress());
        entity.setAddress2(address.getAddress2());
        entity.setCity(address.getCity());
        entity.setCountry(address.getCountry());
        entity.setNumber(address.getNumber());
        entity.setZipCode(address.getZipCode());       
        
        return entity;
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(Long id) {
      Address entity = addressRepository.findById(id);
      if (entity == null) {
          throw new WebApplicationException("Address with id of " + id + " does not exist.", 404);
      }

      addressRepository.delete(entity);
      return Response.status(204).build();
    }

    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Inject
        ObjectMapper objectMapper;

        @Override
        public Response toResponse(Exception exception) {
            LOGGER.error("Failed to handle request", exception);

            int code = 500;
            if (exception instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }

            ObjectNode exceptionJson = objectMapper.createObjectNode();
            exceptionJson.put("exceptionType", exception.getClass().getName());
            exceptionJson.put("code", code);

            if (exception.getMessage() != null) {
                exceptionJson.put("error", exception.getMessage());
            }

            return Response.status(code)
                    .entity(exceptionJson)
                    .build();
        }

    }
}