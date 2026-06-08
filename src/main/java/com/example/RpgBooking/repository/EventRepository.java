package com.example.RpgBooking.repository;

import com.example.RpgBooking.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {}
