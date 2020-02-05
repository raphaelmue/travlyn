package org.travlyn.server.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.travlyn.server.externalapi.DBpediaCityRequest;
import org.travlyn.shared.model.api.City;
import org.travlyn.shared.model.api.User;
import org.travlyn.shared.model.db.UserEntity;
import org.travlyn.util.security.Hash;
import org.travlyn.util.security.RandomString;

import javax.persistence.NoResultException;


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
     * @return true if credentials are correct
     */
    @Transactional(readOnly = true)
    public User checkCredentials(String email, String password) {
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
                    return user.toDataTransferObject();
                }
            }
        } catch (NoResultException ignored) {
        }

        logger.info("Credentials are incorrect.");
        return null;
    }

    /**
     * Searches for city by name using DBmedia and returns city object with all infos.
     * @param cityName Name of city that should be searched for
     * @return City with description, thumbnail, ...
     */
    public City getCityWithInformation(String cityName) {
        DBpediaCityRequest request = new DBpediaCityRequest(cityName);
        return request.getResult();
    }
}
