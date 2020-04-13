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
import org.travlyn.server.externalapi.access.OpenRouteRequest;
import org.travlyn.shared.model.api.*;
import org.travlyn.shared.model.db.*;
import org.travlyn.util.security.Hash;
import org.travlyn.util.security.RandomString;

import javax.persistence.NoResultException;
import java.util.Iterator;
import java.util.Set;


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


    @Transactional
    public User registerUser(String email, String name, String password, String ipAddress) {
        logger.info("Registering new user");

        final String salt = new RandomString(32).nextString();
        final String hashedPassword = Hash.create(password, salt);

        Session session = sessionFactory.getCurrentSession();
        UserEntity userEntity = new UserEntity()
                .setEmail(email)
                .setName(name)
                .setPassword(hashedPassword)
                .setSalt(salt);

        userEntity.setId((Integer) session.save(userEntity));
        Token token = this.generateToken(userEntity, ipAddress);
        return userEntity.toDataTransferObject().token(token);
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
            //City city1 = entity.toDataTransferObject();
            //city1.toEntity();
            return entity.toDataTransferObject();
        } catch (NoResultException noResult) {
            // city is not cached --> get from api
            DBpediaCityRequest request = new DBpediaCityRequest(city);
            City result = request.getResult();
            if (result != null) {
                //get Stops for city
                CityEntity entity;
                entity = this.getStopsForCity(result);
                // valid city was found --> cache result
                session.save(entity);
                return entity.toDataTransferObject();
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

    public CityEntity getStopsForCity(City city) {
        CityEntity cityEntity = new CityEntity();
        cityEntity.setName(city.getName());
        cityEntity.setLatitude(city.getLatitude());
        cityEntity.setLongitude(city.getLongitude());
        cityEntity.setImage(city.getImage());
        cityEntity.setDescription(city.getDescription());
        OpenRouteRequest request = new OpenRouteRequest(cityEntity.getLatitude(), cityEntity.getLongitude(), cityEntity);
        Set<StopEntity> stopEntities = request.getResult();
        for (Iterator<StopEntity> stopEntityIterator = stopEntities.iterator(); stopEntityIterator.hasNext(); ) {
            StopEntity entity = stopEntityIterator.next();
            DBpediaStopRequest poiRequest = new DBpediaStopRequest(entity.getName());
            Stop stop = poiRequest.getResult();
            if (stop != null) {
                entity.setImage(stop.getImage());
                entity.setDescription(stop.getDescription());
            } else {
                stopEntityIterator.remove();
            }
        }
        cityEntity.setStops(stopEntities);
        return cityEntity;
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
}
