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
    value = "/city",
    produces = {"application/json"})
  @PreAuthorize(value = "hasRole(" + ROLE_API_USER + ")")
  ResponseEntity<City> getCity(@ApiParam(value = "Name of the city that should be searched for", required = true, defaultValue = "", example = "Düsseldorf") @Valid @RequestParam(value = "query") String query);
}
