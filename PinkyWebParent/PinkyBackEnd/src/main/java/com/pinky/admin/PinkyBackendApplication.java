package com.pinky.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.pinky.common.entity", "com.pinky.admin.user"})
public class PinkyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PinkyBackendApplication.class, args);
    }

}
