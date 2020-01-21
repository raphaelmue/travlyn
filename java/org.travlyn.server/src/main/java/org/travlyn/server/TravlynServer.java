package org.travlyn.server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import org.travlyn.server.db.HibernateUtil;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackages = {"io.swagger", "org.travlyn.server.api", "org.travlyn.server.configuration"})
public class TravlynServer implements CommandLineRunner {

    @Override
    public void run(String... arg0) throws IOException {
        if (arg0.length > 0 && arg0[0].equals("exitcode")) {
            throw new ExitException();
        }

        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/application.properties"));
        HibernateUtil.setDatabaseProperties(properties);
    }

    public static void main(String[] args) {
        new SpringApplication(TravlynServer.class).run(args);
    }

    static class ExitException extends RuntimeException implements ExitCodeGenerator {
        private static final long serialVersionUID = 1L;

        @Override
        public int getExitCode() {
            return 10;
        }

    }
}
