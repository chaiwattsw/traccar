package org.traccar.api.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.traccar.api.BaseResource;
import org.traccar.model.Cell;
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

@Path("cellularNetworks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CellResource extends BaseResource {

    @Inject
    private org.traccar.storage.Storage storage;

    private List<Cell> loadCells() {
        try (InputStream inputStream = getClass().getResourceAsStream("/data/cellular_networks.json")) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(inputStream, new TypeReference<List<Cell>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to load cellular_networks", e);
        }
    }

    @GET
    public Response getCells() throws StorageException {
        List<Cell> cells = storage.getObjects(Cell.class, new Request(new Columns.All()));
        return Response.ok(cells).build();
    }

    @GET
    @Path("{code}")
    public Response getCell(@PathParam("code") String code) throws StorageException {
        Cell cell = storage.getObject(Cell.class, new Request(
                new Columns.All(),
                new Condition.Equals("code", code)));

        if (cell != null) {
            return Response.ok(cell).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response addCell(Cell entity) throws StorageException {
        storage.addObject(entity, new Request(new Columns.All()));
        return Response.ok(entity).build();
    }

    @PUT
    @Path("{code}")
    public Response updateCell(@PathParam("code") String code, Cell entity) throws StorageException {
        entity.setCode(code);
        storage.updateObject(entity, new Request(
                new Columns.All(),
                new Condition.Equals("code", code)));
        return Response.ok(entity).build();
    }

    @DELETE
    @Path("{code}")
    public Response removeCell(@PathParam("code") String code) throws StorageException {
        storage.removeObject(Cell.class, new Request(
                new Condition.Equals("code", code)));
        return Response.noContent().build();
    }

    @POST
    @Path("init")
    public Response initializeCells() throws StorageException {
        List<Cell> cells = loadCells();
        for (Cell cell : cells) {
            storage.addObject(cell, new Request(new Columns.All()));
        }
        return Response.ok("Cells initialized from JSON file").build();
    }
}