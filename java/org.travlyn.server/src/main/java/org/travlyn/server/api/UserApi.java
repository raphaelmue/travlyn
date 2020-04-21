package org.travlyn.server.api;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.travlyn.shared.model.api.Trip;
import org.travlyn.shared.model.api.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static org.travlyn.server.configuration.AuthenticationTokenFilter.REGISTERED_USER_ROLE;

@Api(value = "user")
public interface UserApi {

    @ApiOperation(
            value = "Get all Trips of user",
            nickname = "getTripsByUserId",
            notes = "",
            response = Trip.class,
            responseContainer = "List",
            authorizations = {@Authorization(value = "ApiKeyAuth")},
            tags = {"user", "trip"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Trip.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "You are not authorized to perform this action")})
    @GetMapping(
            value = "/user/{userId}/trips",
            produces = {"application/json"})
    ResponseEntity<List<Trip>> getTripsByUserId(
            @ApiParam(value = "ID of the user whose trips are to be returned", required = true, defaultValue = "-1", example = "123")
            @PathVariable(value = "userId") Long userId);

    @ApiOperation(
            value = "Logs user into the system",
            nickname = "loginUser",
            notes = "",
            response = User.class,
            tags = {"user"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class),
            @ApiResponse(code = 401, message = "Invalid username/password supplied")})
    @GetMapping(
            value = "/user",
            produces = {"application/json"})
    ResponseEntity<User> loginUser(@NotNull @ApiParam(value = "The email for login", required = true, example = "test@email.com") @Valid @RequestParam(value = "email") String email,
                                   @NotNull @ApiParam(value = "The password for login in clear text", required = true, example = "secret") @Valid @RequestParam(value = "password") String password);

    @ApiOperation(
            value = "Log out current logged in user session",
            nickname = "logoutUser",
            notes = "",
            authorizations = {@Authorization(value = "TokenAuth")},
            tags = {"user"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation"),
            @ApiResponse(code = 401, message = "You are not authorized to perform this action")})
    @DeleteMapping(
            value = "/user")
    @PreAuthorize(value = "hasRole(" + REGISTERED_USER_ROLE + ")")
    ResponseEntity<Void> logoutUser(@NotNull @ApiParam(value = "The user to logout", required = true, example = "{id: 123, email: \"test@email.com\", name: \"Test User\"}")
                                    @Valid @RequestParam(value = "user") User user);

    @ApiOperation(
            value = "Create a new User",
            nickname = "registerUser",
            notes = "",
            response = User.class,
            tags = {"user"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = User.class),
            @ApiResponse(code = 409, message = "Email is already in use")})
    @PutMapping(
            value = "/user",
            produces = {"application/json"})
    ResponseEntity<User> registerUser(@NotNull @ApiParam(value = "The email for registration", required = true, example = "test@email.com") @Valid @RequestParam(value = "email") String email,
                                      @NotNull @ApiParam(value = "The name for registration", required = true, example = "Test User") @Valid @RequestParam(value = "Name") String name,
                                      @NotNull @ApiParam(value = "The password for registration in clear text", required = true, example = "secret") @Valid @RequestParam(value = "password") String password);

    @ApiOperation(
            value = "Update users information",
            nickname = "updateUser",
            notes = "",
            authorizations = {@Authorization(value = "TokenAuth")},
            tags = {"user"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation"),
            @ApiResponse(code = 401, message = "You are not authorized to perform this action")})
    @PostMapping(value = "/user")
    @PreAuthorize(value = "hasRole(" + REGISTERED_USER_ROLE + ")")
    ResponseEntity<Void> updateUser(@NotNull @ApiParam(value = "Updated user object", required = true, example = "{id: 123, email: \"test@email.com\", name: \"Test User\"}")
                                    @Valid @RequestParam(value = "user") User user);

}
