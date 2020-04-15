package org.travlyn.server.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.travlyn.shared.model.api.City;
import org.travlyn.shared.model.api.Token;
import org.travlyn.shared.model.api.User;
import org.travlyn.shared.model.db.CityEntity;
import org.travlyn.shared.model.db.StopEntity;
import org.travlyn.shared.model.db.TokenEntity;
import org.travlyn.shared.model.db.UserEntity;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Tag("unit")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TravlynServiceTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private TravlynService service;

    private UserEntity userEntity;
    private TokenEntity tokenEntity;

    @Before
    @Transactional
    public void setup() {
        Session session = sessionFactory.getCurrentSession();
        userEntity = new UserEntity();
        userEntity.setName("Test User");
        userEntity.setEmail("test@email.com");
        userEntity.setPassword("6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af");
        userEntity.setSalt("I2HoOYJmqKfGboyJAdCEQwulUkxmhVH5");

        session.save(userEntity);

        tokenEntity = new TokenEntity();
        tokenEntity.setUser(userEntity);
        tokenEntity.setToken("6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af");
        tokenEntity.setIpAddress("192.168.0.1");
        tokenEntity.setExpireDate(LocalDate.now().plusMonths(1));

        session.save(tokenEntity);
    }

    @Test
    @Transactional
    public void testCheckCredentials() {
        Session session = sessionFactory.getCurrentSession();

        User userToAssert = service.checkCredentials("test@email.com", "password", "192.168.0.1");
        Assertions.assertNotNull(userToAssert);
        Assertions.assertNotNull(userToAssert.getToken());
        Assertions.assertEquals("test@email.com", userToAssert.getEmail());

        // wrong password
        userToAssert = service.checkCredentials("test@email.com", "wrong", "192.168.0.1");
        Assertions.assertNull(userToAssert);

        // wrong email
        userToAssert = service.checkCredentials("test@wrong.com", "password", "192.168.0.1");
        Assertions.assertNull(userToAssert);
    }

    @Test
    @Transactional
    public void testGetCityWithInformation() {
        //valid search term
        City cityToAssert = service.getCityWithInformation("Poole");
        Assertions.assertNotNull(cityToAssert);
        Assertions.assertEquals("Poole /puːl/ is a large coastal town and seaport in the county of Dorset, on the south coast of England. The town is 33 kilometres (21 mi) east of Dorchester, and adjoins Bournemouth to the east. The local council is Borough of Poole and was made a unitary authority in 1997, gaining administrative independence from Dorset County Council. The borough had a population of 147,645 at the 2011 census, making it the second largest in Dorset. Together with Bournemouth and Christchurch, the town forms the South East Dorset conurbation with a total population of over 465,000. Human settlement in the area dates back to before the Iron Age. The earliest recorded use of the town's name was in the 12th century when the town began to emerge as an important port, prospering with the introduction of the wool trade. In later centuries, the town had important trade links with North America and at its peak in the 18th century it was one of the busiest ports in Britain. In the Second World War, the town was one of the main departing points for the Normandy landings. Poole is a tourist resort, attracting visitors with its large natural harbour, history, the Lighthouse arts centre and Blue Flag beaches. The town has a busy commercial port with cross-Channel freight and passenger ferry services. The headquarters of the Royal National Lifeboat Institution (RNLI) are in Poole, and the Royal Marines have a base in the town's harbour. Despite their names, Poole is the home of The Arts University Bournemouth, the Bournemouth Symphony Orchestra and a significant part of Bournemouth University.", cityToAssert.getDescription());
        Assertions.assertEquals("http://commons.wikimedia.org/wiki/Special:FilePath/Poole_port.jpg?width=300", cityToAssert.getImage());

        //test if caching is working
        Session session = sessionFactory.getCurrentSession();
        CityEntity result = session.createQuery("from CityEntity where name = :name", CityEntity.class)
                .setParameter("name", "Poole")
                .getSingleResult();
        Assertions.assertNotNull(result);

        //test if stops are present
        Assertions.assertNotEquals(0,result.getStops().size());

        //invalid search term
        cityToAssert = service.getCityWithInformation("xyz");
        Assertions.assertNull(cityToAssert);
    }

    @Test
    @Transactional
    public void testGenerateToken() {
        Session session = sessionFactory.getCurrentSession();

        Token tokenToAssert = service.generateToken(userEntity, "192.168.0.1");
        Assertions.assertTrue(tokenToAssert.getId() > 0);
        Assertions.assertEquals(64, tokenToAssert.getToken().length());
    }

    @Test
    @Transactional
    public void testLogoutUser() {
        Session session = sessionFactory.getCurrentSession();

        // deleting currently stored token from session
        session.clear();
        service.logoutUser(userEntity.toDataTransferObject().token(tokenEntity.toDataTransferObject()));
        Assertions.assertNull(session.get(TokenEntity.class, tokenEntity.getId()));
    }
}