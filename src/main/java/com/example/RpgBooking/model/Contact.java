package com.example.RpgBooking.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean active = false;
}
