package org.travlyn.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.travlyn.server.service.TravlynService;
import org.travlyn.shared.model.api.Rating;
import org.travlyn.shared.model.api.Stop;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller
public class StopApiController implements StopApi {

    private static final Logger log = LoggerFactory.getLogger(StopApiController.class);
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    @Autowired
    private TravlynService travlynService;

    @Autowired
    public StopApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public ResponseEntity<Void> rateStop(@PathVariable("stopId") int stopId, @NotNull @Valid Rating rating) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            if (this.travlynService.addRatingToStop(stopId, rating)) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Stop> stopStopIdGet(@PathVariable("stopId") Long stopId) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            Stop result = travlynService.getStopById(stopId);
            if (result != null) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
