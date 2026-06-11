package com.example.RpgBooking.service;

import com.example.RpgBooking.model.Faq;
import com.example.RpgBooking.repository.FaqRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FaqService {

    private final FaqRepository faqRepository;

    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public List<Faq> getAll() {
        return faqRepository.findAll();
    }

    public List<Faq> getActiveFaqs() {
        return faqRepository.findByActiveTrueOrderByLevelAsc();
    }
}
