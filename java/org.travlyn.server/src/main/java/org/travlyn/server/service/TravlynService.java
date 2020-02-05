package org.travlyn.server.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.travlyn.shared.model.api.Token;
import org.travlyn.shared.model.api.User;
import org.travlyn.shared.model.db.TokenEntity;
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
     * @return User DTO if credentials are valid or null otherwise
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
                    return user.toDataTransferObject()
                            .token(generateToken(user));
                }
            }
        } catch (NoResultException ignored) {
        }

        logger.info("Credentials are incorrect.");
        return null;
    }

    /**
     * Generates a token object with a random token string for a given user and stores it in the database.
     *
     * @param user user to which the token belongs
     * @return generated Token DTO
     */
    @Transactional
    Token generateToken(UserEntity user) {
        logger.info("Generating token for user {} (id: {})", user.getName(), user.getId());

        Session session = sessionFactory.getCurrentSession();

        TokenEntity token = new TokenEntity();
        token.setUser(user);
        token.setToken(tokenGenerator.nextString());
        token.setIpAddress("");

        session.save(token);

        return token.toDataTransferObject();
    }
}
