package com.example.RpgBooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RpgBookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(RpgBookingApplication.class, args);
	}

}
