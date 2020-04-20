package org.travlyn.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.travlyn.server.service.TravlynService;
import org.travlyn.shared.model.api.ExecutionInfo;
import org.travlyn.shared.model.api.Rating;
import org.travlyn.shared.model.api.StopIdWrapper;
import org.travlyn.shared.model.api.Trip;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@Controller
public class TripApiController implements TripApi {

    private static final Logger log = LoggerFactory.getLogger(TripApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private TravlynService travlynService;

    @Autowired
    public TripApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<List<Trip>> findTrip(@NotNull @Valid String searchQuery) {
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

    @Override
    public ResponseEntity<Trip> generateTrip(@NotNull @Valid Long userId, @NotNull @Valid Long cityId, @NotNull @Valid String tripName, @NotNull @Valid boolean privateFlag, @NotNull @Valid StopIdWrapper stopIds) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<>(travlynService.generateTrip(userId, cityId, tripName, privateFlag, stopIds.getStopIds()), HttpStatus.OK);
            } catch (NoResultException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Trip> getTripByID(Long tripId, Long userId) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<>(travlynService.getTrip(tripId), HttpStatus.OK);
            } catch (NoResultException e) {
                log.error("Couldn't find requested trip", e);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Void> rateTrip(Long tripId, @NotNull @Valid Rating rating) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> updateTrip(@NotNull @RequestBody @Valid Trip trip) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                travlynService.updateTrip(trip);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (NoResultException e) {
                log.error("Couldn't find requested trip", e);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<ExecutionInfo> getTripExecutionInfo(@PathVariable("tripId") Long tripId, Long userId, double startLatitude, double startLongitude, boolean reorderAllowed, boolean roundTrip) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            String language = request.getHeader("Accept-Language");
            if (language == null || language.isBlank()){
                language = "en";
            }
            try{
                ExecutionInfo info = travlynService.getExecutionInfo(tripId,userId,startLatitude,startLongitude,reorderAllowed,roundTrip,language);
                if (info == null){
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity<>(info,HttpStatus.OK);
            }catch (NoResultException e){
                log.error("Couldn't find requested trip", e);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<ExecutionInfo> getRoutingToStop(double startLatitude, double startLongitude, Long stopId) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            String language = request.getHeader("Accept-Language");
            if (language == null || language.isBlank()){
                language = "en";
            }
            try{
                ExecutionInfo info = travlynService.getRedirection(startLatitude,startLongitude,stopId,language);
                if (info == null){
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity<>(info,HttpStatus.OK);
            }catch (NoResultException e){
                log.error("Couldn't find requested stop", e);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
