package org.travlyn.server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(
        exclude = {HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class},
        scanBasePackages = {"io.swagger", "org.travlyn.server.service", "org.travlyn.server.api", "org.travlyn.server.configuration"})
@EnableSwagger2
public class TravlynServer implements CommandLineRunner {

    @Override
    public void run(String... arg0) {
        if (arg0.length > 0 && arg0[0].equals("exitcode")) {
            throw new ExitException();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(TravlynServer.class, args);
    }

    static class ExitException extends RuntimeException implements ExitCodeGenerator {
        private static final long serialVersionUID = 1L;

        @Override
        public int getExitCode() {
            return 10;
        }

    }
}
