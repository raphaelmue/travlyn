package org.travlyn.server.api;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.travlyn.shared.model.api.Rating;
import org.travlyn.shared.model.api.Trip;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

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
    ResponseEntity<List<Trip>> findTrip(@NotNull @ApiParam(value = "Search query for trips", required = true) @Valid @RequestParam(value = "searchQuery") String searchQuery);

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
            @ApiResponse(code = 500, message = "Trip could not be generated")})
    @PutMapping(
            value = "/trip",
            produces = {"application/json"})
    ResponseEntity<Trip> generateTrip(@NotNull @ApiParam(value = "The user who generates the trip", required = true) @Valid @RequestParam(value = "userId") Long userId);

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
            @ApiResponse(code = 404, message = "Trip not found")})
    @GetMapping(
            value = "/trip/{tripId}",
            produces = {"application/json"})
    ResponseEntity<Trip> getTripByID(@ApiParam(value = "ID of trip to return", required = true) @PathVariable("tripId") Long tripId);

    @ApiOperation(
            value = "Rate a trip",
            nickname = "rateTrip",
            notes = "",
            authorizations = {@Authorization(value = "TokenAuth")},
            tags = {"trip"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation"),
            @ApiResponse(code = 401, message = "You are not authorized to perform this action")})
    @PostMapping(
            value = "/trip/{tripId}")
    ResponseEntity<Void> rateTrip(@ApiParam(value = "ID of the trip that will be rated", required = true) @PathVariable("tripId") Long tripId,
                                  @NotNull @ApiParam(value = "Rating to be created", required = true) @Valid @RequestParam(value = "rating") Rating rating);


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
    ResponseEntity<Void> updateTrip(@NotNull @ApiParam(value = "Updated trip", required = true) @Valid @RequestParam(value = "trip") Trip trip);

}