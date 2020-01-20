package org.travlyn.server.service;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.travlyn.server.db.HibernateUtil;
import org.travlyn.server.db.model.CityEntity;
import org.travlyn.server.db.model.TripEntity;
import org.travlyn.server.db.model.TripStopEntity;
import org.travlyn.server.db.model.UserEntity;
import org.travlyn.server.model.City;
import org.travlyn.server.model.Stop;

import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

@Tag("unit")
public class TravlynServiceTest {

    private Session session;

    @BeforeAll
    public static void beforeAll() throws IOException {
        Properties testProperties = new Properties();
        testProperties.load(TravlynServiceTest.class.getResourceAsStream("/test.properties"));
        HibernateUtil.setDatabaseProperties(testProperties);
    }

    @BeforeEach
    public void beforeEach() {
        session = HibernateUtil.getSessionFactory().openSession();

        UserEntity userEntity = new UserEntity();
        userEntity.setName("Test User");
        userEntity.setEmail("test@test.com");
        userEntity.setPassword("6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af");
        userEntity.setSalt("I2HoOYJmqKfGboyJAdCEQwulUkxmhVH5");

        CityEntity cityEntity = new City().image("testURL").name("TestCity").toEntity();

        Transaction transaction = session.beginTransaction();
        session.save(userEntity);
        session.save(cityEntity);
        transaction.commit();

        TripEntity tripEntity = new TripEntity();

        Set<TripStopEntity> tripStopEntities = new HashSet<>();
        TripStopEntity tripStopEntity1 = new TripStopEntity();
        tripStopEntity1.setTrip(tripEntity);
        tripStopEntity1.setStop(new Stop().longitude(1).latitude(2).name("TestStop1").description("Description").toEntity());
        tripStopEntity1.setIndex(1);
        tripStopEntities.add(tripStopEntity1);
        TripStopEntity tripStopEntity2 = new TripStopEntity();
        tripStopEntity2.setTrip(tripEntity);
        tripStopEntity2.setStop(new Stop().longitude(2).latitude(1).name("TestStop2").description("Description").toEntity());
        tripStopEntity2.setIndex(2);
        tripStopEntities.add(tripStopEntity2);

        tripEntity.setPrivate(false);
        tripEntity.setUser(userEntity);
        tripEntity.setCity(cityEntity);
        tripEntity.setStops(tripStopEntities);


        transaction = session.beginTransaction();
        session.save(tripEntity);
        transaction.commit();

        session.close();
        session = HibernateUtil.getSessionFactory().openSession();
    }

    @Test
    public void testTripModel() {
        Transaction transaction = session.beginTransaction();
        Assertions.assertEquals(2, session.get(TripEntity.class, 1).getStops().size());
        transaction.commit();
    }

    @AfterEach
    public void afterEach() {
        session.close();
    }
}
