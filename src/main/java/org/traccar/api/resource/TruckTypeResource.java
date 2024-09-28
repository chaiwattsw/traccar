package org.traccar.api.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.traccar.api.BaseResource;
import org.traccar.config.Config;
import org.traccar.model.TruckType;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

@Path("truckTypes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TruckTypeResource extends BaseResource {

    @Inject
    private org.traccar.storage.Storage storage;

    @Inject
    private Config config;

    private List<TruckType> loadTruckTypes() {
        try (InputStream inputStream = getClass().getResourceAsStream("/data/truck_types.json")) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(inputStream, new TypeReference<List<TruckType>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to load truck types", e);
        }
    }

    @GET
    public Response getTruckTypes(
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("100") int limit,
            @QueryParam("fromJson") @DefaultValue("false") boolean fromJson) throws StorageException {

        if (fromJson) {
            List<TruckType> types = loadTruckTypes();
            return Response.ok(types.subList(offset, Math.min(offset + limit, types.size()))).build();
        } else {
            Collection<TruckType> types = storage.getObjects(TruckType.class, new Request(
                    new Columns.All()));
            return Response.ok(types).build();
        }
    }

    @GET
    @Path("{id}")
    public Response getTruckType(@PathParam("id") long id) throws StorageException {
        TruckType type = storage.getObject(TruckType.class, new Request(
                new Columns.All(),
                new Condition.Equals("id", id)));

        if (type != null) {
            return Response.ok(type).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response addTruckType(TruckType entity) throws StorageException {
        storage.addObject(entity, new Request(new Columns.Exclude("id")));
        return Response.ok(entity).build();
    }

    @PUT
    @Path("{id}")
    public Response updateTruckType(@PathParam("id") long id, TruckType entity) throws StorageException {
        entity.setId(id);
        storage.updateObject(entity, new Request(
                new Columns.Exclude("id"),
                new Condition.Equals("id", id)));

        return Response.ok(entity).build();
    }

    @DELETE
    @Path("{id}")
    public Response removeTruckType(@PathParam("id") long id) throws StorageException {
        storage.removeObject(TruckType.class, new Request(
                new Condition.Equals("id", id)));

        return Response.noContent().build();
    }

    @GET
    @Path("count")
    public Response getTruckTypesCount(@QueryParam("fromJson") @DefaultValue("false") boolean fromJson)
            throws StorageException {
        if (fromJson) {
            long count = loadTruckTypes().size();
            return Response.ok(count).build();
        } else {
            long count = storage.getObjects(TruckType.class, new Request(new Columns.All())).size();
            return Response.ok(count).build();
        }
    }

    @POST
    @Path("init")
    public Response initializeTruckTypes() throws StorageException {
        List<TruckType> types = loadTruckTypes();
        for (TruckType type : types) {
            storage.addObject(type, new Request(new Columns.All()));
        }
        return Response.ok("Truck types initialized from JSON file").build();
    }
}