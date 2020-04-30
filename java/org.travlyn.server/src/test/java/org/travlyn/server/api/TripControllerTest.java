package org.travlyn.server.api;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.travlyn.server.configuration.WebSecurityConfiguration;
import org.travlyn.server.service.TravlynService;
import org.travlyn.shared.model.api.Trip;

import javax.persistence.NoResultException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@Import(WebSecurityConfiguration.class)
@WebMvcTest(TripApiController.class)
public class TripControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private TravlynService service;

    @BeforeAll
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }



    @Test
    public void testGetTrip() throws Exception {
        when(service.getTrip(1L)).thenReturn(new Trip().name("test Trip").id(1));
        when(service.getTrip(2L)).thenThrow(new NoResultException());
        MvcResult result = this.mockMvc.perform(get("/trip/{tripId}",1).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        Assertions.assertEquals("{\"private\":null,\"id\":1,\"user\":null,\"city\":null,\"name\":\"test Trip\",\"stops\":null,\"ratings\":null,\"averageRating\":0.0,\"geoText\":null}",result.getResponse().getContentAsString());

        this.mockMvc.perform(get("/trip/{tripId}",2).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

        this.mockMvc.perform(get("/trip/{tripId}",1)).andExpect(status().isBadRequest());
    }

    @Test
    public void testGet
}
