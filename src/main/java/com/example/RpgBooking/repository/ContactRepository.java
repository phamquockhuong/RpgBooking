package com.example.RpgBooking.repository;

import com.example.RpgBooking.model.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    @Query(value = "SELECT c FROM Contact c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "CAST(c.content AS string) LIKE CONCAT('%', :keyword, '%')",

            countQuery = "SELECT COUNT(c) FROM Contact c WHERE " +
                    "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "CAST(c.content AS string) LIKE CONCAT('%', :keyword, '%')")
    Page<Contact> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}