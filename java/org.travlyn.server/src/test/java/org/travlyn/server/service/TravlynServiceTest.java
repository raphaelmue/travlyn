package org.travlyn.server.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.travlyn.server.TravlynServer;
import org.travlyn.server.configuration.PersistenceConfiguration;
import org.travlyn.server.configuration.TravlynServiceConfiguration;
import org.travlyn.shared.model.api.User;
import org.travlyn.shared.model.db.UserEntity;

import javax.transaction.Transactional;

@Tag("unit")
@SpringBootTest(
        classes = {TravlynServer.class, TravlynServiceConfiguration.class, PersistenceConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TravlynServiceTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private TravlynService service;

    @Test
    @Transactional
    public void testCheckCredentials() {
        Session session = sessionFactory.getCurrentSession();
        UserEntity userEntity = new UserEntity();
        userEntity.setName("Test User");
        userEntity.setEmail("test@email.com");
        userEntity.setPassword("6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af");
        userEntity.setSalt("I2HoOYJmqKfGboyJAdCEQwulUkxmhVH5");

        session.save(userEntity);

        User userToAssert = service.checkCredentials("test@test.com", "password");
        Assertions.assertNotNull(userToAssert);
        Assertions.assertEquals("test@email.com", userToAssert.getEmail());
    }
}