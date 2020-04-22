package org.travlyn.server.api;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.travlyn.server.configuration.AuthenticationTokenFilter;
import org.travlyn.shared.model.api.Rating;
import org.travlyn.shared.model.api.Stop;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.travlyn.server.configuration.AuthenticationTokenFilter.REGISTERED_USER_ROLE;

@Api(value = "stop")
public interface StopApi {

    @ApiOperation(
            value = "Rate a stop",
            nickname = "rateStop",
            notes = "",
            authorizations = {@Authorization(value = "TokenAuth")},
            tags = {"stop"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation"),
            @ApiResponse(code = 401, message = "You are not authorized to perform this action")})
    @PostMapping(
            value = "/stop/{stopId}")
    @PreAuthorize(value = "hasRole(" + REGISTERED_USER_ROLE + ")")
    ResponseEntity<Void> rateStop(@ApiParam(value = "ID of the stop that will be rated", required = true, defaultValue = "-1", example = "123") @PathVariable("stopId") int stopId,
                                  @NotNull @ApiParam(value = "Rating to be created", required = true, defaultValue = "-1", example = "0.75") @Valid @RequestParam(value = "rating") Rating rating);

    @ApiOperation(
            value = "Commit pricing average to stop",
            nickname = "addPricingStop",
            notes = "",
            authorizations = {@Authorization(value = "TokenAuth")},
            tags = {"stop"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation"),
            @ApiResponse(code = 400, message = "Accept Header missing or invalid pricing supplied."),
            @ApiResponse(code = 401, message = "You are not authorized to perform this action"),
            @ApiResponse(code = 404, message = "Stop Id not found")})
    @PostMapping(
            value = "/stop/{stopId}/pricing")
    ResponseEntity<Stop> addPricingStop(@ApiParam(value = "ID of the stop that will be rated", required = true, defaultValue = "-1", example = "123") @PathVariable("stopId") int stopId,
                                  @NotNull @ApiParam(value = "Average price for adult in USD", required = true, defaultValue = "-1", example = "14.5") @Valid @RequestParam(value = "pricing") double pricing);

    @ApiOperation(
            value = "Commit time effort to stop",
            nickname = "addTimeEffortStop",
            notes = "",
            authorizations = {@Authorization(value = "TokenAuth")},
            tags = {"stop"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation"),
            @ApiResponse(code = 400, message = "Accept Header missing or invalid time effort supplied."),
            @ApiResponse(code = 401, message = "You are not authorized to perform this action"),
            @ApiResponse(code = 404, message = "Stop Id not found")})
    @PostMapping(
            value = "/stop/{stopId}/timeEffort")
    ResponseEntity<Stop> addTimeEffortStop(@ApiParam(value = "ID of the stop that will be rated", required = true, defaultValue = "-1", example = "123") @PathVariable("stopId") int stopId,
                                        @NotNull @ApiParam(value = "Average time effort to visit this stop in hours", required = true, defaultValue = "-1", example = "1.5") @Valid @RequestParam(value = "timeEffort") double timeEffort);

    @ApiOperation(
            value = "Get Stop by ID",
            nickname = "stopStopIdGet",
            notes = "",
            response = Stop.class,
            authorizations = {@Authorization(value = "ApiKeyAuth")},
            tags = {"stop"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Stop.class),
            @ApiResponse(code = 401, message = "You are not authorized to perform this action")})
    @GetMapping(
            value = "/stop/{stopId}",
            produces = {"application/json"})
    ResponseEntity<Stop> stopStopIdGet(@ApiParam(value = "ID of the stop that will be returned", required = true, defaultValue = "-1", example = "123") @PathVariable("stopId") Long stopId);

}
