package org.travlyn.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.travlyn.server.service.TravlynService;
import org.travlyn.shared.model.api.City;
import org.travlyn.shared.model.api.Trip;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Controller
public class CityApiController implements CityApi {
    private final HttpServletRequest request;

    @Autowired
    private TravlynService travlynService;

    @Autowired
    public CityApiController(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public ResponseEntity<City> getCity(@NotNull @Valid String query) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            City result = travlynService.getCityWithInformation(query);
            if (result != null) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<List<Trip>> getPublicTripsForCity(@NotNull @Valid Long cityId) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                List<Trip> result = travlynService.getTripsForCity(cityId);
                return new ResponseEntity<>(result,HttpStatus.OK);
            }catch (NoResultException noResult){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
