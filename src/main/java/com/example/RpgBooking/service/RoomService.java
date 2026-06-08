package com.example.RpgBooking.service;

import com.example.RpgBooking.model.Room;
import com.example.RpgBooking.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + id));
    }

    public List<Room> getTopBookedRooms() {
        java.time.LocalDate thirtyDaysAgo = java.time.LocalDateTime.now().minusDays(30).toLocalDate();

        Pageable topSix = PageRequest.of(0, 6);

        List<Room> topRooms = roomRepository.findTopBookedRooms(thirtyDaysAgo, topSix);

        if (topRooms == null || topRooms.isEmpty()) {
            return roomRepository.findFirst6ByActiveTrue();
        }

        return topRooms;
    }

    public Page<Room> getActiveRooms(Pageable pageable) {
        return roomRepository.findByActiveTrue(pageable);
    }

    public Page<Room> getRoomsByCategoryId(Long categoryId, Pageable pageable) {
        return roomRepository.findByCategoryIdAndActiveTrue(categoryId, pageable);
    }
}