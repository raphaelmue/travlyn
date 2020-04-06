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
import org.travlyn.shared.model.api.City;
import org.travlyn.shared.model.api.Token;
import org.travlyn.shared.model.api.User;
import org.travlyn.shared.model.db.CityEntity;
import org.travlyn.shared.model.db.StopEntity;
import org.travlyn.shared.model.db.TokenEntity;
import org.travlyn.shared.model.db.UserEntity;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Before
    @Transactional
    public void setup() {
        Session session = sessionFactory.getCurrentSession();
        userEntity = new UserEntity();
        userEntity.setName("Test User");
        userEntity.setEmail("test@email.com");
        userEntity.setPassword("6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af");
        userEntity.setSalt("I2HoOYJmqKfGboyJAdCEQwulUkxmhVH5");

        session.save(userEntity);

        tokenEntity = new TokenEntity();
        tokenEntity.setUser(userEntity);
        tokenEntity.setToken("6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af");
        tokenEntity.setIpAddress("192.168.0.1");
        tokenEntity.setExpireDate(LocalDate.now().plusMonths(1));

        session.save(tokenEntity);
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
        City cityToAssert = service.getCityWithInformation("London");
        Assertions.assertNotNull(cityToAssert);
        Assertions.assertEquals("London /ˈlʌndən/ is the capital and most populous city of England and the United Kingdom. Standing on the River Thames in the south east of the island of Great Britain, London has been a major settlement for two millennia. It was founded by the Romans, who named it Londinium. London's ancient core, the City of London, largely retains its 1.12-square-mile (2.9 km2) medieval boundaries. Since at least the 19th century, \"London\" has also referred to the metropolis around this core, historically split between Middlesex, Essex, Surrey, Kent, and Hertfordshire, which today largely makes up Greater London, governed by the Mayor of London and the London Assembly. As a Nylonkong metropolis, London is a leading global city, in the arts, commerce, education, entertainment, fashion, finance, healthcare, media, professional services, research and development, tourism, and transport. It is one of the world's leading financial centres and has the fifth- or sixth-largest metropolitan area GDP in the world. London is a world cultural capital. It is the world's most-visited city as measured by international arrivals and has the world's largest city airport system measured by passenger traffic. London is the world's leading investment destination, hosting more international retailers and ultra high-net-worth individuals than any other city. London's universities form the largest concentration of higher education institutes in Europe, and a 2014 report placed it first in the world university rankings. According to the report London also ranks first in the world in software, multimedia development and design, and shares first position in technology readiness. In 2012, London became the only city to host the modern Summer Olympic Games three times. London has a diverse range of people and cultures, and more than 300 languages are spoken in the region. Its estimated mid-2015 municipal population (corresponding to Greater London) was 8,673,713, the largest of any city in the European Union, and accounting for 12.5 per cent of the UK population. London's urban area is the second most populous in the EU, after Paris, with 9,787,426 inhabitants at the 2011 census. The city's metropolitan area is one of the most populous in Europe with 13,879,757 inhabitants, while the Greater London Authority states the population of the city-region (covering a large part of the south east) as 22.7 million.London was the world's most populous city from around 1831 to 1925. London contains four World Heritage Sites: the Tower of London; Kew Gardens; the site comprising the Palace of Westminster, Westminster Abbey, and St Margaret's Church; and the historic settlement of Greenwich (in which the Royal Observatory, Greenwich marks the Prime Meridian, 0° longitude, and GMT). Other famous landmarks include Buckingham Palace, the London Eye, Piccadilly Circus, St Paul's Cathedral, Tower Bridge, Trafalgar Square, and The Shard. London is home to numerous museums, galleries, libraries, sporting events and other cultural institutions, including the British Museum, National Gallery, Natural History Museum, Tate Modern, British Library and West End theatres. The London Underground is the oldest underground railway network in the world.", cityToAssert.getDescription());
        Assertions.assertEquals("http://commons.wikimedia.org/wiki/Special:FilePath/London_Montage_L.jpg?width=300", cityToAssert.getImage());

        //test if caching is working
        Session session = sessionFactory.getCurrentSession();
        CityEntity result = session.createQuery("from CityEntity where name = :name", CityEntity.class)
                .setParameter("name", "London")
                .getSingleResult();
        Assertions.assertNotNull(result);

        //test if stops are present
        Assertions.assertNotEquals(0,result.getStops().size());

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
}