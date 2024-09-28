package org.traccar.api.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.traccar.api.BaseResource;
import org.traccar.config.Config;
import org.traccar.model.Province;
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

@Path("provinces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProvincesResource extends BaseResource {

    @Inject
    private org.traccar.storage.Storage storage;

    @Inject
    private Config config;

    private List<Province> loadProvinces() {
        try (InputStream inputStream = getClass().getResourceAsStream("/data/provinces.json")) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(inputStream, new TypeReference<List<Province>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to load provinces", e);
        }
    }

    @GET
    public Response getProvinces(
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("100") int limit,
            @QueryParam("fromJson") @DefaultValue("false") boolean fromJson) throws StorageException {

        if (fromJson) {
            List<Province> provinces = loadProvinces();
            return Response.ok(provinces.subList(offset, Math.min(offset + limit, provinces.size()))).build();
        } else {
            Collection<Province> provinces = storage.getObjects(Province.class, new Request(
                    new Columns.All()));
            return Response.ok(provinces).build();
        }
    }

    @GET
    @Path("{id}")
    public Response getProvince(@PathParam("id") long id) throws StorageException {
        Province province = storage.getObject(Province.class, new Request(
                new Columns.All(),
                new Condition.Equals("id", id)));

        if (province != null) {
            return Response.ok(province).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response addProvince(Province entity) throws StorageException {
        storage.addObject(entity, new Request(new Columns.Exclude("id")));
        return Response.ok(entity).build();
    }

    @PUT
    @Path("{id}")
    public Response updateProvince(@PathParam("id") long id, Province entity) throws StorageException {
        entity.setId(id);
        storage.updateObject(entity, new Request(
                new Columns.Exclude("id"),
                new Condition.Equals("id", id)));

        return Response.ok(entity).build();
    }

    @DELETE
    @Path("{id}")
    public Response removeProvince(@PathParam("id") long id) throws StorageException {
        storage.removeObject(Province.class, new Request(
                new Condition.Equals("id", id)));

        return Response.noContent().build();
    }

    @GET
    @Path("count")
    public Response getProvincesCount(@QueryParam("fromJson") @DefaultValue("false") boolean fromJson)
            throws StorageException {
        if (fromJson) {
            long count = loadProvinces().size();
            return Response.ok(count).build();
        } else {
            long count = storage.getObjects(Province.class, new Request(new Columns.All())).size();
            return Response.ok(count).build();
        }
    }

    @POST
    @Path("init")
    public Response initializeProvinces() throws StorageException {
        List<Province> provinces = loadProvinces();
        for (Province province : provinces) {
            storage.addObject(province, new Request(new Columns.All()));
        }
        return Response.ok("Provinces initialized from JSON file").build();
    }
}