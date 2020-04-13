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
import java.util.*;


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
            if (entity.isUnfetchedStops()){
                entity.setStops(this.fetch100Stops(entity.getStops()));
                City returnValue = this.removeUnfetchedStops(entity.toDataTransferObject());
                entity.setUnfetchedStops(returnValue.isUnfetchedStops());
                session.update(entity);
                return returnValue;
            }
            return entity.toDataTransferObject();
        } catch (NoResultException noResult) {
            // city is not cached --> get from api
            DBpediaCityRequest request = new DBpediaCityRequest(city);
            City result = request.getResult();
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
    protected CityEntity getStopsForCity(City city) {
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
        OpenRouteRequest request = new OpenRouteRequest(cityEntity.getLatitude(), cityEntity.getLongitude(), cityEntity,this.getCategorySetFromList(categories));
        Set<StopEntity> stopEntities = this.fetch100Stops(request.getResult());
        cityEntity.setStops(stopEntities);
        return cityEntity;
    }

    private Map<Integer,CategoryEntity> getCategorySetFromList(List<CategoryEntity> list){
        HashMap<Integer,CategoryEntity> result = new HashMap<>();
        for (CategoryEntity category : list) {
            result.put(category.getId(),category);
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
    protected Set<StopEntity> fetch100Stops(Set<StopEntity> entities){
        Session session = sessionFactory.getCurrentSession();
        int requestCount=1;
        Iterator<StopEntity> stopEntityIterator = entities.iterator();
        while(requestCount<100 && stopEntityIterator.hasNext()){
            StopEntity entity = stopEntityIterator.next();
            if (entity.getImage() != null && entity.getDescription() != null){
                continue;
            }
            DBpediaStopRequest poiRequest = new DBpediaStopRequest(entity.getName());
            Stop stop = poiRequest.getResult();
            if (stop != null) {
                entity.setImage(stop.getImage());
                entity.setDescription(stop.getDescription());
            } else {
                session.delete(entity);
                stopEntityIterator.remove();
            }
            requestCount ++;
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
}
