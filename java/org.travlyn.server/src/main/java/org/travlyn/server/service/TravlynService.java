package org.travlyn.server.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.travlyn.server.externalapi.access.*;
import org.travlyn.shared.model.api.*;
import org.travlyn.shared.model.db.*;
import org.travlyn.util.security.Hash;
import org.travlyn.util.security.RandomString;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.time.LocalDate;
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

        Optional<UserEntity> userOptional = findUserByEmail(email);
        if (userOptional.isPresent()) {
            String hashedPassword = Hash.create(password, userOptional.get().getSalt());
            if (hashedPassword.equals(userOptional.get().getPassword())) {
                logger.info("Credentials of user {} (id: {}) are approved.",
                        userOptional.get().getName(), userOptional.get().getId());
                return userOptional.get().toDataTransferObject()
                        .token(generateToken(userOptional.get(), ipAddress));
            }
        }

        logger.info("Credentials are incorrect.");
        return null;
    }

    private Optional<UserEntity> findUserByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        try {
            return Optional.of(session.createQuery("from UserEntity where email = :email", UserEntity.class)
                    .setParameter("email", email)
                    .getSingleResult());
        } catch (NoResultException ignored) {
        }
        return Optional.empty();
    }


    @Transactional
    public User registerUser(String email, String name, String password, String ipAddress) {
        logger.info("Registering new user");

        final String salt = new RandomString(32).nextString();
        final String hashedPassword = Hash.create(password, salt);

        if (findUserByEmail(email).isPresent()) {
            throw new NonUniqueResultException("Email is already is use");
        }

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

    @Transactional
    public void updateUser(User newUser) throws IllegalAccessException {
        Session session = sessionFactory.getCurrentSession();
        //get corresponding user
        UserEntity user;
        user = session.get(UserEntity.class, getUserId());

        if(user.getId() != newUser.getId()){
            //user is trying to change other user--> not allowed
            throw new IllegalAccessException();
        }
        session.clear();

        session.merge(newUser.toEntity());
    }

    /**
     * Checks whether the given token is valid (not expired and existing) and returns the respective user if so.
     *
     * @param token token string to validate
     * @return user object if token is valid
     */
    @Transactional
    public Optional<UserEntity> checkUsersToken(String token) {
        Session session = sessionFactory.getCurrentSession();

        try {
            TokenEntity tokenEntity = session.createQuery("from TokenEntity where token = :token", TokenEntity.class)
                    .setParameter("token", token).getSingleResult();

            // check if token is not expired
            // !isBefore is equal to isAfter && isEqual
            if (!tokenEntity.getExpireDate().isBefore(LocalDate.now())) {
                tokenEntity.setExpireDate(LocalDate.now().plusMonths(1));
                session.update(tokenEntity);
                return Optional.of(tokenEntity.getUser());
            }
        } catch (NoResultException ignored) {
        }

        return Optional.empty();
    }

    /**
     * Returns the authenticated user object, if exists.
     *
     * @return user object
     */
    public Optional<UserEntity> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null && !(authentication.getPrincipal() instanceof String)) {
            return Optional.of((UserEntity) authentication.getPrincipal());
        }
        return Optional.empty();
    }

    public int getUserId() {
        Optional<UserEntity> userOptional = getAuthenticatedUser();
        return userOptional.map(UserEntity::getId).orElse(-1);
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
            City cityDTO = entity.toDataTransferObject();
            cityDTO.setStops(this.reorderStopsAccordingToPreferences(cityDTO.getStops()));
            return cityDTO;
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
                session.persist(entity);
                City returnValue = this.removeUnfetchedStops(entity.toDataTransferObject());
                entity.setUnfetchedStops(returnValue.isUnfetchedStops());
                // valid city was found --> cache result
                session.update(entity);
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
        token.setExpireDate(LocalDate.now().plusMonths(1));
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
    public Trip generateTrip(int cityId, String tripName, boolean privateFlag, List<Long> stopIds) throws NoResultException {
        Session session = sessionFactory.getCurrentSession();
        Trip trip = new Trip();

        //get corresponding user
        UserEntity user;
        user = session.get(UserEntity.class, getUserId());

        if (user == null) {
            throw new NoResultException(String.format("No user entity found with ID %s", getUserId()));
        }

        trip.setUser(user.toDataTransferObject());

        //get corresponding city
        CityEntity city;
        try {
            city = session.get(CityEntity.class, cityId);
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

        TripEntity tripEntity;
        tripEntity = session.createQuery("from TripEntity where id = :id", TripEntity.class)
                .setParameter("id", toIntExact(tripId))
                .getSingleResult();

        if (tripEntity.isPrivate() && tripEntity.getUser().getId() != getUserId()) {
            throw new IllegalAccessError("The trip is private and cannot be access by another user.");
        }

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
        List<Stop> stops = city.getStops();
        boolean removed = stops.removeIf(stop -> stop.getDescription() == null && stop.getImage() == null);
        city.setUnfetchedStops(removed);
        return city;
    }

    @Transactional
    public List<Stop> reorderStopsAccordingToPreferences(List<Stop> stops) {
        Optional<UserEntity> user = getAuthenticatedUser();
        if (user.isEmpty()){
            return stops;
        }
        UserEntity userEntity = user.get();
        Set<PreferenceEntity> preferences = userEntity.getPreferences();
        Set<Integer> categoryIds = new HashSet<>();
        preferences.forEach(preferenceEntity -> categoryIds.add(preferenceEntity.getCategoryEntity().getId()));

        for (int i = 0; i<stops.size();i++){
            if (categoryIds.contains(stops.get(i).getCategory().getId())){
                Stop stop = stops.remove(i);
                stops.add(0,stop);
            }
        }
        return stops;
    }

    @Transactional
    protected Set<StopEntity> fetchNumberOfStops(Set<StopEntity> entities) {
        Session session = sessionFactory.getCurrentSession();
        Optional<UserEntity> userOptional = getAuthenticatedUser();
        Set<StopEntity> preferredStops = new HashSet<>();
        if (userOptional.isPresent()){
            UserEntity userEntity = userOptional.get();
            Set<PreferenceEntity> preferences = userEntity.getPreferences();
            Set<Integer> categoryIds = new HashSet<>();
            preferences.forEach(preferenceEntity -> categoryIds.add(preferenceEntity.getCategoryEntity().getId()));
            for (Iterator<StopEntity> entityIterator = entities.iterator(); entityIterator.hasNext();){
                StopEntity stopEntity = entityIterator.next();
                if (categoryIds.contains(stopEntity.getCategory().getId())){
                    entityIterator.remove();
                    preferredStops.add(stopEntity);
                }
            }
        }
        int requestCount = 2;
        Iterator<StopEntity> stopEntityIterator = entities.iterator();
        Iterator<StopEntity> preferredStopIterator = preferredStops.iterator();
        while (requestCount < FETCHABLESTOPS && (preferredStopIterator.hasNext() || stopEntityIterator.hasNext())) {
            StopEntity entity;
            boolean preferredStop;
            if (preferredStopIterator.hasNext()){
                entity = preferredStopIterator.next();
                preferredStop = true;
            }else{
                entity = stopEntityIterator.next();
                preferredStop = false;
            }
            if (entity.getImage() != null && entity.getDescription() != null) {
                continue;
            }
            DBpediaStopRequest poiRequest = new DBpediaStopRequest(entity.getName());
            Stop stop;
            try {
                stop = poiRequest.getResult();
            } catch (QuotaLimitException e) {
                logger.error(e.getMessage());
                break;
            }
            if (stop != null) {
                entity.setImage(stop.getImage());
                entity.setDescription(stop.getDescription());
            } else {
                session.delete(entity);
                if (preferredStop){
                    preferredStopIterator.remove();
                }else{
                    stopEntityIterator.remove();
                }
            }
            requestCount++;
        }
        entities.addAll(preferredStops);
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

    /**
     * Adds a new pricing suggestion to a Stop.
     * @param stopId Id of Stop that should be updated
     * @param pricing Suggested pricing value
     * @return Updated Stop DTO
     * @throws NoResultException If passed Stop Id is not associated with a Stop
     * @throws ValueException If suggested pricing value is invalid
     */
    @Transactional
    public Stop addPricingToStop(int stopId, double pricing) throws NoResultException {
        Session session = sessionFactory.getCurrentSession();

        if (pricing < 0) {
            throw new ValueException("Pricing can not be negative!");
        }

        StopEntity stopEntity = session.createQuery("from StopEntity where id = :stopId", StopEntity.class)
                .setParameter("stopId", stopId)
                .getSingleResult();
        double oldPricing = stopEntity.getPricing();
        int numberOfCommitments = stopEntity.getNumberOfPricingCommitments();

        if (numberOfCommitments == 0) {
            //pricing initial
            stopEntity.setPricing(pricing);
        } else {
            //calc avg and set;
            stopEntity.setPricing((1.0 / (numberOfCommitments + 1)) * ((numberOfCommitments * oldPricing) + pricing));
        }
        stopEntity.setNumberOfPricingCommitments(numberOfCommitments + 1);
        session.merge(stopEntity);
        return stopEntity.toDataTransferObject();
    }

    /**
     * Adds a new time effort suggestion to a Stop.
     * @param stopId Id of Stop that should be updated
     * @param timeEffort Suggested time effort
     * @return Updated Stop DTO
     * @throws NoResultException If passed Stop Id is not associated with a Stop
     * @throws ValueException If suggested time effort value is invalid
     */
    @Transactional
    public Stop addTimeEffortToStop(int stopId, double timeEffort) throws NoResultException {
        Session session = sessionFactory.getCurrentSession();

        if (timeEffort < 0 || timeEffort > 32) {
            throw new ValueException("Time effort is invalid");
        }

        StopEntity stopEntity = session.createQuery("from StopEntity where id = :stopId", StopEntity.class)
                .setParameter("stopId", stopId)
                .getSingleResult();
        double oldTimeEffort = stopEntity.getTimeEffort();
        int numberOfCommitments = stopEntity.getNumberOfTimeEffortCommitments();

        if ( numberOfCommitments == 0) {
            //time effort initial
            stopEntity.setTimeEffort(timeEffort);
        } else {
            //calc avg and set;
            stopEntity.setTimeEffort((1.0 / (numberOfCommitments+1)) * ((numberOfCommitments * oldTimeEffort) + timeEffort));
        }
        stopEntity.setNumberOfTimeEffortCommitments(numberOfCommitments + 1);
        session.merge(stopEntity);
        return stopEntity.toDataTransferObject();
    }

    @Transactional
    public List<Trip> getTripsForCity(Long cityId) throws NoResultException {
        Session session = sessionFactory.getCurrentSession();
        //check if city exists --> throws exception if not
        session.createQuery("from CityEntity where id = :id")
                .setParameter("id", toIntExact(cityId))
                .getSingleResult();

        //city is present...search corresponding trips
        List<TripEntity> result = session.createQuery("from TripEntity where city.id = :cityId and " +
                "(isPrivate = false or user.id = :userId)", TripEntity.class)
                .setParameter("cityId", toIntExact(cityId))
                .setParameter("userId", getUserId())
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

        if (getAuthenticatedUser().isEmpty()) {
            throw new IllegalAccessError("User must be authenticated to perform this operation.");
        }

        //check if trip exists
        TripEntity oldTrip = session.get(TripEntity.class, trip.getId());

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
            if (!entity.isPrivate() || userId == getUserId()) {
                trips.add(entity.toDataTransferObject());
            }
        }
        return trips;
    }

    @Transactional
    public boolean addRatingToTrip(int tripId, Rating rating) throws IllegalAccessError{
        Session session = sessionFactory.getCurrentSession();
        TripEntity trip = session.get(TripEntity.class,tripId);
        if(trip == null){
            throw new NoResultException();
        }
        Optional<UserEntity> user = this.getAuthenticatedUser();

        if (trip.isPrivate() && (user.isEmpty() || trip.getUser().getId() != user.get().getId())){
            throw  new IllegalAccessError();
        }

        Set<TripRatingEntity> ratingEntities = trip.getRatings();
        TripRatingEntity tripRating = rating.toTripEntity(trip);
        session.save(tripRating);

        if (ratingEntities.isEmpty()) {
            // new rating is equal to average rating when the list of ratings is empty
            trip.setAverageRating(tripRating.getRating());
        } else {
            // calculate new average rating including the new rating
            int numberOfRatings = ratingEntities.size();
            trip.setAverageRating((numberOfRatings * trip.getAverageRating() + tripRating.getRating()) /
                    (numberOfRatings + 1));
        }

        ratingEntities.add(tripRating);
        trip.setRatings(ratingEntities);

        session.merge(trip);
        return true;

    }

    @Transactional
    public ExecutionInfo getExecutionInfo(Long tripId, Long userId, double lat, double lon, boolean reorder, boolean isRoundtrip, String lang) throws NoResultException {
        Session session = sessionFactory.getCurrentSession();
        TripEntity tripEntity = session.createQuery("from TripEntity where id = :id", TripEntity.class)
                .setParameter("id", toIntExact(tripId))
                .getSingleResult();

        Trip trip = tripEntity.toDataTransferObject();
        if (reorder) {
            List<Stop> stops = trip.getStops();
            // add stop that represents the position of the user
            stops.add(0, new Stop().id(-1).latitude(lat).longitude(lon));
            trip.setStops(new TSPSolver(stops).solve());
        }
        OpenRouteTripDirectionRequest directionRequest = new OpenRouteTripDirectionRequest(trip, isRoundtrip, lang);
        return directionRequest.getResult();
    }

    @Transactional
    public ExecutionInfo getRedirection(double lat, double lon, Long stopId, String lang) throws NoResultException {
        Session session = sessionFactory.getCurrentSession();
        StopEntity stop = session.createQuery("from StopEntity where id = :id", StopEntity.class)
                .setParameter("id", toIntExact(stopId))
                .getSingleResult();
        OpenRouteRedirectionRequest redirectionRequest = new OpenRouteRedirectionRequest(lat, lon, stop.toDataTransferObject(), lang);
        return redirectionRequest.getResult();
    }
}
