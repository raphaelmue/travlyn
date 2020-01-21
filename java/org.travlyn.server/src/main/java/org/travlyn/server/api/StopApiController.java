package org.travlyn.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.travlyn.shared.model.api.Rating;
import org.travlyn.shared.model.api.Stop;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@Controller
public class StopApiController implements StopApi {

    private static final Logger log = LoggerFactory.getLogger(StopApiController.class);
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    @Autowired
    public StopApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public ResponseEntity<Void> rateStop(Long stopId, @NotNull @Valid Rating rating) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Stop> stopStopIdGet(Long stopId) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<>(objectMapper.readValue("{\n  \"ratings\" : [ {\n    \"rating\" : 7.386281948385884,\n    \"description\" : \"description\",\n    \"id\" : 4\n  }, {\n    \"rating\" : 7.386281948385884,\n    \"description\" : \"description\",\n    \"id\" : 4\n  } ],\n  \"latitude\" : 7.061401241503109,\n  \"name\" : \"name\",\n  \"description\" : \"description\",\n  \"average_rating\" : 2.027123023002322,\n  \"id\" : 5,\n  \"category\" : {\n    \"name\" : \"name\",\n    \"id\" : 1\n  },\n  \"pricing\" : 9.301444243932576,\n  \"longitude\" : 2.3021358869347655,\n  \"time_effort\" : 3.616076749251911\n}", Stop.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
