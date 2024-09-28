package org.traccar.api.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.traccar.api.BaseResource;
import org.traccar.config.Config;
import org.traccar.model.CarBrand;
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

@Path("brands")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CarBrandResource extends BaseResource {

    @Inject
    private org.traccar.storage.Storage storage;

    @Inject
    private Config config;

    private List<CarBrand> loadCarBrands() {
        try (InputStream inputStream = getClass().getResourceAsStream("/data/car_brands.json")) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(inputStream, new TypeReference<List<CarBrand>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to load car brands", e);
        }
    }

    @GET
    public Response getBrands(
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("100") int limit,
            @QueryParam("fromJson") @DefaultValue("false") boolean fromJson) throws StorageException {

        if (fromJson) {
            List<CarBrand> brands = loadCarBrands();
            return Response.ok(brands.subList(offset, Math.min(offset + limit, brands.size()))).build();
        } else {
            Collection<CarBrand> brands = storage.getObjects(CarBrand.class, new Request(
                    new Columns.All()));
            return Response.ok(brands).build();
        }
    }

    @GET
    @Path("{id}")
    public Response getBrand(@PathParam("id") long id) throws StorageException {
        CarBrand brand = storage.getObject(CarBrand.class, new Request(
                new Columns.All(),
                new Condition.Equals("id_brand", id)));

        if (brand != null) {
            return Response.ok(brand).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response addBrand(CarBrand entity) throws StorageException {
        storage.addObject(entity, new Request(new Columns.Exclude("id_brand")));
        return Response.ok(entity).build();
    }

    @PUT
    @Path("{id}")
    public Response updateBrand(@PathParam("id") long id, CarBrand entity) throws StorageException {
        entity.setId(id);
        storage.updateObject(entity, new Request(
                new Columns.Exclude("id_brand"),
                new Condition.Equals("id_brand", id)));

        return Response.ok(entity).build();
    }

    @DELETE
    @Path("{id}")
    public Response removeBrand(@PathParam("id") long id) throws StorageException {
        storage.removeObject(CarBrand.class, new Request(
                new Condition.Equals("id_brand", id)));

        return Response.noContent().build();
    }

    @GET
    @Path("count")
    public Response getBrandsCount(@QueryParam("fromJson") @DefaultValue("false") boolean fromJson)
            throws StorageException {
        if (fromJson) {
            long count = loadCarBrands().size();
            return Response.ok(count).build();
        } else {
            long count = storage.getObjects(CarBrand.class, new Request(new Columns.All())).size();
            return Response.ok(count).build();
        }
    }

    @POST
    @Path("init")
    public Response initializeBrands() throws StorageException {
        List<CarBrand> brands = loadCarBrands();
        for (CarBrand brand : brands) {
            storage.addObject(brand, new Request(new Columns.All()));
        }
        return Response.ok("Brands initialized from JSON file").build();
    }
}