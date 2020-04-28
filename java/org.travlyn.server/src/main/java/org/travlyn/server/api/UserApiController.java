package org.travlyn.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.travlyn.server.service.TravlynService;
import org.travlyn.shared.model.api.Trip;
import org.travlyn.shared.model.api.User;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Controller
public class UserApiController implements UserApi {
    private final static String ACCEPT_HEADER = "Accept";
    private final static String JSON_TYPE = "application/json";
    private final HttpServletRequest request;

    @Autowired
    private TravlynService travlynService;

    @Autowired
    public UserApiController(HttpServletRequest request) {
        this.request = request;
    }

    public ResponseEntity<List<Trip>> getTripsByUserId(@PathVariable("userId") Long userId) {
        String accept = request.getHeader(ACCEPT_HEADER);
        if (accept != null && accept.contains(JSON_TYPE)) {
            try {
                List<Trip> result = travlynService.getTripsPerUser(userId);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }catch (NoResultException noResult){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<User> loginUser(@NotNull @Valid String email, @NotNull @Valid String password) {
        String accept = request.getHeader(ACCEPT_HEADER);
        if (accept != null && accept.contains(JSON_TYPE)) {
            return new ResponseEntity<>(travlynService.checkCredentials(email, password, request.getRemoteAddr()), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Void> logoutUser(@NotNull @Valid User user) {
        String accept = request.getHeader(ACCEPT_HEADER);
        if (accept != null && accept.contains(JSON_TYPE)) {
            travlynService.logoutUser(user);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<User> registerUser(@NotNull @Valid String email, @NotNull @Valid String name, @NotNull @Valid String password) {
        String accept = request.getHeader(ACCEPT_HEADER);
        if (accept != null && accept.contains(JSON_TYPE)) {
            return new ResponseEntity<>(travlynService.registerUser(email, name, password, request.getRemoteAddr()),
                    HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Void> updateUser(@NotNull @Valid User user) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
