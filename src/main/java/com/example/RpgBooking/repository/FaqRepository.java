package com.example.RpgBooking.repository;

import com.example.RpgBooking.model.Faq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {
    Page<Faq> findAll(Pageable pageable);

    long count();

    List<Faq> findByActiveTrueOrderByLevelAsc();

    Page<Faq> findByQuestionContainingIgnoreCase(String keyword, Pageable pageable);

    boolean existsByQuestion(String question);

    boolean existsByQuestionAndIdNot(String question, Long id);
}