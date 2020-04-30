package org.travlyn.server.api;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.travlyn.server.configuration.WebSecurityConfiguration;
import org.travlyn.server.service.TravlynService;
import org.travlyn.shared.model.api.Trip;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@Import(WebSecurityConfiguration.class)
@WebMvcTest(TripApiController.class)
public class TripControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TravlynService service;

    @Test
    public void testGetTrip() throws Exception {
        when(service.getTrip(1L)).thenReturn(new Trip().name("test Trip").id(1));
        this.mockMvc.perform(get("/trip/1").accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk());
    }
}
