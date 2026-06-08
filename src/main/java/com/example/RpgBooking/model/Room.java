package com.example.RpgBooking.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Data
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    private int maxPlayers;

    private int minPlayers;

    private int duration;

    private double priceAdult;

    private double priceKid;

    private boolean active = true;

    private String imageUrl;

    @Transient
    private MultipartFile imageFile;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
