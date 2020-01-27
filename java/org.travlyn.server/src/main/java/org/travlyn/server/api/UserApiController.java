package org.travlyn.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.travlyn.server.service.TravlynService;
import org.travlyn.shared.model.api.Trip;
import org.travlyn.shared.model.api.User;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@Controller
public class UserApiController implements UserApi {

    private static final Logger log = LoggerFactory.getLogger(UserApiController.class);
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    @Autowired
    private TravlynService travlynService;

    @Autowired
    public UserApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<List<Trip>> getTripsByUserId(Long userId) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<Trip>>(objectMapper.readValue("[ {\n  \"geoText\" : [ {\n    \"id\" : 1,\n    \"text\" : \"text\"\n  }, {\n    \"id\" : 1,\n    \"text\" : \"text\"\n  } ],\n  \"private\" : true,\n  \"city\" : {\n    \"image\" : \"image\",\n    \"name\" : \"name\",\n    \"description\" : \"description\",\n    \"id\" : 5\n  },\n  \"ratings\" : [ null, null ],\n  \"id\" : 0,\n  \"stops\" : [ {\n    \"ratings\" : [ {\n      \"rating\" : 7.386281948385884,\n      \"description\" : \"description\",\n      \"id\" : 4\n    }, {\n      \"rating\" : 7.386281948385884,\n      \"description\" : \"description\",\n      \"id\" : 4\n    } ],\n    \"latitude\" : 7.061401241503109,\n    \"name\" : \"name\",\n    \"description\" : \"description\",\n    \"average_rating\" : 2.027123023002322,\n    \"id\" : 5,\n    \"category\" : {\n      \"name\" : \"name\",\n      \"id\" : 1\n    },\n    \"pricing\" : 9.301444243932576,\n    \"longitude\" : 2.3021358869347655,\n    \"time_effort\" : 3.616076749251911\n  }, {\n    \"ratings\" : [ {\n      \"rating\" : 7.386281948385884,\n      \"description\" : \"description\",\n      \"id\" : 4\n    }, {\n      \"rating\" : 7.386281948385884,\n      \"description\" : \"description\",\n      \"id\" : 4\n    } ],\n    \"latitude\" : 7.061401241503109,\n    \"name\" : \"name\",\n    \"description\" : \"description\",\n    \"average_rating\" : 2.027123023002322,\n    \"id\" : 5,\n    \"category\" : {\n      \"name\" : \"name\",\n      \"id\" : 1\n    },\n    \"pricing\" : 9.301444243932576,\n    \"longitude\" : 2.3021358869347655,\n    \"time_effort\" : 3.616076749251911\n  } ],\n  \"user\" : {\n    \"name\" : \"name\",\n    \"id\" : 6,\n    \"email\" : \"email\",\n    \"token\" : {\n      \"id\" : 1,\n      \"ip_address\" : \"ip_address\",\n      \"token\" : \"token\"\n    }\n  }\n}, {\n  \"geoText\" : [ {\n    \"id\" : 1,\n    \"text\" : \"text\"\n  }, {\n    \"id\" : 1,\n    \"text\" : \"text\"\n  } ],\n  \"private\" : true,\n  \"city\" : {\n    \"image\" : \"image\",\n    \"name\" : \"name\",\n    \"description\" : \"description\",\n    \"id\" : 5\n  },\n  \"ratings\" : [ null, null ],\n  \"id\" : 0,\n  \"stops\" : [ {\n    \"ratings\" : [ {\n      \"rating\" : 7.386281948385884,\n      \"description\" : \"description\",\n      \"id\" : 4\n    }, {\n      \"rating\" : 7.386281948385884,\n      \"description\" : \"description\",\n      \"id\" : 4\n    } ],\n    \"latitude\" : 7.061401241503109,\n    \"name\" : \"name\",\n    \"description\" : \"description\",\n    \"average_rating\" : 2.027123023002322,\n    \"id\" : 5,\n    \"category\" : {\n      \"name\" : \"name\",\n      \"id\" : 1\n    },\n    \"pricing\" : 9.301444243932576,\n    \"longitude\" : 2.3021358869347655,\n    \"time_effort\" : 3.616076749251911\n  }, {\n    \"ratings\" : [ {\n      \"rating\" : 7.386281948385884,\n      \"description\" : \"description\",\n      \"id\" : 4\n    }, {\n      \"rating\" : 7.386281948385884,\n      \"description\" : \"description\",\n      \"id\" : 4\n    } ],\n    \"latitude\" : 7.061401241503109,\n    \"name\" : \"name\",\n    \"description\" : \"description\",\n    \"average_rating\" : 2.027123023002322,\n    \"id\" : 5,\n    \"category\" : {\n      \"name\" : \"name\",\n      \"id\" : 1\n    },\n    \"pricing\" : 9.301444243932576,\n    \"longitude\" : 2.3021358869347655,\n    \"time_effort\" : 3.616076749251911\n  } ],\n  \"user\" : {\n    \"name\" : \"name\",\n    \"id\" : 6,\n    \"email\" : \"email\",\n    \"token\" : {\n      \"id\" : 1,\n      \"ip_address\" : \"ip_address\",\n      \"token\" : \"token\"\n    }\n  }\n} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<User> loginUser(@NotNull @Valid String email, @NotNull @Valid String password) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            return new ResponseEntity<>(travlynService.checkCredentials(email, password), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<Void> logoutUser(@NotNull @Valid User user) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<User> registerUser(@NotNull @Valid String email, @NotNull @Valid String name, @NotNull @Valid String password) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<>(objectMapper.readValue("{\n  \"name\" : \"name\",\n  \"id\" : 6,\n  \"email\" : \"email\",\n  \"token\" : {\n    \"id\" : 1,\n    \"ip_address\" : \"ip_address\",\n    \"token\" : \"token\"\n  }\n}", User.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> updateUser(@NotNull @Valid User user) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
