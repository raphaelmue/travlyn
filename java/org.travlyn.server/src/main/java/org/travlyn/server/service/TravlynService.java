package org.travlyn.server.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.travlyn.server.externalapi.access.DBpediaCityRequest;
import org.travlyn.shared.model.api.*;
import org.travlyn.shared.model.db.*;
import org.travlyn.util.security.Hash;
import org.travlyn.util.security.RandomString;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Math.toIntExact;


@Service
public class TravlynService {

    private final Logger logger = LoggerFactory.getLogger(TravlynService.class);
    private final RandomString tokenGenerator = new RandomString(64);

    @Autowired
    private SessionFactory sessionFactory;

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

            //return cached city
            return entity.toDataTransferObject();
        } catch (NoResultException noResult) {
            // city is not cached --> get from api
            DBpediaCityRequest request = new DBpediaCityRequest(city);
            City result = request.getResult();
            if (result != null) {
                // valid city was found --> cache result
                session.save(result.toEntity());
                return result;
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
    public Trip generateTrip(Long userId,Long cityId, String tripName, boolean privateFlag, List<Long> stopIds) throws NoResultException{
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
        city = session.createQuery("from CityEntity where id = :id", CityEntity.class)
                .setParameter("id", toIntExact(cityId))
                .getSingleResult();
        trip.setCity(city.toDataTransferObject());

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
    public Trip getTrip(Long tripId) throws NoResultException{
        Session session = sessionFactory.getCurrentSession();

        TripEntity tripEntity = new TripEntity();
        tripEntity = session.createQuery("from TripEntity where id = :id", TripEntity.class)
                    .setParameter("id", toIntExact(tripId))
                    .getSingleResult();
        return tripEntity.toDataTransferObject();
    }
}
