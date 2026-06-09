package com.example.RpgBooking.repository;

import com.example.RpgBooking.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findFirstByActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LocalDate date1,
            LocalDate date2
    );
}
