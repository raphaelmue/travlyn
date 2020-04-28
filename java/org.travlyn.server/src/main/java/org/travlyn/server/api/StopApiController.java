package org.travlyn.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.travlyn.server.service.TravlynService;
import org.travlyn.server.service.ValueException;
import org.travlyn.shared.model.api.Rating;
import org.travlyn.shared.model.api.Stop;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller
@Validated
public class StopApiController implements StopApi {
    private final static String ACCEPT_HEADER = "Accept";
    private final static String JSON_TYPE = "application/json";
    private final HttpServletRequest request;

    @Autowired
    private TravlynService travlynService;

    @Autowired
    public StopApiController(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public ResponseEntity<Void> rateStop(@PathVariable("stopId") int stopId, @NotNull @Valid Rating rating) {
        String accept = request.getHeader(ACCEPT_HEADER);
        if (accept != null && accept.contains(JSON_TYPE)) {
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
    public ResponseEntity<Stop> addPricingStop(@PathVariable("stopId") int stopId, double pricing) {
        String accept = request.getHeader(ACCEPT_HEADER);
        if (accept != null && accept.contains(JSON_TYPE)) {
            Stop stop;
            try {
                stop = this.travlynService.addPricingToStop(stopId, pricing);
            } catch (NoResultException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (ValueException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (stop != null) {
                return new ResponseEntity<>(stop, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Stop> addTimeEffortStop(@PathVariable("stopId") int stopId, double timeEffort) {
        String accept = request.getHeader(ACCEPT_HEADER);
        if (accept != null && accept.contains(JSON_TYPE)) {
            Stop stop;
            try {
                stop = this.travlynService.addTimeEffortToStop(stopId, timeEffort);
            } catch (NoResultException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (ValueException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (stop != null) {
                return new ResponseEntity<>(stop, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Stop> stopStopIdGet(@PathVariable("stopId") Long stopId) {
        String accept = request.getHeader(ACCEPT_HEADER);
        if (accept != null && accept.contains(JSON_TYPE)) {
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
