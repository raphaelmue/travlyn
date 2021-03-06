package org.travlyn.server.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.travlyn.server.ApiTest;
import org.travlyn.server.externalapi.access.*;
import org.travlyn.shared.model.api.*;
import org.travlyn.shared.model.db.*;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("unit")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TravlynServiceTest extends ApiTest {

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
    private TripEntity tripEntity;

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
        categoryEntity.setId(10);
        categoryEntity.setName("tourism");

        categoryEntity.setId((Integer) session.save(categoryEntity));

        cityEntity = new CityEntity();
        cityEntity.setName("Test city");
        cityEntity.setDescription("Test descr");
        cityEntity.setLongitude(8.4039514);
        cityEntity.setLatitude(49.0092097);
        cityEntity.setImage("https://testurl.com/test.jpg");

        cityEntity.setId((Integer) session.save(cityEntity));

        stopEntity = new StopEntity();
        stopEntity.setName("Test Stop");
        stopEntity.setDescription("Test descr");
        stopEntity.setAverageRating(2.0);
        stopEntity.setLatitude(49.009231);
        stopEntity.setLongitude(8.403905);
        stopEntity.setCategory(categoryEntity);
        stopEntity.setCity(cityEntity);

        stopEntity.setId((Integer) session.save(stopEntity));

        secondStopEntity = new StopEntity();
        secondStopEntity.setName("Second Test Stop");
        secondStopEntity.setDescription("Test descr");
        secondStopEntity.setAverageRating(2.0);
        secondStopEntity.setLatitude(49.007849);
        secondStopEntity.setLongitude(8.398887);
        secondStopEntity.setCategory(categoryEntity);
        secondStopEntity.setCity(cityEntity);

        secondStopEntity.setId((Integer) session.save(secondStopEntity));

        tripEntity = new TripEntity();
        tripEntity.setName("Test trip");
        tripEntity.setUser(userEntity);
        tripEntity.setCity(cityEntity);
        tripEntity.setPrivate(false);
        tripEntity.setRatings(new HashSet<>());
        tripEntity.setStops(new HashSet<>());
        tripEntity.setGeoTexts(new HashSet<>());

        tripEntity.setId((Integer) session.save(tripEntity));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
                new UsernamePasswordAuthenticationToken(userEntity, null));
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @Transactional
    public void testCheckCredentials() {
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
    public void testCheckUsersToken(){
        Session session = sessionFactory.getCurrentSession();
        //normal case
        Optional<UserEntity> optionalToAssert = service.checkUsersToken(tokenEntity.getToken());
        Assertions.assertTrue(optionalToAssert.isPresent());
        UserEntity userToAssert = optionalToAssert.get();
        Assertions.assertEquals(userEntity.getId(),userToAssert.getId());

        //invalid token
        optionalToAssert = service.checkUsersToken("invalidToken");
        Assertions.assertFalse(optionalToAssert.isPresent());


        //create outdated token
        TokenEntity outdatedToken = new TokenEntity();
        outdatedToken.setUser(userEntity);
        outdatedToken.setToken("6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949aj");
        outdatedToken.setIpAddress("192.168.0.1");
        outdatedToken.setExpireDate(LocalDate.now().minusMonths(1));

        outdatedToken.setId((Integer) session.save(outdatedToken));

        optionalToAssert = service.checkUsersToken(outdatedToken.getToken());
        Assertions.assertFalse(optionalToAssert.isPresent());
    }

    @Test
    @Transactional
    public void testGetCityWithInformation() throws Exception {
        enqueue("dbpedia-response-karlsruhe.json");
        enqueue("openroute-response-karlsruhe.json");
        for (int i = 0; i < 8; i++) enqueue("openroute-response-karlsruhe-empty.json");
        enqueue("dbpedia-response-karlsruhe_palace.json");
        enqueue("dbpedia-response-empty.json");

        final String url = startServer();
        DBpediaCityRequest.setBaseUrl(url);
        OpenRoutePOIRequest.setBaseUrl(url);
        DBpediaStopRequest.setBaseUrl(url);

        //valid search term
        City cityToAssert = service.getCityWithInformation("Karlsruhe");
        Assertions.assertNotNull(cityToAssert);
        Assertions.assertFalse(cityEntity.isUnfetchedStops());

        //test if caching is working
        Session session = sessionFactory.getCurrentSession();
        CityEntity result = session.createQuery("from CityEntity where name = :name", CityEntity.class)
                .setParameter("name", "Karlsruhe")
                .getSingleResult();
        Assertions.assertNotNull(result);

        //test if stops are present
        Assertions.assertEquals(1, result.getStops().size());

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
    public void testRegisterUser() {
        User userToAssert = service.registerUser("test2@email.com", "Second Test User", "password", "192.168.0.1");
        Assertions.assertTrue(userToAssert.getId() > 0);
        Assertions.assertNotNull(userToAssert.getToken().getToken());
        userToAssert = service.checkCredentials("test2@email.com", "password", "192.168.0.1");
        Assertions.assertEquals("Second Test User", userToAssert.getName());

        Assertions.assertThrows(NonUniqueResultException.class,
                () -> service.registerUser("test@email.com", "Second Test User", "password", "192.168.0.1"));

    }

    @Test
    @Transactional
    public void testUpdateUser() throws IllegalAccessException {
        Session session = sessionFactory.getCurrentSession();
        Set<Preference> preferences = new HashSet<>();
        preferences.add(new Preference().setCategory(categoryEntity.toDataTransferObject()).setUser(userEntity.toDataTransferObject()));
        User user = userEntity.toDataTransferObject();
        user.setPreferences(preferences);
        service.updateUser(user);

        PreferenceEntity preferenceEntity = session.createQuery("from PreferenceEntity",PreferenceEntity.class).getSingleResult();
        System.out.println("");
    }

    @Transactional
    @Test
    public void testGenerateTrip() {
        //normal case
        ArrayList<Long> stopIds = new ArrayList<>();
        stopIds.add((long) stopEntity.getId());
        stopIds.add((long) secondStopEntity.getId());
        Trip result = service.generateTrip(cityEntity.getId(), "Test Trip", false, stopIds);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(userEntity.getId(), result.getUser().getId());
        Assertions.assertEquals("Test Trip", result.getName());
        //check order
        for (int i = 0; i < result.getStops().size(); i++) {
            Assertions.assertEquals(stopIds.get(i), result.getStops().get(i).getId());
        }

        //trip without stops
        result = service.generateTrip(cityEntity.getId(), "Test Trip", true, null);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(userEntity.getId(), result.getUser().getId());
        Assertions.assertEquals("Test Trip", result.getName());
        Assertions.assertEquals(0, result.getStops().size());

        //user not found
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
                new UsernamePasswordAuthenticationToken(null, null));
        SecurityContextHolder.setContext(securityContext);
        Assertions.assertThrows(NoResultException.class, () -> service.generateTrip(cityEntity.getId(), "Test Trip", false, stopIds));
    }

    @Test
    @Transactional
    public void testGetTrip() {
        Trip trip = service.getTrip((long) tripEntity.getId());
        Assertions.assertEquals("Test trip", trip.getName());
        Assertions.assertEquals(userEntity.getId(), trip.getUser().getId());
        Assertions.assertEquals(cityEntity.getId(), trip.getCity().getId());
        Assertions.assertEquals(tripEntity.getStops().size(), trip.getStops().size());

        //not found
        Assertions.assertThrows(NoResultException.class, () -> service.getTrip((long) 2));

    }

    @Test
    @Transactional
    public void testGetTripPerUser() {
        List<Trip> trips = service.getTripsPerUser((long) userEntity.getId());
        Assertions.assertEquals(1, trips.size());
        Assertions.assertEquals("Test trip", trips.get(0).getName());

        //user not found
        Assertions.assertThrows(NoResultException.class, () -> service.getTripsPerUser(2L));
    }

    @Test
    @Transactional
    public void testGetTripPerCity() {
        List<Trip> trips = service.getTripsForCity((long) cityEntity.getId());
        Assertions.assertEquals(1, trips.size());
        Assertions.assertEquals("Test trip", trips.get(0).getName());

        //city not found
        Assertions.assertThrows(NoResultException.class, () -> service.getTripsForCity((long) (cityEntity.getId() + 100)));
    }

    @Test
    @Transactional
    public void testUpdateTrip() {
        //create updated trip
        TripEntity newTrip = new TripEntity();
        TripStopEntity tripStopEntity = new TripStopEntity();
        tripStopEntity.setPredecessor(null);
        tripStopEntity.setTrip(tripEntity);
        tripStopEntity.setStop(secondStopEntity);
        TripStopEntity.TripStopId tripStopId = new TripStopEntity.TripStopId();
        tripStopId.setTripId(tripEntity.getId());
        tripStopId.setStopId(secondStopEntity.getId());
        tripStopEntity.setTripStopId(tripStopId);
        HashSet<TripStopEntity> stops = new HashSet<>();
        stops.add(tripStopEntity);
        newTrip.setStops(stops);
        newTrip.setId(tripEntity.getId());
        newTrip.setName("Updated test Trip");
        newTrip.setRatings(new HashSet<>());
        newTrip.setGeoTexts(new HashSet<>());
        newTrip.setCity(cityEntity);
        newTrip.setUser(userEntity);

        //update and check
        service.updateTrip(newTrip.toDataTransferObject());
        Trip trip = service.getTrip((long) tripEntity.getId());

        Assertions.assertNotEquals("Test trip", trip.getName());
        Assertions.assertEquals("Updated test Trip", trip.getName());
        Assertions.assertEquals(1, trip.getStops().size());
        Assertions.assertEquals("Test descr", trip.getStops().get(0).getDescription());
    }

    @Transactional
    @Test
    public void testUpdatePricing() throws NoResultException {
        Session session = sessionFactory.getCurrentSession();

        Stop result = service.addPricingToStop(stopEntity.getId(), 10.0);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(10.0, result.getPricing());

        StopEntity entity = session.createQuery("from StopEntity where id = :id", StopEntity.class)
                .setParameter("id", stopEntity.getId())
                .getSingleResult();

        Assertions.assertEquals(10.0, entity.getPricing());

        Assertions.assertThrows(ValueException.class, () -> service.addPricingToStop(stopEntity.getId(), -20));
        Assertions.assertThrows(NoResultException.class, () -> service.addPricingToStop(-1, 20));

        result = service.addPricingToStop(stopEntity.getId(), 20);
        Assertions.assertEquals(15, result.getPricing());
    }

    @Transactional
    @Test
    public void testUpdateTimeEffort() throws NoResultException {
        Session session = sessionFactory.getCurrentSession();

        Stop result = service.addTimeEffortToStop(stopEntity.getId(), 2.0);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2.0, result.getTimeEffort());

        StopEntity entity = session.createQuery("from StopEntity where id = :id", StopEntity.class)
                .setParameter("id", stopEntity.getId())
                .getSingleResult();

        Assertions.assertEquals(2.0, entity.getTimeEffort());

        Assertions.assertThrows(ValueException.class, () -> service.addTimeEffortToStop(stopEntity.getId(), -20));
        Assertions.assertThrows(NoResultException.class, () -> service.addTimeEffortToStop(-1, 2.0));

        result = service.addTimeEffortToStop(stopEntity.getId(), 10);
        Assertions.assertEquals(6.0, result.getTimeEffort(),0.1);
    }

    @Transactional
    @Test
    public void testAddRatingToStop(){
        Session session = sessionFactory.getCurrentSession();
        Rating rating = new Rating().rating(0.5).description("Ok!").user(userEntity.toDataTransferObject());

        //normal case
        boolean result = service.addRatingToStop(stopEntity.getId(),rating);

        Assertions.assertTrue(result);
        StopEntity stopToAssert = session.createQuery("from StopEntity where id = :id",StopEntity.class)
                                        .setParameter("id",stopEntity.getId())
                                        .getSingleResult();
        Assertions.assertEquals(0.5,stopToAssert.getAverageRating());
        Assertions.assertEquals(1, stopToAssert.getRatings().size());
        StopRatingEntity ratingToAssert = stopToAssert.getRatings().iterator().next();
        Assertions.assertEquals("Ok!",ratingToAssert.getDescription());
        Assertions.assertEquals(0.5,ratingToAssert.getRating());

        //illegal stop id
        Assertions.assertThrows(NoResultException.class,() -> service.addRatingToStop(-1,rating));
    }

    @Transactional
    @Test
    public void testGetStopById(){
        Stop result = service.getStopById((long)stopEntity.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(stopEntity.getId(),result.getId());
        Assertions.assertEquals(stopEntity.getName(),result.getName());

        //invalid id
        result = service.getStopById(-1L);
        Assertions.assertNull(result);
    }

    @Transactional
    @Test
    public void testAddRatingToTrip() throws NoResultException{
        Session session = sessionFactory.getCurrentSession();
        //normal case
        Rating testRating = new Rating().rating(0.0001).user(userEntity.toDataTransferObject()).description("Sucks!");
        Assertions.assertTrue(service.addRatingToTrip( tripEntity.getId(),testRating));
        TripEntity entity = session.get(TripEntity.class,tripEntity.getId());
        Assertions.assertEquals(1, entity.getRatings().size());
        Assertions.assertEquals(0.0001,entity.getAverageRating());

        TripRatingEntity savedRating = session.createQuery("from TripRatingEntity where trip.id = :id",TripRatingEntity.class)
                .setParameter("id",tripEntity.getId())
                .getSingleResult();
        Assertions.assertNotNull(savedRating);
        Assertions.assertEquals(0.0001,savedRating.getRating());

        //try to set ratong for non existing trip
        Assertions.assertThrows(NoResultException.class,() ->service.addRatingToTrip(-1,testRating));

        //try to set rating for private trip without auth
        tripEntity.setPrivate(true);
        service.updateTrip(tripEntity.toDataTransferObject());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
                new UsernamePasswordAuthenticationToken(null, null));
        SecurityContextHolder.setContext(securityContext);
        Assertions.assertThrows(IllegalAccessError.class,() -> service.addRatingToTrip(tripEntity.getId(),testRating));
    }

    @Test
    @Transactional
    public void testGetExecutionInfo() throws Exception {
        //mock api response
        enqueue("openroute-response-triproute.json");
        final String url = startServer();
        OpenRouteDirectionRequest.setBaseUrl(url);

        ExecutionInfo infoToAssert = service.getExecutionInfo((long)tripEntity.getId(),(long)userEntity.getId(),cityEntity.getLatitude(),cityEntity.getLongitude(),false,true,"en");
        Assertions.assertEquals(1.5843, infoToAssert.getDistance());
        Assertions.assertEquals(19.011666666666667,infoToAssert.getDuration());
        Assertions.assertEquals(tripEntity.getId(),infoToAssert.getTripId());
        Assertions.assertEquals(85,infoToAssert.getWaypoints().size());
        Assertions.assertEquals(26, infoToAssert.getSteps().size());

        Assertions.assertThrows(NoResultException.class,()->service.getExecutionInfo((long)-1,(long)userEntity.getId(),cityEntity.getLatitude(),cityEntity.getLongitude(),false,true,"en"));
    }

    @Test
    @Transactional
    public void testGetRedirection() throws Exception{
        enqueue("openroute-response-redirection.json");
        OpenRouteRedirectionRequest.setBaseUrl(startServer());

        ExecutionInfo infoToAssert = service.getRedirection(cityEntity.getLatitude(),cityEntity.getLongitude(), (long) stopEntity.getId(),"en");
        Assertions.assertEquals(0.555, infoToAssert.getDistance());
        Assertions.assertEquals(35, infoToAssert.getWaypoints().size());

        Assertions.assertThrows(NoResultException.class,()->service.getRedirection(cityEntity.getLatitude(),cityEntity.getLongitude(),-1L,"en"));

    }
}