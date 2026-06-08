package com.example.RpgBooking.repository;

import com.example.RpgBooking.model.Category;
import com.example.RpgBooking.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Page<Category> findAll(Pageable pageable);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}
