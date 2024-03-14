package adp.resilience.gateway.model.dto;

import java.time.LocalDateTime;

public record BookingResponseDto(
        Long bookingId,
        Long roomId,
        String startTime,
        String endTime,
        String guestName) {
}