package com.example.RpgBooking.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Faq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String question;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String answer;

    private int level;

    private boolean active = true;
}
