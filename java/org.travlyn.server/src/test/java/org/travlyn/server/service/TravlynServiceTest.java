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
import org.travlyn.shared.model.api.*;
import org.travlyn.shared.model.db.*;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

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
    private StopEntity stopEntity;
    private StopEntity secondStopEntity;
    private CityEntity cityEntity;
    private CategoryEntity categoryEntity;

    @Before
    @Transactional
    public void setup() {
        Session session = sessionFactory.getCurrentSession();
        userEntity = new UserEntity();
        userEntity.setName("Test User");
        userEntity.setEmail("test@email.com");
        userEntity.setPassword("6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af");
        userEntity.setSalt("I2HoOYJmqKfGboyJAdCEQwulUkxmhVH5");

        userEntity.setId((Integer) session.save(userEntity));

        tokenEntity = new TokenEntity();
        tokenEntity.setUser(userEntity);
        tokenEntity.setToken("6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af");
        tokenEntity.setIpAddress("192.168.0.1");
        tokenEntity.setExpireDate(LocalDate.now().plusMonths(1));

        session.save(tokenEntity);

        categoryEntity = new CategoryEntity();
        categoryEntity.setName("tourism");

        categoryEntity.setId((Integer) session.save(categoryEntity));

        stopEntity = new StopEntity();
        stopEntity.setName("Test Stop");
        stopEntity.setDescription("Test descr");
        stopEntity.setAverageRating(2.0);
        stopEntity.setLatitude(33.0);
        stopEntity.setLongitude(5.0);
        stopEntity.setCategory(categoryEntity);

        stopEntity.setId((Integer) session.save(stopEntity));

        secondStopEntity = new StopEntity();
        secondStopEntity.setName("Second Test Stop");
        secondStopEntity.setDescription("Test descr");
        secondStopEntity.setAverageRating(2.0);
        secondStopEntity.setLatitude(33.0);
        secondStopEntity.setLongitude(5.0);
        secondStopEntity.setCategory(categoryEntity);

        secondStopEntity.setId((Integer) session.save(secondStopEntity));

        cityEntity = new CityEntity();
        cityEntity.setName("Test city");
        cityEntity.setDescription("Test descr");
        cityEntity.setLongitude(0.0);
        cityEntity.setLatitude(0.0);
        cityEntity.setImage("https://testurl.com/test.jpg");

        cityEntity.setId((Integer) session.save(cityEntity));

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
        City cityToAssert = service.getCityWithInformation("Wesel");
        Assertions.assertNotNull(cityToAssert);
        Assertions.assertEquals("Wesel (German pronunciation: [ˈveːzəl]) is a city in North Rhine-Westphalia, Germany. It is the capital of the Wesel district.", cityToAssert.getDescription());
        Assertions.assertEquals("http://commons.wikimedia.org/wiki/Special:FilePath/Wesel_willibrordi_dom_chor.jpg?width=300", cityToAssert.getImage());

        //test if caching is working
        Session session = sessionFactory.getCurrentSession();
        CityEntity result = session.createQuery("from CityEntity where name = :name", CityEntity.class)
                .setParameter("name", "Wesel")
                .getSingleResult();
        Assertions.assertNotNull(result);

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

    @Test
    @Transactional
    public void testGenerateTrip(){
        //normal case
        ArrayList<Long> stopIds= new ArrayList<>();
        stopIds.add((long) stopEntity.getId());
        stopIds.add((long) secondStopEntity.getId());
        Trip result = service.generateTrip((long) userEntity.getId(), (long) cityEntity.getId(),"Test Trip",false,stopIds);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(userEntity.getId(),result.getUser().getId());
        Assertions.assertEquals("Test Trip",result.getName());
        //check order
        for (int i = 0; i<result.getStops().size();i++){
            Assertions.assertEquals(stopIds.get(i),result.getStops().get(i).getId());
        }

        //trip without stops
        result = service.generateTrip((long) userEntity.getId(), (long) cityEntity.getId(),"Test Trip",true,null);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(userEntity.getId(),result.getUser().getId());
        Assertions.assertEquals("Test Trip",result.getName());
        Assertions.assertEquals(0,result.getStops().size());

        //user not found
        Assertions.assertThrows(NoResultException.class, ()-> service.generateTrip((long) userEntity.getId() +256, (long) cityEntity.getId(),"Test Trip",false,stopIds));
    }
}