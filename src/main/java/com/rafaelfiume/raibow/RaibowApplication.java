package com.rafaelfiume.raibow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RaibowApplication {

    public static void main(String... args) throws Exception {
        SpringApplication app = new SpringApplication(RaibowApplication.class);
        app.setShowBanner(false);
        app.run(args);
    }

}
