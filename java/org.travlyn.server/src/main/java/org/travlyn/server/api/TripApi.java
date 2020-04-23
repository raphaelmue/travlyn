package org.travlyn.server.api;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.travlyn.shared.model.api.ExecutionInfo;
import org.travlyn.shared.model.api.Rating;
import org.travlyn.shared.model.api.StopIdWrapper;
import org.travlyn.shared.model.api.Trip;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static org.travlyn.server.configuration.AuthenticationTokenFilter.REGISTERED_USER_ROLE;

@Api(value = "trip")
public interface TripApi {

    @ApiOperation(
            value = "Search for trips",
            nickname = "findTrip",
            notes = "",
            response = Trip.class,
            responseContainer = "List",
            authorizations = {@Authorization(value = "ApiKeyAuth")},
            tags = {"trip"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Trip.class, responseContainer = "List")})
    @GetMapping(
            value = "/trip",
            produces = {"application/json"})
    ResponseEntity<List<Trip>> findTrip(@NotNull @ApiParam(value = "Search query for trips", required = true, example = "New York") @Valid @RequestParam(value = "searchQuery") String searchQuery);

    @ApiOperation(
            value = "Generate a Trip",
            nickname = "generateTrip",
            notes = "",
            response = Trip.class,
            authorizations = {@Authorization(value = "TokenAuth")},
            tags = {"trip"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Trip.class),
            @ApiResponse(code = 401, message = "You are not authorized to perform this action"),
            @ApiResponse(code = 404, message = "One of the provided IDs is not valid"),
            @ApiResponse(code = 500, message = "Trip could not be generated")})
    @PutMapping(
            value = "/trip",
            produces = {"application/json"})
    ResponseEntity<Trip> generateTrip(
            @ApiParam(value = "The city which the trip is generated for", required = true, defaultValue = "-1", example = "123") @Valid @RequestParam(value = "cityId") int cityId,
            @ApiParam(value = "Name for the new trip", required = true, defaultValue = "Trip", example = "My personal Trip") @Valid @RequestParam(value = "tripName") String tripName,
            @ApiParam(value = "Flag to set privacy setting for this trip", required = true, defaultValue = "false", example = "false") @Valid @RequestParam(value = "privateFlag") boolean privateFlag,
            @ApiParam(value = "List of stops that are part of the trip", required = true, defaultValue = "-1", example = "[0,124,758]") @Valid @RequestParam(value = "stopIds") StopIdWrapper stopIds);

    @ApiOperation(
            value = "Get Trip by ID",
            nickname = "getTripByID",
            notes = "",
            response = Trip.class,
            authorizations = {@Authorization(value = "ApiKeyAuth")},
            tags = {"trip"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Trip.class),
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 401, message = "You are not authorized to perform this action"),
            @ApiResponse(code = 403, message = "You can not read a private trip"),
            @ApiResponse(code = 404, message = "Trip not found")})
    @GetMapping(
            value = "/trip/{tripId}",
            produces = {"application/json"})
    ResponseEntity<Trip> getTripByID(@ApiParam(value = "ID of trip to return", required = true, defaultValue = "-1", example = "123") @PathVariable("tripId") Long tripId);

    @ApiOperation(
            value = "Rate a trip",
            nickname = "rateTrip",
            notes = "",
            authorizations = {@Authorization(value = "TokenAuth")},
            tags = {"trip"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation"),
            @ApiResponse(code = 401, message = "You are not authorized to perform this action"),
            @ApiResponse(code = 404, message = "Trip not found")})
    @PostMapping(
            value = "/trip/{tripId}")
    ResponseEntity<Void> rateTrip(@ApiParam(value = "ID of the trip that will be rated", required = true, defaultValue = "-1", example = "123") @PathVariable("tripId") int tripId,
                                  @NotNull @ApiParam(value = "Rating to be created", required = true, defaultValue = "-1", example = "0.75") @Valid @RequestParam(value = "rating") Rating rating);


    @ApiOperation(
            value = "Update a trip",
            nickname = "updateTrip",
            notes = "",
            authorizations = {@Authorization(value = "TokenAuth")},
            tags = {"trip"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successfull operation"),
            @ApiResponse(code = 401, message = "You are not authorized to perform this action"),
            @ApiResponse(code = 500, message = "Trip could not be updated")})
    @PostMapping(
            value = "/trip")
    ResponseEntity<Void> updateTrip(@NotNull @ApiParam(value = "Updated trip", required = true, example = "{id: 123, private: true, city: {id: 123, name: \"New York\", description: \"This is a description of New York.\"}}}")
                                    @Valid @RequestBody Trip trip);

    @ApiOperation(
            value = "Get Trip execution info including route and duration",
            nickname = "getTripExecutionInfo",
            notes = "",
            response = ExecutionInfo.class,
            authorizations = {@Authorization(value = "ApiKeyAuth")},
            tags = {"trip"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = ExecutionInfo.class),
            @ApiResponse(code = 401, message = "You are not authorized to perform this action"),
            @ApiResponse(code = 403, message = "You can not read a private trip"),
            @ApiResponse(code = 404, message = "Trip not found")})
    @GetMapping(
            value = "/trip/{tripId}/execution",
            produces = {"application/json"})
    ResponseEntity<ExecutionInfo> getTripExecutionInfo(@ApiParam(value = "ID of trip to return", required = true, defaultValue = "-1", example = "123") @PathVariable("tripId") Long tripId,
                                                       @ApiParam(value = "Id of user that is reading the trip", required = true, defaultValue = "-1", example = "123") @Valid @RequestParam(value = "userId") Long userId,
                                                       @ApiParam(value = "Latitude where trip should start", required = true, defaultValue = "0.0", example = "-3.25") @Valid @RequestParam(value = "startLatitude") double startLatitude,
                                                       @ApiParam(value = "Longitude where trip should start", required = true, defaultValue = "0.0", example = "56.0") @Valid @RequestParam(value = "startLongitude") double startLongitude,
                                                       @ApiParam(value = "Indicates if reordering of stops is allowed", required = true, defaultValue = "false", example = "false") @Valid @RequestParam(value = "reorderAllowed") boolean reorderAllowed,
                                                       @ApiParam(value = "Indicates if trip should be created as round trip from current position", required = true, defaultValue = "false", example = "true") @Valid @RequestParam(value = "roundTrip") boolean roundTrip);

    @ApiOperation(
            value = "Get rerouting to the next stop",
            nickname = "getRoutingToStop",
            notes = "",
            response = ExecutionInfo.class,
            authorizations = {@Authorization(value = "ApiKeyAuth")},
            tags = {"trip"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = ExecutionInfo.class),
            @ApiResponse(code = 401, message = "You are not authorized to perform this action"),
            @ApiResponse(code = 404, message = "Stop not found")
    })
    @GetMapping(
            value = "/trip/reroute",
            produces = {"application/json"})
    ResponseEntity<ExecutionInfo> getRoutingToStop(@ApiParam(value = "Latitude where rerouting should start", required = true, defaultValue = "0.0", example = "-3.25") @Valid @RequestParam(value = "startLatitude") double startLatitude,
                                                   @ApiParam(value = "Longitude where rerouting should start", required = true, defaultValue = "0.0", example = "56.0") @Valid @RequestParam(value = "startLongitude") double startLongitude,
                                                   @ApiParam(value = "Stop ID that should be rerouted to", required = true, defaultValue = "-1", example = "123") @Valid @RequestParam(value = "stopId") Long stopId);

}
