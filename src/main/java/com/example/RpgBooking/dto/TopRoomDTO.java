package com.example.RpgBooking.dto;

public class TopRoomDTO {

    private String roomName;
    private Long bookingCount;
    private Double totalRevenue;

    public TopRoomDTO(String roomName,
                      Long bookingCount,
                      Double totalRevenue) {
        this.roomName = roomName;
        this.bookingCount = bookingCount;
        this.totalRevenue = totalRevenue;
    }

    public String getRoomName() {
        return roomName;
    }

    public Long getBookingCount() {
        return bookingCount;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }
}