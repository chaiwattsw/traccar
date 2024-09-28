package org.traccar.api.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.traccar.api.BaseResource;
import org.traccar.model.BoxDevice;
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

@Path("boxDevices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BoxDeviceResource extends BaseResource {

    @Inject
    private org.traccar.storage.Storage storage;

    private List<BoxDevice> loadBoxDevices() {
        try (InputStream inputStream = getClass().getResourceAsStream("/data/box_devices.json")) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(inputStream, new TypeReference<List<BoxDevice>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to load box devices", e);
        }
    }

    @GET
    public Response getBoxDevices() throws StorageException {
        List<BoxDevice> boxDevices = storage.getObjects(BoxDevice.class, new Request(new Columns.All()));
        return Response.ok(boxDevices).build();
    }

    @GET
    @Path("{id}")
    public Response getBoxDevice(@PathParam("id") long id) throws StorageException {
        BoxDevice boxDevice = storage.getObject(BoxDevice.class, new Request(
                new Columns.All(),
                new Condition.Equals("id", id)));

        if (boxDevice != null) {
            return Response.ok(boxDevice).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response addBoxDevice(BoxDevice entity) throws StorageException {
        storage.addObject(entity, new Request(new Columns.Exclude("id")));
        return Response.ok(entity).build();
    }

    @PUT
    @Path("{id}")
    public Response updateBoxDevice(@PathParam("id") long id, BoxDevice entity) throws StorageException {
        entity.setId(id);
        storage.updateObject(entity, new Request(
                new Columns.Exclude("id"),
                new Condition.Equals("id", id)));
        return Response.ok(entity).build();
    }

    @DELETE
    @Path("{id}")
    public Response removeBoxDevice(@PathParam("id") long id) throws StorageException {
        storage.removeObject(BoxDevice.class, new Request(
                new Condition.Equals("id", id)));
        return Response.noContent().build();
    }

    @POST
    @Path("init")
    public Response initializeBoxDevices() throws StorageException {
        List<BoxDevice> boxDevices = loadBoxDevices();
        for (BoxDevice boxDevice : boxDevices) {
            storage.addObject(boxDevice, new Request(new Columns.All()));
        }
        return Response.ok("Box devices initialized from JSON file").build();
    }
}