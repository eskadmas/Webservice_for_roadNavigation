package it.polito.dp2.RNS.sol3.service.resource;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import it.polito.dp2.RNS.sol3.jaxb.*;
import it.polito.dp2.RNS.sol3.service.rnsService.RnsService;

import it.polito.dp2.RNS.lab2.BadStateException;
import it.polito.dp2.RNS.lab2.PathFinderException;
import it.polito.dp2.RNS.lab2.ServiceException;
import it.polito.dp2.RNS.lab2.UnknownIdException;
import it.polito.dp2.RNS.sol3.service.dataAndNeo4j.EntranceRefusedException;
import it.polito.dp2.RNS.sol3.service.dataAndNeo4j.UnknownPlaceException;
import it.polito.dp2.RNS.sol3.service.dataAndNeo4j.WrongPlaceException;

@Path("/entity")
@Api(value = "/entity")
public class RnsResources {
	public UriInfo uriInfo;
	private RnsService service = new RnsService();
	
	public RnsResources(@Context UriInfo uriInfo){
		this.uriInfo = uriInfo;
		
	}	
	@GET
    @ApiOperation(value = "getEntity", notes = "reads main resource"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Entity getEntity() {
		Entity entity = new Entity();
		UriBuilder root = uriInfo.getAbsolutePathBuilder();
		entity.setSelf(root.toTemplate());		
		UriBuilder places = root.clone().path("places");
		entity.setPlaces(places.toTemplate());
		entity.setTrackedVehicles((root.clone().path("vehicles").toTemplate()));
		
		return entity;		
	}
	
	@GET
	@Path("/places")
    @ApiOperation(value = "getPlaces", notes = "searches places"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Places getPlaces() {
		Places places = service.getPlaces();
		
		return places;
	}
	
	@POST
	@Path("/vehicles")
    @Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response addVehicle(Vehicle vehicle) {
		String id = vehicle.getId();
    	UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(id);
    	URI self = builder.build();
    	vehicle.setSelf(self.toString());
    	
    	it.polito.dp2.RNS.sol2.jaxb.Path nodeList = new it.polito.dp2.RNS.sol2.jaxb.Path();
    	List<String> suggestedPath = new ArrayList<>();
    	
    	try {
    		suggestedPath = service.addVehicle(id, vehicle);
		} catch (UnknownPlaceException e) {
			return Response.status(304).entity("Uknown Place").build();
		}catch(WrongPlaceException wpe){
			return Response.status(305).entity("Wrong Place").build();
		}catch (EntranceRefusedException e) {
			return Response.status(407).entity("Entrance Refused").build();
		}catch(BadStateException | ServiceException e){
			return Response.status(500).entity("Service Exception").build();
		} catch (UnknownIdException e) {
			e.printStackTrace();
		} 
    	nodeList.getNodes().addAll(suggestedPath);
    	
		return Response.created(self).entity(nodeList).build();
	}
	
	@GET
	@Path("/vehicles")
    @ApiOperation(value = "getVehicles", notes = "searches vehicles"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public TrackedVehicles getVehicles(@QueryParam("place") String place) {
		
		return service.getVehicles(place);	
	}
	
	@GET
	@Path("/vehicles/{id}")
    @ApiOperation(value = "getVehicle", notes = "finds a vehicle"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Vehicle getVehicle(@PathParam("id") String id) {
		
		return service.getVehicle(id);
	}
	
	@PUT
	@Path("/vehicles/{id}")
    @ApiOperation(value = "updateSuggestedPath", notes = "update vehicles suggested Path"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		})
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response moveVehicle(@PathParam("id") String id, @QueryParam("newPlace") String newPlace) throws UnknownIdException, BadStateException, ServiceException, PathFinderException {
		
		it.polito.dp2.RNS.sol2.jaxb.Path nodeList = new it.polito.dp2.RNS.sol2.jaxb.Path();
    	List<String> newPath = new ArrayList<>();
		
    	try {
			newPath = service.moveVehicle(id, newPlace);
		} catch (UnknownPlaceException upe) {
			return Response.status(304).entity("Uknown Place").build();
		}catch(WrongPlaceException wpe) {
			return Response.status(305).entity("Wrong Place").build();
		}catch(UnknownIdException uie){
			return Response.status(309).entity("Path Not Changed").build();
		}
    	nodeList.getNodes().addAll(newPath);
		
		return Response.accepted().entity(nodeList).build();
	}
	
	@PUT
	@Path("/vehicles/{id}")
    @ApiOperation(value = "changeState", notes = "update vehicles state"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Vehicle changeState(@PathParam("id") String id, @QueryParam("newState") VehicleStateType newState) {
		
		return service.changeState(id, newState); 
	}
	
	@DELETE
	@Path("/vehicles/{id}")
    @ApiOperation(value = "removeVehicle", notes = "removes a single vehicle"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
	public Response removeVehicle(@PathParam("id") String id, @QueryParam("outGate") String outGate) {
		
		boolean removed = false;
		
		try {
			
			removed = service.removeVehicle(id, outGate);
			
		} catch (UnknownPlaceException upe) {
			return Response.status(304).entity("Uknown Place").build();
		}catch(WrongPlaceException wpe) {
			return Response.status(305).entity("Wrong Place").build();	
		}
		
		if (removed==true)
			return Response.status(200).entity("Ok").build(); 
			
		else
			return Response.status(400).entity("Vehicle can not be removed").build();
	}
}
