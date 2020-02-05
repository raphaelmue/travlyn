package org.travlyn.server.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.travlyn.shared.model.api.Token;
import org.travlyn.shared.model.api.User;
import org.travlyn.shared.model.db.UserEntity;

import javax.transaction.Transactional;

@Tag("unit")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

        User userToAssert = service.checkCredentials("test@email.com", "password");
        Assertions.assertNotNull(userToAssert);
        Assertions.assertNotNull(userToAssert.getToken());
        Assertions.assertEquals("test@email.com", userToAssert.getEmail());

        // wrong password
        userToAssert = service.checkCredentials("test@email.com", "wrong");
        Assertions.assertNull(userToAssert);

        // wrong email
        userToAssert = service.checkCredentials("test@wrong.com", "password");
        Assertions.assertNull(userToAssert);
    }

    @Test
    @Transactional
    public void testGenerateToken() {
        Session session = sessionFactory.getCurrentSession();
        UserEntity userEntity = new UserEntity();
        userEntity.setName("Test User");
        userEntity.setEmail("test@email.com");
        userEntity.setPassword("6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af");
        userEntity.setSalt("I2HoOYJmqKfGboyJAdCEQwulUkxmhVH5");

        session.save(userEntity);

        Token tokenToAssert = service.generateToken(userEntity);
        Assertions.assertTrue(tokenToAssert.getId() > 0);
        Assertions.assertEquals(64, tokenToAssert.getToken().length());
    }
}