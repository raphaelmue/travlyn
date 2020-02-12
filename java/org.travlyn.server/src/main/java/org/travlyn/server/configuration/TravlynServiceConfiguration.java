package org.travlyn.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.travlyn.server.service.TravlynService;

@Configuration
@EnableTransactionManagement
public class TravlynServiceConfiguration {

    @Bean
    public TravlynService travlynService() {
        return new TravlynService();
    }

}
