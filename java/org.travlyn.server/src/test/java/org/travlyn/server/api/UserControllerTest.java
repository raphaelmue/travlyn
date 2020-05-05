package org.travlyn.server.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
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
import org.travlyn.shared.model.api.User;

import javax.persistence.NoResultException;
import java.util.ArrayList;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@Import(WebSecurityConfiguration.class)
@WebMvcTest(UserApiController.class)
public class UserControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private TravlynService service;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testLogin() throws Exception {
        when(service.checkCredentials(eq("test@test.de"),eq("password"),anyString())).thenReturn(new User().email("test@test.de"));

        MvcResult result = this.mockMvc.perform(get("/user")
                .accept(MediaType.APPLICATION_JSON)
                .param("email","test@test.de")
                .param("password","password"))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals("{\"id\":-1,\"email\":\"test@test.de\",\"name\":null,\"token\":null,\"preferences\":[]}",result.getResponse().getContentAsString());

        this.mockMvc.perform(get("/user")
                .param("email","test@test.de")
                .param("password","password"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLogout() throws Exception {
        this.mockMvc.perform(delete("/user")
                .accept(MediaType.APPLICATION_JSON)
                .param("user","{id: 123, email: \"test@email.com\", name: \"Test User\"}"))
                .andExpect(status().isOk());

        this.mockMvc.perform(delete("/user")
                .param("user","{id: 123, email: \"test@email.com\", name: \"Test User\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegistering() throws Exception {
        when(service.registerUser(eq("test@test.de"),eq("Test"),eq("password"),anyString())).thenReturn(new User().email("test@test.de"));
        MvcResult result = this.mockMvc.perform(put("/user")
                .accept(MediaType.APPLICATION_JSON)
                .param("email","test@test.de")
                .param("name","Test")
                .param("password","password"))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals("{\"id\":-1,\"email\":\"test@test.de\",\"name\":null,\"token\":null,\"preferences\":[]}",result.getResponse().getContentAsString());

        this.mockMvc.perform(patch("/user")
                .accept(MediaType.APPLICATION_JSON)
                .param("email","test@test.de")
                .param("name","Test")
                .param("password","password"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(put("/user")
                .param("email","test@test.de")
                .param("name","Test")
                .param("password","password"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetTripsByUserId() throws Exception {
        when(service.getTripsPerUser(1L)).thenReturn(new ArrayList<>());
        when(service.getTripsPerUser(2L)).thenThrow(new NoResultException());

        MvcResult result = this.mockMvc.perform(get("/user/{userId}/trips",1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals("[]",result.getResponse().getContentAsString());

        this.mockMvc.perform(get("/user/{userId}/trips",2)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
