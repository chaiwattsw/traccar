package org.traccar.api.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.traccar.api.BaseResource;
import org.traccar.model.AssetType;
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

@Path("assetTypes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AssetTypeResource extends BaseResource {

    @Inject
    private org.traccar.storage.Storage storage;

    private List<AssetType> loadAssetTypes() {
        try (InputStream inputStream = getClass().getResourceAsStream("/data/asset_types.json")) {
            ObjectMapper mapper = new ObjectMapper();
            List<AssetType> assetTypes = mapper.readValue(inputStream, new TypeReference<List<AssetType>>() {
            });
            for (AssetType assetType : assetTypes) {
                assetType.setId(assetType.getId());
                assetType.setName(assetType.getName());
                assetType.setDetail(assetType.getDetail());
            }
            return assetTypes;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load asset types", e);
        }
    }

    @GET
    public Response getAssetTypes() throws StorageException {
        List<AssetType> assetTypes = storage.getObjects(AssetType.class, new Request(new Columns.All()));
        return Response.ok(assetTypes).build();
    }

    @GET
    @Path("{id}")
    public Response getAssetType(@PathParam("id") long id) throws StorageException {
        AssetType assetType = storage.getObject(AssetType.class, new Request(
                new Columns.All(),
                new Condition.Equals("id", id)));

        if (assetType != null) {
            return Response.ok(assetType).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response addAssetType(AssetType entity) throws StorageException {
        storage.addObject(entity, new Request(new Columns.Exclude("id")));
        return Response.ok(entity).build();
    }

    @PUT
    @Path("{id}")
    public Response updateAssetType(@PathParam("id") long id, AssetType entity) throws StorageException {
        entity.setId(id);
        storage.updateObject(entity, new Request(
                new Columns.Exclude("id"),
                new Condition.Equals("id", id)));
        return Response.ok(entity).build();
    }

    @DELETE
    @Path("{id}")
    public Response removeAssetType(@PathParam("id") long id) throws StorageException {
        storage.removeObject(AssetType.class, new Request(
                new Condition.Equals("id", id)));
        return Response.noContent().build();
    }

    @POST
    @Path("init")
    public Response initializeAssetTypes() throws StorageException {
        List<AssetType> assetTypes = loadAssetTypes();
        for (AssetType assetType : assetTypes) {
            storage.addObject(assetType, new Request(new Columns.All()));
        }
        return Response.ok("Asset types initialized from JSON file").build();
    }
}