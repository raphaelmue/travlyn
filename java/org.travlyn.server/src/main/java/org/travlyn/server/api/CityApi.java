package org.travlyn.server.api;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.travlyn.shared.model.api.City;

import javax.validation.Valid;

@Api(value = "city")
public interface CityApi {

    @ApiOperation(
            value = "Get City by search term",
            nickname = "getCity",
            notes = "",
            response = City.class,
            authorizations = {@Authorization(value = "ApiKeyAuth")},
            tags = {"city"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = City.class),
            @ApiResponse(code = 401, message = "You are not authorized to perform this action")})
    @GetMapping(
            value = "/city/",
            produces = {"application/json"})
    ResponseEntity<City> getCity(@ApiParam(value = "Name of the city that should be searched for", required = true, defaultValue = "", example = "Düsseldorf") @Valid @RequestParam(value = "city") String cityName);

}
