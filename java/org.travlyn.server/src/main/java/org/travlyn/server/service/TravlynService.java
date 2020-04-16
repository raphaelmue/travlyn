package org.travlyn.server.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.travlyn.server.externalapi.access.DBpediaCityRequest;
import org.travlyn.server.externalapi.access.DBpediaStopRequest;
import org.travlyn.server.externalapi.access.OpenRoutePOIRequest;
import org.travlyn.server.externalapi.access.QuotaLimitException;
import org.travlyn.shared.model.api.*;
import org.travlyn.shared.model.db.*;
import org.travlyn.util.security.Hash;
import org.travlyn.util.security.RandomString;

import javax.persistence.NoResultException;
import java.util.*;

import static java.lang.Math.toIntExact;


@Service
public class TravlynService {

    private final Logger logger = LoggerFactory.getLogger(TravlynService.class);
    private final RandomString tokenGenerator = new RandomString(64);

    @Autowired
    private SessionFactory sessionFactory;

    public static final int FETCHABLESTOPS = 100;

    public TravlynService() {
    }

    /**
     * Checks the users credentials. Returns true, if the credentials are correct.
     *
     * @param email    email to verify
     * @param password password to verify
     * @return User DTO if credentials are valid or null otherwise
     */
    @Transactional()
    public User checkCredentials(String email, String password, String ipAddress) {
        logger.info("Checking credentials ...");

        Session session = sessionFactory.getCurrentSession();
        UserEntity user;

        try {
            user = session.createQuery("from UserEntity where email = :email", UserEntity.class)
                    .setParameter("email", email)
                    .getSingleResult();
            if (user != null) {
                String hashedPassword = Hash.create(password, user.getSalt());
                if (hashedPassword.equals(user.getPassword())) {
                    logger.info("Credentials of user {} (id: {}) are approved.", user.getName(), user.getId());
                    return user.toDataTransferObject()
                            .token(generateToken(user, ipAddress));
                }
            }
        } catch (NoResultException ignored) {
        }

        logger.info("Credentials are incorrect.");
        return null;
    }

    /**
     * Searches for city by name using db cache / DBmedia and returns city object with all infos.
     *
     * @param city Name of city that should be searched for
     * @return City with description, thumbnail, ...
     */
    @Transactional
    public City getCityWithInformation(String city) {
        Session session = sessionFactory.getCurrentSession();
        try {
            CityEntity entity = session.createQuery("from CityEntity where name = :name", CityEntity.class)
                    .setParameter("name", city)
                    .getSingleResult();
            if (entity.isUnfetchedStops()) {
                entity.setStops(this.fetchNumberOfStops(entity.getStops()));
                City returnValue = this.removeUnfetchedStops(entity.toDataTransferObject());
                entity.setUnfetchedStops(returnValue.isUnfetchedStops());
                session.update(entity);
                return returnValue;
            }
            return entity.toDataTransferObject();
        } catch (NoResultException noResult) {
            // city is not cached --> get from api
            DBpediaCityRequest request = new DBpediaCityRequest(city);
            City result;
            try {
                result = request.getResult();
            } catch (QuotaLimitException e) {
                logger.error(e.getMessage());
                return null;
            }
            if (result != null) {
                //get Stops for city
                CityEntity entity;
                entity = this.getStopsForCity(result);
                City returnValue = this.removeUnfetchedStops(entity.toDataTransferObject());
                entity.setUnfetchedStops(returnValue.isUnfetchedStops());
                // valid city was found --> cache result
                session.persist(entity);
                return returnValue;
            }
        }
        return null;
    }

    /**
     * Generates a token object with a random token string for a given user and stores it in the database.
     *
     * @param user user to which the token belongs
     * @return generated Token DTO
     */
    @Transactional
    Token generateToken(UserEntity user, String ipAddress) {
        logger.info("Generating token for user {} (id: {}).", user.getName(), user.getId());

        Session session = sessionFactory.getCurrentSession();

        TokenEntity token = new TokenEntity();
        token.setUser(user);
        token.setToken(tokenGenerator.nextString());
        token.setIpAddress(ipAddress);

        session.save(token);

        return token.toDataTransferObject();
    }

    /**
     * Deletes the respective token from database in order to log the user out.
     *
     * @param user User to log out
     */
    @Transactional
    public void logoutUser(User user) {
        logger.info("Logging out user {} (id: {}) from {}.", user.getName(), user.getId(), user.getToken().getIpAddress());

        Session session = sessionFactory.getCurrentSession();
        session.delete(user.getToken().toEntity());
    }

    @Transactional
    public Trip generateTrip(Long userId, Long cityId, String tripName, boolean privateFlag, List<Long> stopIds) throws NoResultException {
        Session session = sessionFactory.getCurrentSession();
        Trip trip = new Trip();

        //get corresponding user
        UserEntity user;
        user = session.createQuery("from UserEntity where id = :id", UserEntity.class)
                .setParameter("id", toIntExact(userId))
                .getSingleResult();
        trip.setUser(user.toDataTransferObject());

        //get corresponding city
        CityEntity city;
        try {
            city = session.createQuery("from CityEntity where id = :id", CityEntity.class)
                    .setParameter("id", toIntExact(cityId))
                    .getSingleResult();
            trip.setCity(city.toDataTransferObject());
        } catch (NoResultException e) {
            trip.setCity(null);
        }

        //set trip metadata and save trip
        trip.setPrivate(privateFlag);
        trip.name(tripName);
        trip.setRatings(new ArrayList<>());
        trip.setGeoText(new ArrayList<>());
        trip.setStops(new ArrayList<>());
        TripEntity tripEntity = trip.toEntity();
        tripEntity.setId((Integer) session.save(tripEntity));

        //create tripstops and save them
        StopEntity stop;
        TripStopEntity predecessor = null;
        Set<TripStopEntity> stopEntitySet = new HashSet<>();
        if (stopIds != null) {
            for (Long stopId : stopIds) {
                stop = session.createQuery("from StopEntity where id = :id", StopEntity.class)
                        .setParameter("id", toIntExact(stopId))
                        .getSingleResult();
                TripStopEntity tripStopEntity = new TripStopEntity();
                TripStopEntity.TripStopId tripStopId = new TripStopEntity.TripStopId();
                tripStopId.setStopId(stop.getId());
                tripStopId.setTripId(tripEntity.getId());
                tripStopEntity.setTripStopId(tripStopId);
                tripStopEntity.setTrip(tripEntity);
                tripStopEntity.setStop(stop);
                tripStopEntity.setPredecessor(predecessor);
                stopEntitySet.add(tripStopEntity);
                session.save(tripStopEntity);
                predecessor = tripStopEntity;
            }
        }
        tripEntity.setStops(stopEntitySet);
        return tripEntity.toDataTransferObject();
    }

    @Transactional
    public Trip getTrip(Long tripId) throws NoResultException {
        Session session = sessionFactory.getCurrentSession();

        TripEntity tripEntity = new TripEntity();
        tripEntity = session.createQuery("from TripEntity where id = :id", TripEntity.class)
                .setParameter("id", toIntExact(tripId))
                .getSingleResult();
        return tripEntity.toDataTransferObject();
    }

    @Transactional
    public CityEntity getStopsForCity(City city) {
        Session session = sessionFactory.getCurrentSession();
        CityEntity cityEntity = new CityEntity();
        cityEntity.setName(city.getName());
        cityEntity.setLatitude(city.getLatitude());
        cityEntity.setLongitude(city.getLongitude());
        cityEntity.setImage(city.getImage());
        cityEntity.setDescription(city.getDescription());
        //fetch categories and pass for reuse
        List<CategoryEntity> categories = session.createQuery("from CategoryEntity", CategoryEntity.class)
                .getResultList();
        OpenRoutePOIRequest request = new OpenRoutePOIRequest(cityEntity.getLatitude(), cityEntity.getLongitude(), cityEntity, this.getCategorySetFromList(categories));
        Set<StopEntity> stopEntities = this.fetchNumberOfStops(request.getResult());
        cityEntity.setStops(stopEntities);
        return cityEntity;
    }

    private Map<Integer, CategoryEntity> getCategorySetFromList(List<CategoryEntity> list) {
        HashMap<Integer, CategoryEntity> result = new HashMap<>();
        for (CategoryEntity category : list) {
            result.put(category.getId(), category);
        }
        return result;
    }

    private City removeUnfetchedStops(City city) {
        Set<Stop> stops = city.getStops();
        boolean removed = stops.removeIf(stop -> stop.getDescription() == null && stop.getImage() == null);
        city.setUnfetchedStops(removed);
        return city;
    }

    @Transactional
    protected Set<StopEntity> fetchNumberOfStops(Set<StopEntity> entities) {
        Session session = sessionFactory.getCurrentSession();
        int requestCount = 2;
        Iterator<StopEntity> stopEntityIterator = entities.iterator();
        while (requestCount < FETCHABLESTOPS && stopEntityIterator.hasNext()) {
            StopEntity entity = stopEntityIterator.next();
            if (entity.getImage() != null && entity.getDescription() != null) {
                continue;
            }
            DBpediaStopRequest poiRequest = new DBpediaStopRequest(entity.getName());
            Stop stop;
            try {
                stop = poiRequest.getResult();
            } catch (QuotaLimitException e) {
                logger.error(e.getMessage());
                return null;
            }
            if (stop != null) {
                entity.setImage(stop.getImage());
                entity.setDescription(stop.getDescription());
            } else {
                session.delete(entity);
                stopEntityIterator.remove();
            }
            requestCount++;
        }
        return entities;
    }

    @Transactional
    public Stop getStopById(Long stopId) {
        Session session = sessionFactory.getCurrentSession();
        try {
            StopEntity entity = session.createQuery("from StopEntity where id = :id", StopEntity.class)
                    .setParameter("id", Math.toIntExact(stopId))
                    .getSingleResult();
            return entity.toDataTransferObject();
        } catch (NoResultException noResult) {
            return null;
        }
    }

    @Transactional
    public boolean addRatingToStop(int stopId, Rating rating) throws NoResultException {
        Session session = sessionFactory.getCurrentSession();
        StopEntity stopEntity = session.createQuery("from StopEntity where id = :stopId", StopEntity.class)
                .setParameter("stopId", stopId)
                .getSingleResult();

        Set<StopRatingEntity> ratings = stopEntity.getRatings();
        StopRatingEntity stopRatingEntity = rating.toStopEntity(stopEntity);
        session.save(stopRatingEntity);
        if (stopEntity.getRatings().isEmpty()) {
            // new rating is equal to average rating when the list of ratings is empty
            stopEntity.setAverageRating(stopRatingEntity.getRating());
        } else {
            // calculate new average rating including the new rating
            int numberOfRatings = stopEntity.getRatings().size();
            stopEntity.setAverageRating((numberOfRatings * stopEntity.getAverageRating() + stopRatingEntity.getRating()) /
                    (numberOfRatings + 1));
        }

        ratings.add(stopRatingEntity);
        stopEntity.setRatings(ratings);

        session.merge(stopEntity);
        return true;
    }

    @Transactional
    public List<Trip> getTripsForCity(Long cityId) throws NoResultException {
        Session session = sessionFactory.getCurrentSession();
        //check if city exists --> throws exception if not
        session.createQuery("from CityEntity where id = :id")
                .setParameter("id", toIntExact(cityId))
                .getSingleResult();

        //city is present...search corresponding trips
        List<TripEntity> result = session.createQuery("from TripEntity where city.id = :cityId and isPrivate = false", TripEntity.class)
                .setParameter("cityId", toIntExact(cityId))
                .getResultList();
        ArrayList<Trip> trips = new ArrayList<>();
        for (TripEntity entity : result) {
            trips.add(entity.toDataTransferObject());
        }
        return trips;
    }

    @Transactional
    public void updateTrip(Trip trip) throws NoResultException {
        Session session = sessionFactory.getCurrentSession();
        //check if trip exists
        TripEntity oldTrip = session.createQuery("from TripEntity where id = :id", TripEntity.class)
                .setParameter("id", trip.getId())
                .getSingleResult();

        for (TripStopEntity stop : oldTrip.getStops()) {
            stop.setPredecessor(null);
            session.update(stop);
        }

        session.createQuery("delete from TripStopEntity where tripStopId.tripId = :tripId ")
                .setParameter("tripId", trip.getId()).executeUpdate();

        session.clear();
        TripEntity tripEntity = trip.toEntity();

        if (tripEntity.getCity() == null) {
            StopEntity stopEntity = session.get(StopEntity.class, tripEntity.getStops().iterator().next().getStop().getId());
            tripEntity.setCity(stopEntity.getCity());
        }

        session.update(tripEntity);
    }

    @Transactional
    public List<Trip> getTripsPerUser(Long userId) throws NoResultException {
        Session session = sessionFactory.getCurrentSession();
        //check if user exists --> throws exception if not
        session.createQuery("from UserEntity where id = :id")
                .setParameter("id", toIntExact(userId))
                .getSingleResult();

        //user present..get trips
        List<TripEntity> result = session.createQuery("from TripEntity where user.id = :userId", TripEntity.class)
                .setParameter("userId", toIntExact(userId))
                .getResultList();
        ArrayList<Trip> trips = new ArrayList<>();
        for (TripEntity entity : result) {
            trips.add(entity.toDataTransferObject());
        }
        return trips;
    }
}
