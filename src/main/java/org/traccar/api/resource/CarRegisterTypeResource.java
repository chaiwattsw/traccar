package org.traccar.api.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.traccar.api.BaseResource;
import org.traccar.model.CarRegisterType;
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

@Path("carRegisterTypes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CarRegisterTypeResource extends BaseResource {

    @Inject
    private org.traccar.storage.Storage storage;

    private List<CarRegisterType> loadCarRegisterTypes() {
        try (InputStream inputStream = getClass().getResourceAsStream("/data/car_register_type_list.json")) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(inputStream, new TypeReference<List<CarRegisterType>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to load car register types", e);
        }
    }

    @GET
    public Response getCarRegisterTypes() throws StorageException {
        List<CarRegisterType> types = storage.getObjects(CarRegisterType.class, new Request(new Columns.All()));
        return Response.ok(types).build();
    }

    @GET
    @Path("{code}")
    public Response getCarRegisterType(@PathParam("code") String code) throws StorageException {
        CarRegisterType type = storage.getObject(CarRegisterType.class, new Request(
                new Columns.All(),
                new Condition.Equals("code", code)));

        if (type != null) {
            return Response.ok(type).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response addCarRegisterType(CarRegisterType entity) throws StorageException {
        storage.addObject(entity, new Request(new Columns.All()));
        return Response.ok(entity).build();
    }

    @PUT
    @Path("{code}")
    public Response updateCarRegisterType(@PathParam("code") String code, CarRegisterType entity)
            throws StorageException {
        entity.setCode(code);
        storage.updateObject(entity, new Request(
                new Columns.All(),
                new Condition.Equals("code", code)));
        return Response.ok(entity).build();
    }

    @DELETE
    @Path("{code}")
    public Response removeCarRegisterType(@PathParam("code") String code) throws StorageException {
        storage.removeObject(CarRegisterType.class, new Request(
                new Condition.Equals("code", code)));
        return Response.noContent().build();
    }

    @POST
    @Path("init")
    public Response initializeCarRegisterTypes() throws StorageException {
        List<CarRegisterType> types = loadCarRegisterTypes();
        for (CarRegisterType type : types) {
            storage.addObject(type, new Request(new Columns.All()));
        }
        return Response.ok("Car register types initialized from JSON file").build();
    }
}