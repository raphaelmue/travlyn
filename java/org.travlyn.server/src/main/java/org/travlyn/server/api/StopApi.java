package org.travlyn.server.api;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.travlyn.shared.model.api.Rating;
import org.travlyn.shared.model.api.Stop;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
    ResponseEntity<Void> rateStop(@ApiParam(value = "ID of the stop that will be rated", required = true, defaultValue = "-1", example = "123") @PathVariable("stopId") Long stopId,
                                  @NotNull @ApiParam(value = "Rating to be created", required = true, defaultValue = "-1", example = "0.75") @Valid @RequestParam(value = "rating") Rating rating);

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
