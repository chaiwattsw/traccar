package org.traccar.api.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.traccar.api.BaseResource;
import org.traccar.model.PayType;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.InputStream;
import java.util.List;

@Path("payTypes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PayTypeResource extends BaseResource {

    @Inject
    private org.traccar.storage.Storage storage;

    private List<PayType> loadPayTypes() {
        try (InputStream inputStream = getClass().getResourceAsStream("/data/pay_types.json")) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(inputStream, new TypeReference<List<PayType>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to load pay types", e);
        }
    }

    @GET
    public Response getPayTypes() {
        List<PayType> payTypes = loadPayTypes();
        return Response.ok(payTypes).build();
    }

    @GET
    @Path("{code}")
    public Response getPayType(@PathParam("code") String code) {
        List<PayType> payTypes = loadPayTypes();
        PayType payType = payTypes.stream()
                .filter(pt -> pt.getCode().equals(code))
                .findFirst()
                .orElse(null);

        if (payType != null) {
            return Response.ok(payType).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("init")
    public Response initializePayTypes() throws StorageException {
        List<PayType> payTypes = loadPayTypes();
        for (PayType payType : payTypes) {
            storage.addObject(payType, new Request(new Columns.All()));
        }
        return Response.ok("Pay types initialized from JSON file").build();
    }

    // The following methods are included for completeness, but may not be used if
    // the pay types are fixed

    @POST
    public Response addPayType(PayType entity) throws StorageException {
        storage.addObject(entity, new Request(new Columns.All()));
        return Response.ok(entity).build();
    }

    @PUT
    @Path("{code}")
    public Response updatePayType(@PathParam("code") String code, PayType entity) throws StorageException {
        entity.setCode(code);
        storage.updateObject(entity, new Request(
                new Columns.All(),
                new Condition.Equals("code", code)));
        return Response.ok(entity).build();
    }

    @DELETE
    @Path("{code}")
    public Response removePayType(@PathParam("code") String code) throws StorageException {
        storage.removeObject(PayType.class, new Request(
                new Condition.Equals("code", code)));
        return Response.noContent().build();
    }
}